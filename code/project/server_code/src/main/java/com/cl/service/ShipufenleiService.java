package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.ShipufenleiEntity;
import com.cl.entity.view.ShipufenleiView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 食谱分类
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
public interface ShipufenleiService extends IService<ShipufenleiEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ShipufenleiView> selectListView(Wrapper<ShipufenleiEntity> wrapper);

    ShipufenleiView selectView(@Param("ew") Wrapper<ShipufenleiEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<ShipufenleiEntity> wrapper);


}

