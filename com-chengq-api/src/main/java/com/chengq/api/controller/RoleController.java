package com.chengq.api.controller;

import com.chengq.api.entity.Role;
import com.chengq.api.model.EmptyRequest;
import com.chengq.api.model.IdRequest;
import com.chengq.api.model.base.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 角色管理：全部使用 POST + JSON Body
 */
@RequestMapping("/roles")
@Tag(name = "Role Management", description = "Role Management API")
public interface RoleController {

    @PostMapping
    @Operation(summary = "创建角色", description = "创建新角色")
    ApiResponse<Role> createRole(@RequestBody Role role);

    @PostMapping("/update")
    @Operation(summary = "更新角色", description = "更新角色信息")
    ApiResponse<Role> updateRole(@RequestBody Role role);

    @PostMapping("/delete")
    @Operation(summary = "删除角色", description = "根据 id 删除角色")
    ApiResponse<Void> deleteRole(@RequestBody IdRequest request);

    @PostMapping("/detail")
    @Operation(summary = "获取角色", description = "根据 id 查询角色")
    ApiResponse<Role> getRoleById(@RequestBody IdRequest request);

    @PostMapping("/list")
    @Operation(summary = "获取所有角色", description = "查询全部角色")
    ApiResponse<List<Role>> getAllRoles(@RequestBody(required = false) EmptyRequest request);
}
