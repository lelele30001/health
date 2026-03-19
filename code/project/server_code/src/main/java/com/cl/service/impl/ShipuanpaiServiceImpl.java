package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.ShipuanpaiDao;
import com.cl.entity.ShipuanpaiEntity;
import com.cl.entity.view.ShipuanpaiView;
import com.cl.service.ShipuanpaiService;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("shipuanpaiService")
public class ShipuanpaiServiceImpl extends ServiceImpl<ShipuanpaiDao, ShipuanpaiEntity> implements ShipuanpaiService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ShipuanpaiEntity> page = this.selectPage(
                new Query<ShipuanpaiEntity>(params).getPage(),
                new EntityWrapper<ShipuanpaiEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<ShipuanpaiEntity> wrapper) {
        Page<ShipuanpaiView> page = new Query<ShipuanpaiView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<ShipuanpaiView> selectListView(Wrapper<ShipuanpaiEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public ShipuanpaiView selectView(Wrapper<ShipuanpaiEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }


}
