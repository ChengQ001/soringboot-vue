package com.chengq.app.config;

import com.chengq.api.model.base.ApiResponse;
import com.chengq.app.filter.JwtAuthenticationFilter;
import com.chengq.app.filter.ParkContextFilter;
import com.chengq.app.service.interfaces.IUserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 * 负责配置认证、授权、安全过滤器等安全相关功能
 */
@Configuration
@EnableWebSecurity // 启用Spring Security
@EnableMethodSecurity(securedEnabled = true) // 启用方法级安全控制（支持@PreAuthorize、@PermitAll等注解）
public class SecurityConfig {
    
    // 用户服务，用于加载用户信息
    @Autowired
    private IUserService userService;
    
    // JSON序列化工具
    @Autowired
    private ObjectMapper objectMapper;
    
    // 密码编码器
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 配置安全过滤器链
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   ParkContextFilter parkContextFilter) throws Exception {
        http
                // 禁用CSRF保护（REST API通常不需要）
                .csrf(csrf -> csrf.disable())
                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        // 白名单：不需要认证的路径
                .antMatchers("/auth/login").permitAll() // 登录接口
                .antMatchers("/auth/register").permitAll() // 注册接口
                .antMatchers("/mq/**").permitAll() // RabbitMQ测试接口
                .antMatchers("/swagger-ui/**", "/swagger/**", "/api-docs/**").permitAll() // Swagger文档
                        // 其他所有路径都需要认证
                        .anyRequest().authenticated()
                )
                // 配置会话管理
                .sessionManagement(session -> session
                        // 无状态会话，符合RESTful API设计
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 设置认证提供者
                .authenticationProvider(authenticationProvider())
                // 添加JWT认证过滤器，在UsernamePasswordAuthenticationFilter之前执行
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(parkContextFilter, JwtAuthenticationFilter.class)
                // 配置异常处理
                .exceptionHandling(exception -> exception
                        // 未认证异常处理
                        .authenticationEntryPoint(authenticationEntryPoint())
                        // 权限不足异常处理
                        .accessDeniedHandler(accessDeniedHandler())
                );
        
        return http.build();
    }
    
    /**
     * 配置认证提供者
     * 使用DaoAuthenticationProvider从UserDetailsService加载用户信息
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // 设置用户详情服务
        authProvider.setUserDetailsService(userService);
        // 设置密码编码器
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
    
    /**
     * 获取认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    
    /**
     * 认证入口点，处理未认证异常
     * 返回统一的授权失败响应
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401状态码
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(401, "未登录或登录已失效")));
        };
    }
    
    /**
     * 访问拒绝处理器，处理权限不足异常
     * 返回统一的授权失败响应
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403状态码
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.error(403, "没有权限访问该资源")));
        };
    }
}