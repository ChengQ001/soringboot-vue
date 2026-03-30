package com.chengq.api.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户实体类
 * 实现UserDetails接口以支持Spring Security
 */
@Data
@TableName("tb_user")
public class User extends BaseEntity implements UserDetails {
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 手机号（唯一）
     */
    private String phone;
    
    /**
     * 密码（加密存储）
     */
    private String password;
    
    /**
     * 用户描述
     */
    private String description;
    
    /**
     * 用户权限列表
     */
    @TableField(exist = false)
    private List<String> permissions;
    
    /**
     * 用户角色列表
     */
    @TableField(exist = false)
    private List<Role> roles;
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        if (roles != null) {
            roles.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName())));
        }
        if (permissions != null) {
            permissions.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission)));
        }
        return authorities;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}