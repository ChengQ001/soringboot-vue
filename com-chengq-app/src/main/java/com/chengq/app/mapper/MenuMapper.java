package com.chengq.app.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chengq.api.entity.Menu;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜单 Mapper（自定义 SQL 见 resources/mapper/MenuMapper.xml）
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> selectAllMenus();

    List<Menu> selectRootMenus();

    List<Menu> selectChildrenByParentId(Long parentId);
}
