package com.chengq.app.implcontroller;

import com.chengq.api.controller.SysUserController;
import com.chengq.api.model.EmptyRequest;
import com.chengq.api.model.IdRequest;
import com.chengq.api.model.SysUserAddRequest;
import com.chengq.api.model.SysUserUpdateRequest;
import com.chengq.api.model.SysUserVO;
import com.chengq.api.model.base.ApiResponse;
import com.chengq.app.service.interfaces.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SysUserControllerImpl implements SysUserController {

    private final SysUserService sysUserService;

    @Override
    public ApiResponse<List<SysUserVO>> list(EmptyRequest body) {
        return ApiResponse.success(sysUserService.listUsers());
    }

    @Override
    public ApiResponse<SysUserVO> add(SysUserAddRequest request) {
        return ApiResponse.success(sysUserService.addUser(request));
    }

    @Override
    public ApiResponse<SysUserVO> update(SysUserUpdateRequest request) {
        return ApiResponse.success(sysUserService.updateUser(request));
    }

    @Override
    public ApiResponse<Void> delete(IdRequest request) {
        sysUserService.deleteUser(request.getId());
        return ApiResponse.success(null);
    }
}
