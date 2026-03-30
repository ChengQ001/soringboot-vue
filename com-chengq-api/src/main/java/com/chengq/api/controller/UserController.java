package com.chengq.api.controller;

import com.chengq.api.model.DeleteUserRequest;
import com.chengq.api.model.UserRequest;
import com.chengq.api.model.base.ApiResponse;
import com.chengq.api.annotation.RequiresPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreFilter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

/**
 * 用户管理控制器接口
 * 包含各种Spring Security授权注解的示例
 */
@RequestMapping("/users")
@Tag(name = "User Management", description = "User Management API")
public interface UserController {

    /**
     * 1. @PreAuthorize("isAuthenticated()") - 要求用户已认证
     * 验证用户是否已登录，任何已认证的用户都可以访问此接口
     */
    @PostMapping
    @Operation(summary = "获取所有用户列表", description = "获取所有用户列表（需要用户已认证）")
    @PreAuthorize("isAuthenticated()")
    ApiResponse<List<String>> getUsers(@RequestBody UserRequest request);

    /**
     * 2. @PreAuthorize("permitAll()") - 允许所有用户访问（包括匿名用户）
     * 任何用户（包括未登录用户）都可以访问此接口
     */
    @PostMapping("/anonymous")
    @Operation(summary = "匿名访问测试", description = "允许匿名用户访问的接口")
    @PreAuthorize("permitAll()")
    ApiResponse<List<String>> getUsersAnonymous(@RequestBody UserRequest request);

    /**
     * 3. @PreAuthorize("hasRole('ADMIN')") - 要求用户具有ADMIN角色
     * 只有具有ADMIN角色的用户才能访问此接口
     */
    @PostMapping("/admin-only")
    @Operation(summary = "管理员专属接口", description = "只有管理员可以访问的接口")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<String>> getUsersAdminOnly(@RequestBody UserRequest request);

    /**
     * 4. @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')") - 要求用户具有ADMIN或MANAGER角色之一
     * 用户只要具有ADMIN或MANAGER任一角色即可访问
     */
    @PostMapping("/admin-or-manager")
    @Operation(summary = "管理员或经理访问", description = "管理员或经理角色可以访问的接口")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    ApiResponse<List<String>> getUsersAdminOrManager(@RequestBody UserRequest request);

    /**
     * 5. @PreAuthorize("hasAuthority('user:read')") - 要求用户具有特定权限
     * 用户必须具有user:read权限才能访问此接口
     */
    @PostMapping("/permission-read")
    @Operation(summary = "需要读取权限", description = "需要user:read权限才能访问")
    @PreAuthorize("hasAuthority('user:read')")
    ApiResponse<List<String>> getUsersWithReadPermission(@RequestBody UserRequest request);

    /**
     * 6. @PreAuthorize("hasAnyAuthority('user:read', 'user:write')") - 要求用户具有任一指定权限
     * 用户只要具有user:read或user:write任一权限即可访问
     */
    @PostMapping("/permission-any")
    @Operation(summary = "需要任一权限", description = "需要user:read或user:write权限之一")
    @PreAuthorize("hasAnyAuthority('user:read', 'user:write')")
    ApiResponse<List<String>> getUsersWithAnyPermission(@RequestBody UserRequest request);

    /**
     * 7. @Secured("ROLE_ADMIN") - Spring Security的Secured注解
     * 需要用户具有ADMIN角色（注意：角色名需要以ROLE_前缀开头）
     */
    @PostMapping("/secured-admin")
    @Operation(summary = "Secured注解示例", description = "使用@Secured注解要求ADMIN角色")
    @Secured("ROLE_ADMIN")
    ApiResponse<List<String>> getUsersSecuredAdmin(@RequestBody UserRequest request);

    /**
     * 8. @PreAuthorize("hasRole('ADMIN')") - Spring Security的角色授权
     * 需要用户具有ADMIN角色
     */
    @PostMapping("/roles-allowed")
    @Operation(summary = "角色授权示例", description = "使用@PreAuthorize注解要求ADMIN角色")
    @PreAuthorize("hasRole('ADMIN')")
    ApiResponse<List<String>> getUsersRolesAllowed(@RequestBody UserRequest request);

    /**
     * 9. @PreAuthorize("permitAll()") - Spring Security的允许所有访问
     * 允许所有用户访问（包括匿名用户）
     */
    @PostMapping("/permit-all")
    @Operation(summary = "允许所有访问示例", description = "使用@PreAuthorize('permitAll()')允许所有用户访问")
    @PreAuthorize("permitAll()")
    ApiResponse<List<String>> getUsersPermitAll(@RequestBody UserRequest request);

    /**
     * 10. @PreAuthorize("denyAll()") - Spring Security的拒绝所有访问
     * 拒绝所有用户访问，包括管理员
     */
    @PostMapping("/deny-all")
    @Operation(summary = "拒绝所有访问示例", description = "使用@PreAuthorize('denyAll()')拒绝所有用户访问")
    @PreAuthorize("denyAll()")
    ApiResponse<List<String>> getUsersDenyAll(@RequestBody UserRequest request);

    /**
     * 11. @PostAuthorize("returnObject.body.username == authentication.name") - 方法执行后验证
     * 方法执行完成后验证返回结果是否符合条件
     */
    @PostMapping("/post-authorize")
    @Operation(summary = "PostAuthorize注解示例", description = "方法执行后验证返回结果")
    @PreAuthorize("isAuthenticated()")
    @PostAuthorize("returnObject.body.username == authentication.name")
    ApiResponse<UserRequest> getUserPostAuthorize(@RequestBody UserRequest request);

    /**
     * 12. @PreFilter("filterObject.username != 'admin'") - 方法执行前过滤集合参数
     * 在方法执行前过滤请求参数中的集合数据
     */
    @PostMapping("/pre-filter")
    @Operation(summary = "PreFilter注解示例", description = "方法执行前过滤集合参数")
    @PreAuthorize("isAuthenticated()")
    @PreFilter("filterObject.username != 'admin'")
    ApiResponse<List<UserRequest>> filterUsersPre(@RequestBody List<UserRequest> users);

    /**
     * 13. @PostFilter("filterObject.username != 'admin'") - 方法执行后过滤返回集合
     * 在方法执行完成后过滤返回结果中的集合数据
     */
    @PostMapping("/post-filter")
    @Operation(summary = "PostFilter注解示例", description = "方法执行后过滤返回集合")
    @PreAuthorize("isAuthenticated()")
    @PostFilter("filterObject != 'admin'")
    ApiResponse<List<String>> filterUsersPost(@RequestBody UserRequest request);

    /**
     * 14. @AuthenticationPrincipal - 获取当前认证用户信息
     * 获取当前登录用户的UserDetails对象
     */
    @PostMapping("/current-user")
    @Operation(summary = "获取当前用户", description = "获取当前登录用户信息")
    @PreAuthorize("isAuthenticated()")
    ApiResponse<UserDetails> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails);

    /**
     * 15. @PreAuthorize结合方法参数 - 基于方法参数进行授权
     * 根据请求参数中的userId与当前用户ID进行比较
     */
    @PostMapping("/user/{userId}")
    @Operation(summary = "获取指定用户信息", description = "用户只能查看自己的信息")
    @PreAuthorize("#userId == authentication.name")
    ApiResponse<UserRequest> getUserById(@RequestBody UserRequest request);

    /**
     * 16. 自定义@RequiresPermission注解 - 项目自定义的权限注解
     * 使用自定义注解标记需要特定权限的方法
     */
    @PostMapping("/custom-permission")
    @Operation(summary = "自定义权限注解示例", description = "使用自定义@RequiresPermission注解")
    @RequiresPermission("user:custom")
    ApiResponse<String> getUsersWithCustomPermission(@RequestBody UserRequest request);

    /**
     * 17. 创建新用户 - 示例：需要user:write权限
     */
    @PostMapping("/create")
    @Operation(summary = "创建用户", description = "创建新用户（需要user:write权限）")
    @PreAuthorize("hasAuthority('user:write')")
    ApiResponse<String> createUser(@RequestBody UserRequest request);

    /**
     * 18. 删除用户 - 示例：需要user:delete权限
     */
    @PostMapping("/delete")
    @Operation(summary = "删除用户", description = "删除用户（需要user:delete权限）")
    @PreAuthorize("hasAuthority('user:delete')")
    ApiResponse<String> deleteUser(@RequestBody DeleteUserRequest request);
    

}