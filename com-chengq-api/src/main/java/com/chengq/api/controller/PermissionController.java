package com.chengq.api.controller;

import com.chengq.api.model.IdRequest;
import com.chengq.api.model.base.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 权限管理控制器接口
 */
@RequestMapping("/permissions")
@Tag(name = "Permission Management", description = "Permission Management API")
public interface PermissionController {
    
    /**
     * 绑定角色和菜单
     */
    @PostMapping("/role-menus")
    @Operation(summary = "绑定角色菜单", description = "绑定角色和菜单")
    ApiResponse<Void> bindRoleMenus(@RequestBody RoleMenuBindRequest request);
    
    /**
     * 绑定用户和角色
     */
    @PostMapping("/user-roles")
    @Operation(summary = "绑定用户角色", description = "绑定用户和角色")
    ApiResponse<Void> bindUserRoles(@RequestBody UserRoleBindRequest request);

    @PostMapping("/role-menus/detail")
    @Operation(summary = "查询角色菜单绑定", description = "返回 roleId 下当前绑定的 menuIds")
    ApiResponse<java.util.List<Long>> getRoleMenuIds(@RequestBody IdRequest request);

    @PostMapping("/user-roles/detail")
    @Operation(summary = "查询用户角色绑定", description = "返回 userId 下当前绑定的 roleIds")
    ApiResponse<java.util.List<Long>> getUserRoleIds(@RequestBody IdRequest request);
    
    /**
     * 角色菜单绑定请求
     */
    class RoleMenuBindRequest {
        private Long roleId;
        private List<Long> menuIds;
        private Long parkId;
        
        public Long getRoleId() {
            return roleId;
        }
        
        public void setRoleId(Long roleId) {
            this.roleId = roleId;
        }
        
        public List<Long> getMenuIds() {
            return menuIds;
        }
        
        public void setMenuIds(List<Long> menuIds) {
            this.menuIds = menuIds;
        }
        
        public Long getParkId() {
            return parkId;
        }
        
        public void setParkId(Long parkId) {
            this.parkId = parkId;
        }
    }
    
    /**
     * 用户角色绑定请求
     */
    class UserRoleBindRequest {
        private Long userId;
        private List<Long> roleIds;
        private Long parkId;
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public List<Long> getRoleIds() {
            return roleIds;
        }
        
        public void setRoleIds(List<Long> roleIds) {
            this.roleIds = roleIds;
        }
        
        public Long getParkId() {
            return parkId;
        }
        
        public void setParkId(Long parkId) {
            this.parkId = parkId;
        }
    }
}