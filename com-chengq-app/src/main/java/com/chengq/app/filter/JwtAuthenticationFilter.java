package com.chengq.app.filter;

import com.chengq.app.service.interfaces.UserService;
import com.chengq.app.util.JwtUtil;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 * 负责从请求头中提取JWT token，验证token有效性，并将用户信息设置到SecurityContext中
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    // JWT工具类，用于token生成和验证
    private final JwtUtil jwtUtil;
    // 用户服务，用于加载用户信息
    private final UserService userService;
    
    /**
     * 构造函数注入依赖
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }
    
    /**
     * 过滤方法，每个请求都会执行一次
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            // 从请求头中提取JWT token
            String token = extractToken(request);
            
            // 如果token存在且有效
            if (token != null && jwtUtil.isTokenValid(token)) {
                // 从token中提取用户名
                String username = jwtUtil.extractUsername(token);
                // 根据用户名加载用户详情
                UserDetails userDetails = userService.loadUserByUsername(username);
                
                // 如果用户存在
                if (userDetails != null) {
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    // 设置认证详情
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // 将认证对象设置到SecurityContext中
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("User authenticated: {}, authorities: {}", username, userDetails.getAuthorities());
                }
            }
        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage());
            // 认证失败，清空SecurityContext
            SecurityContextHolder.clearContext();
        }
        
        // 继续执行后续过滤器
        chain.doFilter(request, response);
    }
    
    /**
     * 从请求头中提取JWT token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // 检查Authorization头是否以Bearer开头
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // 提取token部分（去掉"Bearer "前缀）
            return bearerToken.substring(7);
        }
        // 如果没有Bearer前缀，直接返回token
        return bearerToken;
    }
}