package com.chengq.app.implcontroller;

import com.chengq.api.controller.PermissionController;
import com.chengq.api.model.base.ApiResponse;
import com.chengq.app.service.interfaces.RoleMenuService;
import com.chengq.app.service.interfaces.UserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PermissionControllerImpl implements PermissionController {

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private UserRoleService userRoleService;

    @Override
    public ApiResponse<Void> bindRoleMenus(RoleMenuBindRequest request) {
        roleMenuService.bindRoleMenus(request.getRoleId(), request.getMenuIds(), request.getParkId());
        return ApiResponse.success(null);
    }

    @Override
    public ApiResponse<Void> bindUserRoles(UserRoleBindRequest request) {
        userRoleService.bindUserRoles(request.getUserId(), request.getRoleIds(), request.getParkId());
        return ApiResponse.success(null);
    }
}