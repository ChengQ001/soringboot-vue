package com.chengq.app.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chengq.api.entity.Park;
import com.chengq.api.mapper.ParkMapper;
import com.chengq.api.model.SysParkAddRequest;
import com.chengq.api.model.SysParkUpdateRequest;
import com.chengq.api.model.SysParkVO;
import com.chengq.app.service.interfaces.SysParkService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SysParkServiceImpl implements SysParkService {

    private final ParkMapper parkMapper;

    @Override
    public List<SysParkVO> listParks() {
        List<Park> parks = parkMapper.selectList(new LambdaQueryWrapper<Park>().orderByAsc(Park::getId));
        return parks.stream().map(this::toVo).collect(Collectors.toList());
    }

    @Override
    public SysParkVO addPark(SysParkAddRequest request) {
        String name = trimToNull(request.getName());
        if (!StringUtils.hasText(name)) {
            throw new RuntimeException("园区名称不能为空");
        }
        if (parkMapper.selectByName(name) != null) {
            throw new RuntimeException("园区名称已存在");
        }
        Park park = new Park();
        park.setName(name);
        park.setDescription(trimToNull(request.getDescription()));
        parkMapper.insert(park);
        return toVo(parkMapper.selectById(park.getId()));
    }

    @Override
    public SysParkVO updatePark(SysParkUpdateRequest request) {
        if (request.getId() == null) {
            throw new RuntimeException("园区ID不能为空");
        }
        Park park = parkMapper.selectById(request.getId());
        if (park == null) {
            throw new RuntimeException("园区不存在");
        }
        if (request.getName() != null) {
            String name = trimToNull(request.getName());
            if (!StringUtils.hasText(name)) {
                throw new RuntimeException("园区名称不能为空");
            }
            Park other = parkMapper.selectByName(name);
            if (other != null && !other.getId().equals(park.getId())) {
                throw new RuntimeException("园区名称已存在");
            }
            park.setName(name);
        }
        if (request.getDescription() != null) {
            park.setDescription(trimToNull(request.getDescription()));
        }
        parkMapper.updateById(park);
        return toVo(parkMapper.selectById(park.getId()));
    }

    @Override
    public void deletePark(Long id) {
        if (id == null) {
            throw new RuntimeException("园区ID不能为空");
        }
        Park park = parkMapper.selectById(id);
        if (park == null) {
            throw new RuntimeException("园区不存在");
        }
        parkMapper.deleteById(id);
    }

    private SysParkVO toVo(Park park) {
        if (park == null) return null;
        SysParkVO vo = new SysParkVO();
        vo.setId(park.getId());
        vo.setName(park.getName());
        vo.setDescription(park.getDescription());
        vo.setCreatedAt(park.getCreatedAt());
        vo.setUpdatedAt(park.getUpdatedAt());
        return vo;
    }

    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}

