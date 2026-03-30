package com.chengq.app.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 用户上下文工具类
 * 提供获取当前登录用户信息的统一方法
 */
public class UserContext {

    /**
     * 获取当前认证对象
     * @return 当前认证对象，如果未认证则返回null
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 判断用户是否已认证
     * @return 是否已认证
     */
    public static boolean isAuthenticated() {
        Authentication authentication = getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
            && !"anonymousUser".equals(authentication.getName());
    }

    /**
     * 获取当前登录用户名
     * @return 用户名字符串，如果未认证则返回null
     */
    public static String getCurrentUsername() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 获取当前登录用户的UserDetails对象
     * @return UserDetails对象，如果未认证则返回null
     */
    public static UserDetails getCurrentUserDetails() {
        Authentication authentication = getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前登录用户的角色列表
     * @return 角色列表，如果未认证则返回空列表
     */
    public static List<String> getCurrentUserRoles() {
        UserDetails userDetails = getCurrentUserDetails();
        if (userDetails != null) {
            return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(role -> role.substring(5)) // 去掉ROLE_前缀
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 获取当前登录用户的权限列表
     * @return 权限列表，如果未认证则返回空列表
     */
    public static List<String> getCurrentUserAuthorities() {
        UserDetails userDetails = getCurrentUserDetails();
        if (userDetails != null) {
            return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 检查当前用户是否具有指定角色
     * @param role 角色名称（不需要ROLE_前缀）
     * @return 是否具有该角色
     */
    public static boolean hasRole(String role) {
        Objects.requireNonNull(role, "角色名称不能为空");
        List<String> roles = getCurrentUserRoles();
        return roles.contains(role);
    }

    /**
     * 检查当前用户是否具有指定权限
     * @param authority 权限名称
     * @return 是否具有该权限
     */
    public static boolean hasAuthority(String authority) {
        Objects.requireNonNull(authority, "权限名称不能为空");
        List<String> authorities = getCurrentUserAuthorities();
        return authorities.contains(authority);
    }

    /**
     * 检查当前用户是否具有任一指定角色
     * @param roles 角色名称列表
     * @return 是否具有任一指定角色
     */
    public static boolean hasAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            return false;
        }
        List<String> currentRoles = getCurrentUserRoles();
        for (String role : roles) {
            if (currentRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查当前用户是否具有所有指定权限
     * @param authorities 权限名称列表
     * @return 是否具有所有指定权限
     */
    public static boolean hasAllAuthorities(String... authorities) {
        if (authorities == null || authorities.length == 0) {
            return true;
        }
        List<String> currentAuthorities = getCurrentUserAuthorities();
        for (String authority : authorities) {
            if (!currentAuthorities.contains(authority)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取当前用户的所有权限（包括角色和权限）
     * @return 所有权限列表
     */
    public static Collection<? extends GrantedAuthority> getAuthorities() {
        UserDetails userDetails = getCurrentUserDetails();
        if (userDetails != null) {
            return userDetails.getAuthorities();
        }
        return new ArrayList<>();
    }

    /**
     * 获取当前用户的认证对象的详细信息
     * @return 认证对象详细信息字符串
     */
    public static String getAuthenticationDetails() {
        Authentication authentication = getAuthentication();
        if (authentication != null) {
            return String.format("认证类型: %s, 用户名: %s, 是否已认证: %b",
                authentication.getClass().getSimpleName(),
                authentication.getName(),
                authentication.isAuthenticated());
        }
        return "未认证";
    }
}
