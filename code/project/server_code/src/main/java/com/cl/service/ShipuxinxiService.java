package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.ShipuxinxiEntity;
import com.cl.entity.view.ShipuxinxiView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 食谱信息
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
public interface ShipuxinxiService extends IService<ShipuxinxiEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ShipuxinxiView> selectListView(Wrapper<ShipuxinxiEntity> wrapper);

    ShipuxinxiView selectView(@Param("ew") Wrapper<ShipuxinxiEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<ShipuxinxiEntity> wrapper);


}

