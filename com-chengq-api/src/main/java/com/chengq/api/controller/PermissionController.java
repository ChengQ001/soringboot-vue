package com.chengq.api.controller;

import com.chengq.api.model.RoleMenuBindRequest;
import com.chengq.api.model.UserRoleBindRequest;
import com.chengq.api.model.UserRoleDetailRequest;
import com.chengq.api.model.base.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
    @Operation(summary = "绑定角色菜单", description = "非 ADMIN 角色按园区写入 tb_role_menu；ADMIN 拥有全量菜单，不允许调用本接口修改绑定")
    ApiResponse<Boolean> bindRoleMenus(@RequestBody RoleMenuBindRequest request);

    /**
     * 绑定用户和角色
     */
    @PostMapping("/user-roles")
    @Operation(summary = "绑定用户角色", description = "绑定用户和角色")
    ApiResponse<Boolean> bindUserRoles(@RequestBody UserRoleBindRequest request);

    @PostMapping("/role-menus/detail")
    @Operation(summary = "查询角色菜单绑定", description = "id 为 roleId；须指定 parkId。ADMIN 角色返回全部菜单 id（仅展示用，不持久化依赖）；其它角色：本园区有绑定则仅返回本园区，否则返回 park_id 为 NULL 的全局绑定")
    ApiResponse<java.util.List<Long>> getRoleMenuIds(@RequestBody UserRoleDetailRequest request);

    @PostMapping("/user-roles/detail")
    @Operation(summary = "查询用户角色绑定", description = "返回指定用户、指定园区下绑定的 roleIds；parkId 为空时仅查询 tb_user_role.park_id 为 NULL 的行")
    ApiResponse<java.util.List<Long>> getUserRoleIds(@RequestBody UserRoleDetailRequest request);
}
