package com.chengq.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chengq.api.entity.Park;
import org.apache.ibatis.annotations.Mapper;

/**
 * 园区 Mapper（自定义 SQL 见 resources/mapper/ParkMapper.xml）
 */
@Mapper
public interface ParkMapper extends BaseMapper<Park> {

    Park selectByName(String name);
}
