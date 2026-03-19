package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.ShipufenleiEntity;
import com.cl.entity.view.ShipufenleiView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 食谱分类
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
public interface ShipufenleiDao extends BaseMapper<ShipufenleiEntity> {

    List<ShipufenleiView> selectListView(@Param("ew") Wrapper<ShipufenleiEntity> wrapper);

    List<ShipufenleiView> selectListView(Pagination page, @Param("ew") Wrapper<ShipufenleiEntity> wrapper);

    ShipufenleiView selectView(@Param("ew") Wrapper<ShipufenleiEntity> wrapper);


}
