package com.chengq.app.implcontroller;

import com.chengq.api.controller.SysParkController;
import com.chengq.api.model.EmptyRequest;
import com.chengq.api.model.base.IdRequest;
import com.chengq.api.model.SysParkAddRequest;
import com.chengq.api.model.SysParkUpdateRequest;
import com.chengq.api.model.SysParkVO;
import com.chengq.api.model.base.ApiResponse;
import com.chengq.app.service.interfaces.ISysParkService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SysParkControllerImpl implements SysParkController {

    private final ISysParkService sysParkService;

    @Override
    public ApiResponse<List<SysParkVO>> list(EmptyRequest body) {
        return ApiResponse.success(sysParkService.listParks());
    }

    @Override
    public ApiResponse<SysParkVO> add(SysParkAddRequest request) {
        return ApiResponse.success(sysParkService.addPark(request));
    }

    @Override
    public ApiResponse<SysParkVO> update(SysParkUpdateRequest request) {
        return ApiResponse.success(sysParkService.updatePark(request));
    }

    @Override
    public ApiResponse<Boolean> delete(IdRequest request) {
        sysParkService.deletePark(request.getId());
        return ApiResponse.success(true);
    }
}

