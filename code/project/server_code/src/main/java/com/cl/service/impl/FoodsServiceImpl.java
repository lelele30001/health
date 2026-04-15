package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.FoodsDao;
import com.cl.entity.FoodsEntity;
import com.cl.service.FoodsService;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;

import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 食材服务实现
 */
@Service("foodsService")
public class FoodsServiceImpl extends ServiceImpl<FoodsDao, FoodsEntity> implements FoodsService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<FoodsEntity> page = this.selectPage(
                new Query<FoodsEntity>(params).getPage(),
                new EntityWrapper<FoodsEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, com.baomidou.mybatisplus.mapper.Wrapper<FoodsEntity> wrapper) {
        Page<FoodsEntity> page = this.selectPage(
                new Query<FoodsEntity>(params).getPage(),
                wrapper
        );
        return new PageUtils(page);
    }
}
