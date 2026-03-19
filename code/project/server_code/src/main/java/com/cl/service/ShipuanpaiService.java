package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.ShipuanpaiEntity;
import com.cl.entity.view.ShipuanpaiView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 食谱安排
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:54
 */
public interface ShipuanpaiService extends IService<ShipuanpaiEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ShipuanpaiView> selectListView(Wrapper<ShipuanpaiEntity> wrapper);

    ShipuanpaiView selectView(@Param("ew") Wrapper<ShipuanpaiEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<ShipuanpaiEntity> wrapper);


}

