package com.chengq.app.filter;

import com.chengq.api.entity.User;
import com.chengq.app.service.interfaces.IParkAccessService;
import com.chengq.app.util.ParkContext;
import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 在 JWT 之后解析 {@code X-Park-Id}，写入 {@link ParkContext}（请求线程内有效）
 */
@Component
@RequiredArgsConstructor
public class ParkContextFilter extends OncePerRequestFilter {

    private final IParkAccessService parkAccessService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            ParkContext.clear();
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof User) {
                User user = (User) auth.getPrincipal();
                String header = request.getHeader("X-Park-Id");
                Long requested = null;
                if (header != null && !header.isBlank()) {
                    try {
                        requested = Long.parseLong(header.trim());
                    } catch (NumberFormatException ignored) {
                        requested = null;
                    }
                }
                parkAccessService.resolveAndSetCurrentPark(user, requested);
            }
            filterChain.doFilter(request, response);
        } finally {
            ParkContext.clear();
        }
    }
}
