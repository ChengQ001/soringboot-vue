package com.chengq.api.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chengq.api.entity.Menu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 菜单Mapper接口
 * 继承BaseMapper以获得MyBatis Plus提供的基础CRUD操作
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
    
    @Select("SELECT * FROM tb_menu WHERE deleted = 0 ORDER BY id ASC")
    List<Menu> selectAllMenus();

    @Select("SELECT * FROM tb_menu WHERE deleted = 0 AND (parent_id IS NULL OR parent_id = 0) ORDER BY id ASC")
    List<Menu> selectRootMenus();

    @Select("SELECT * FROM tb_menu WHERE deleted = 0 AND parent_id = #{parentId} ORDER BY id ASC")
    List<Menu> selectChildrenByParentId(Long parentId);
}