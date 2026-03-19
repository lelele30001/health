package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.ShicaileixingDao;
import com.cl.entity.ShicaileixingEntity;
import com.cl.entity.view.ShicaileixingView;
import com.cl.service.ShicaileixingService;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("shicaileixingService")
public class ShicaileixingServiceImpl extends ServiceImpl<ShicaileixingDao, ShicaileixingEntity> implements ShicaileixingService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ShicaileixingEntity> page = this.selectPage(
                new Query<ShicaileixingEntity>(params).getPage(),
                new EntityWrapper<ShicaileixingEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<ShicaileixingEntity> wrapper) {
        Page<ShicaileixingView> page = new Query<ShicaileixingView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<ShicaileixingView> selectListView(Wrapper<ShicaileixingEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public ShicaileixingView selectView(Wrapper<ShicaileixingEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }


}
