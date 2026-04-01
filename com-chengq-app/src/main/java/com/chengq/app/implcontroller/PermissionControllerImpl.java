package com.chengq.app.implcontroller;

import com.chengq.api.controller.PermissionController;
import com.chengq.api.model.RoleMenuBindRequest;
import com.chengq.api.model.UserRoleBindRequest;
import com.chengq.api.model.UserRoleDetailRequest;
import com.chengq.api.model.base.ApiResponse;
import com.chengq.app.service.interfaces.IRoleMenuService;
import com.chengq.app.service.interfaces.IUserRoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PermissionControllerImpl implements PermissionController {

    @Autowired
    private IRoleMenuService roleMenuService;

    @Autowired
    private IUserRoleService userRoleService;

    @Override
    public ApiResponse<Boolean> bindRoleMenus(RoleMenuBindRequest request) {
        roleMenuService.bindRoleMenus(request.getRoleId(), request.getMenuIds(), request.getParkId());
        return ApiResponse.success(true);
    }

    @Override
    public ApiResponse<Boolean> bindUserRoles(UserRoleBindRequest request) {
        userRoleService.bindUserRoles(request.getUserId(), request.getRoleIds(), request.getParkId());
        return ApiResponse.success(true);
    }

    @Override
    public ApiResponse<java.util.List<Long>> getRoleMenuIds(UserRoleDetailRequest request) {
        return ApiResponse.success(roleMenuService.getMenuIdsByRoleId(request.getId(), request.getParkId()));
    }

    @Override
    public ApiResponse<java.util.List<Long>> getUserRoleIds(UserRoleDetailRequest request) {
        return ApiResponse.success(userRoleService.getRoleIdsByUserId(request.getId(), request.getParkId()));
    }
}