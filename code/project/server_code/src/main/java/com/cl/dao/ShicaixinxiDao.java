package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.ShicaixinxiEntity;
import com.cl.entity.view.ShicaixinxiView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 食材信息
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
public interface ShicaixinxiDao extends BaseMapper<ShicaixinxiEntity> {

    List<ShicaixinxiView> selectListView(@Param("ew") Wrapper<ShicaixinxiEntity> wrapper);

    List<ShicaixinxiView> selectListView(Pagination page, @Param("ew") Wrapper<ShicaixinxiEntity> wrapper);

    ShicaixinxiView selectView(@Param("ew") Wrapper<ShicaixinxiEntity> wrapper);


}
