package com.chengq.api.controller;

import com.chengq.api.model.EmptyRequest;
import com.chengq.api.model.IdRequest;
import com.chengq.api.model.SysParkAddRequest;
import com.chengq.api.model.SysParkUpdateRequest;
import com.chengq.api.model.SysParkVO;
import com.chengq.api.model.base.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 园区管理（CRUD，全部 POST）
 */
@RequestMapping("/system/parks")
@Tag(name = "System Park", description = "园区管理")
public interface SysParkController {

    @PostMapping("/list")
    @Operation(summary = "园区列表")
    ApiResponse<List<SysParkVO>> list(@RequestBody(required = false) EmptyRequest body);

    @PostMapping("/add")
    @Operation(summary = "新增园区")
    ApiResponse<SysParkVO> add(@RequestBody SysParkAddRequest request);

    @PostMapping("/update")
    @Operation(summary = "编辑园区")
    ApiResponse<SysParkVO> update(@RequestBody SysParkUpdateRequest request);

    @PostMapping("/delete")
    @Operation(summary = "删除园区")
    ApiResponse<Void> delete(@RequestBody IdRequest request);
}

