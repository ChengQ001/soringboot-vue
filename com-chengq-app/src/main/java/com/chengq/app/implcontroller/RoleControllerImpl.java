package com.chengq.app.implcontroller;

import com.chengq.api.controller.RoleController;
import com.chengq.api.entity.Role;
import com.chengq.api.model.EmptyRequest;
import com.chengq.api.model.IdRequest;
import com.chengq.api.model.base.ApiResponse;
import com.chengq.app.service.interfaces.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class RoleControllerImpl implements RoleController {

    @Autowired
    private RoleService roleService;

    @Override
    public ApiResponse<Role> createRole(Role role) {
        return ApiResponse.success(roleService.createRole(role));
    }

    @Override
    public ApiResponse<Role> updateRole(Role role) {
        return ApiResponse.success(roleService.updateRole(role));
    }

    @Override
    public ApiResponse<Void> deleteRole(IdRequest request) {
        roleService.deleteRole(request.getId());
        return ApiResponse.success(null);
    }

    @Override
    public ApiResponse<Role> getRoleById(IdRequest request) {
        return ApiResponse.success(roleService.getRoleById(request.getId()));
    }

    @Override
    public ApiResponse<List<Role>> getAllRoles(EmptyRequest request) {
        return ApiResponse.success(roleService.getAllRoles());
    }
}
