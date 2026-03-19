package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.ShipuanpaiEntity;
import com.cl.entity.view.ShipuanpaiView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 食谱安排
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:54
 */
public interface ShipuanpaiDao extends BaseMapper<ShipuanpaiEntity> {

    List<ShipuanpaiView> selectListView(@Param("ew") Wrapper<ShipuanpaiEntity> wrapper);

    List<ShipuanpaiView> selectListView(Pagination page, @Param("ew") Wrapper<ShipuanpaiEntity> wrapper);

    ShipuanpaiView selectView(@Param("ew") Wrapper<ShipuanpaiEntity> wrapper);


}
