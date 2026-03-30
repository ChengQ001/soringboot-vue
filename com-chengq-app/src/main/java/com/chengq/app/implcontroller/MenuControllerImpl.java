package com.chengq.app.implcontroller;

import com.chengq.api.controller.MenuController;
import com.chengq.api.entity.Menu;
import com.chengq.api.model.EmptyRequest;
import com.chengq.api.model.IdRequest;
import com.chengq.api.model.ParentIdRequest;
import com.chengq.api.model.base.ApiResponse;
import com.chengq.app.service.interfaces.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class MenuControllerImpl implements MenuController {

    @Autowired
    private MenuService menuService;

    @Override
    public ApiResponse<Menu> createMenu(Menu menu) {
        return ApiResponse.success(menuService.createMenu(menu));
    }

    @Override
    public ApiResponse<Menu> updateMenu(Menu menu) {
        return ApiResponse.success(menuService.updateMenu(menu));
    }

    @Override
    public ApiResponse<Void> deleteMenu(IdRequest request) {
        menuService.deleteMenu(request.getId());
        return ApiResponse.success(null);
    }

    @Override
    public ApiResponse<Menu> getMenuById(IdRequest request) {
        return ApiResponse.success(menuService.getMenuById(request.getId()));
    }

    @Override
    public ApiResponse<List<Menu>> getAllMenus(EmptyRequest request) {
        return ApiResponse.success(menuService.getAllMenus());
    }

    @Override
    public ApiResponse<List<Menu>> getMenuTree(EmptyRequest request) {
        return ApiResponse.success(menuService.getMenuTree());
    }

    @Override
    public ApiResponse<List<Menu>> getRootMenus(EmptyRequest request) {
        return ApiResponse.success(menuService.getRootMenus());
    }

    @Override
    public ApiResponse<List<Menu>> getChildrenByParentId(ParentIdRequest request) {
        return ApiResponse.success(menuService.getChildrenByParentId(request.getParentId()));
    }
}
