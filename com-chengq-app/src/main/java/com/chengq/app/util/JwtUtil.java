package com.chengq.app.util;

import com.chengq.api.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 负责JWT token的生成、验证和解析
 */
@Slf4j
@Component
public class JwtUtil {
    
    // JWT过期时间（毫秒），默认24小时
    @Value("${jwt.expiration:86400000}")
    private long expiration;
    
    // JWT签名密钥（使用HS256算法生成的安全密钥）
    private final Key signingKey;
    
    /**
     * 构造函数，初始化签名密钥
     * 使用Keys.secretKeyFor(SignatureAlgorithm.HS256)生成安全的256位密钥
     */
    public JwtUtil() {
        this.signingKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
    
    /**
     * 获取签名密钥
     */
    private Key getSigningKey() {
        return signingKey;
    }
    
    /**
     * 生成JWT token
     */
    public String generateToken(User user) {
        // 创建claims，存储用户信息
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", user.getUsername());
        claims.put("role", "USER");
        claims.put("permissions", user.getPermissions());
        
        // 构建并返回JWT token
        return Jwts.builder()
                .setClaims(claims) // 设置自定义claims
                .setSubject(user.getUsername()) // 设置subject（用户名）
                .setIssuedAt(new Date()) // 设置签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 设置过期时间
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // 使用HS256算法签名
                .compact(); // 压缩生成token字符串
    }
    
    /**
     * 解析JWT token，提取claims
     */
    public Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 设置签名密钥
                    .build() // 构建解析器
                    .parseClaimsJws(token) // 解析token
                    .getBody(); // 获取claims
        } catch (JwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * 从token中提取用户名
     */
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }
    
    /**
     * 验证token是否有效
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = extractClaims(token);
            // 检查token是否过期
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从token中提取用户角色
     */
    public String extractRole(String token) {
        return extractClaims(token).get("role", String.class);
    }
    
    /**
     * 从token中提取用户权限列表
     */
    @SuppressWarnings("unchecked")
    public java.util.List<String> extractPermissions(String token) {
        return extractClaims(token).get("permissions", java.util.List.class);
    }
}