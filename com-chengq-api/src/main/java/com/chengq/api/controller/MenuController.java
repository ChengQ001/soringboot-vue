package com.chengq.api.controller;

import com.chengq.api.entity.Menu;
import com.chengq.api.model.EmptyRequest;
import com.chengq.api.model.base.IdRequest;
import com.chengq.api.model.ParentIdRequest;
import com.chengq.api.model.base.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 菜单管理：全部使用 POST + JSON Body
 */
@RequestMapping("/menus")
@Tag(name = "Menu Management", description = "Menu Management API")
public interface MenuController {

    @PostMapping
    @Operation(summary = "创建菜单", description = "创建新菜单")
    ApiResponse<Menu> createMenu(@RequestBody Menu menu);

    @PostMapping("/update")
    @Operation(summary = "更新菜单", description = "更新菜单信息")
    ApiResponse<Menu> updateMenu(@RequestBody Menu menu);

    @PostMapping("/delete")
    @Operation(summary = "删除菜单", description = "根据 id 删除菜单")
    ApiResponse<Boolean> deleteMenu(@RequestBody IdRequest request);

    @PostMapping("/detail")
    @Operation(summary = "获取菜单", description = "根据 id 查询菜单")
    ApiResponse<Menu> getMenuById(@RequestBody IdRequest request);

    @PostMapping("/list")
    @Operation(summary = "获取所有菜单", description = "查询全部菜单")
    ApiResponse<List<Menu>> getAllMenus(@RequestBody(required = false) EmptyRequest request);

    @PostMapping("/tree")
    @Operation(summary = "获取菜单树", description = "菜单树结构")
    ApiResponse<List<Menu>> getMenuTree(@RequestBody(required = false) EmptyRequest request);

    @PostMapping("/root")
    @Operation(summary = "获取根菜单", description = "根节点菜单")
    ApiResponse<List<Menu>> getRootMenus(@RequestBody(required = false) EmptyRequest request);

    @PostMapping("/children")
    @Operation(summary = "获取子菜单", description = "根据 parentId 查询子菜单")
    ApiResponse<List<Menu>> getChildrenByParentId(@RequestBody ParentIdRequest request);
}
