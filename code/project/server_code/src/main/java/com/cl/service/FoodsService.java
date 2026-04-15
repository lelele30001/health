package com.cl.service;

import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.FoodsEntity;
import com.cl.utils.PageUtils;

import java.util.Map;

/**
 * 食材服务
 */
public interface FoodsService extends IService<FoodsEntity> {
    PageUtils queryPage(Map<String, Object> params);
    PageUtils queryPage(Map<String, Object> params, com.baomidou.mybatisplus.mapper.Wrapper<FoodsEntity> wrapper);
}
