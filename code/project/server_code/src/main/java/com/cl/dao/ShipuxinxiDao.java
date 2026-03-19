package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.ShipuxinxiEntity;
import com.cl.entity.view.ShipuxinxiView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 食谱信息
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
public interface ShipuxinxiDao extends BaseMapper<ShipuxinxiEntity> {

    List<ShipuxinxiView> selectListView(@Param("ew") Wrapper<ShipuxinxiEntity> wrapper);

    List<ShipuxinxiView> selectListView(Pagination page, @Param("ew") Wrapper<ShipuxinxiEntity> wrapper);

    ShipuxinxiView selectView(@Param("ew") Wrapper<ShipuxinxiEntity> wrapper);


}
