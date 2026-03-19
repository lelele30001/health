package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.ShipuxinxiDao;
import com.cl.entity.ShipuxinxiEntity;
import com.cl.entity.view.ShipuxinxiView;
import com.cl.service.ShipuxinxiService;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("shipuxinxiService")
public class ShipuxinxiServiceImpl extends ServiceImpl<ShipuxinxiDao, ShipuxinxiEntity> implements ShipuxinxiService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ShipuxinxiEntity> page = this.selectPage(
                new Query<ShipuxinxiEntity>(params).getPage(),
                new EntityWrapper<ShipuxinxiEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<ShipuxinxiEntity> wrapper) {
        Page<ShipuxinxiView> page = new Query<ShipuxinxiView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<ShipuxinxiView> selectListView(Wrapper<ShipuxinxiEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public ShipuxinxiView selectView(Wrapper<ShipuxinxiEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }


}
