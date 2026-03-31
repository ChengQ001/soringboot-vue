package com.chengq.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chengq.api.entity.Park;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ParkMapper extends BaseMapper<Park> {

    @Select("SELECT * FROM tb_park WHERE name = #{name} AND deleted = 0 LIMIT 1")
    Park selectByName(String name);
}

