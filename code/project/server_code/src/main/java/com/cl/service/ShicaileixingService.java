package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.ShicaileixingEntity;
import com.cl.entity.view.ShicaileixingView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 食材类型
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
public interface ShicaileixingService extends IService<ShicaileixingEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ShicaileixingView> selectListView(Wrapper<ShicaileixingEntity> wrapper);

    ShicaileixingView selectView(@Param("ew") Wrapper<ShicaileixingEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<ShicaileixingEntity> wrapper);


}

