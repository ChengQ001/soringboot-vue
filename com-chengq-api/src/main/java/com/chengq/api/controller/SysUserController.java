package com.chengq.api.controller;

import com.chengq.api.model.EmptyRequest;
import com.chengq.api.model.IdRequest;
import com.chengq.api.model.SysUserAddRequest;
import com.chengq.api.model.SysUserUpdateRequest;
import com.chengq.api.model.SysUserVO;
import com.chengq.api.model.base.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 系统用户管理（CRUD，全部 POST）
 */
@RequestMapping("/system/users")
@Tag(name = "System User", description = "系统用户管理")
public interface SysUserController {

    @PostMapping("/list")
    @Operation(summary = "用户列表")
    ApiResponse<List<SysUserVO>> list(@RequestBody(required = false) EmptyRequest body);

    @PostMapping("/add")
    @Operation(summary = "新增用户", description = "手机号唯一")
    ApiResponse<SysUserVO> add(@RequestBody SysUserAddRequest request);

    @PostMapping("/update")
    @Operation(summary = "编辑用户")
    ApiResponse<SysUserVO> update(@RequestBody SysUserUpdateRequest request);

    @PostMapping("/delete")
    @Operation(summary = "删除用户")
    ApiResponse<Boolean> delete(@RequestBody IdRequest request);
}
