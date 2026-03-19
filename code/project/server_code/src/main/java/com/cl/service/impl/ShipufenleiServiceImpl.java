package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.ShipufenleiDao;
import com.cl.entity.ShipufenleiEntity;
import com.cl.entity.view.ShipufenleiView;
import com.cl.service.ShipufenleiService;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("shipufenleiService")
public class ShipufenleiServiceImpl extends ServiceImpl<ShipufenleiDao, ShipufenleiEntity> implements ShipufenleiService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<ShipufenleiEntity> page = this.selectPage(
                new Query<ShipufenleiEntity>(params).getPage(),
                new EntityWrapper<ShipufenleiEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<ShipufenleiEntity> wrapper) {
        Page<ShipufenleiView> page = new Query<ShipufenleiView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<ShipufenleiView> selectListView(Wrapper<ShipufenleiEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public ShipufenleiView selectView(Wrapper<ShipufenleiEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }


}
