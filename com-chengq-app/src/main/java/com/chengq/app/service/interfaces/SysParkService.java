package com.chengq.app.service.interfaces;

import com.chengq.api.model.SysParkAddRequest;
import com.chengq.api.model.SysParkUpdateRequest;
import com.chengq.api.model.SysParkVO;

import java.util.List;

public interface SysParkService {

    List<SysParkVO> listParks();

    SysParkVO addPark(SysParkAddRequest request);

    SysParkVO updatePark(SysParkUpdateRequest request);

    void deletePark(Long id);
}

