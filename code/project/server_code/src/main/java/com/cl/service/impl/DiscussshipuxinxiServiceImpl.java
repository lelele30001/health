package com.cl.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.cl.dao.DiscussshipuxinxiDao;
import com.cl.entity.DiscussshipuxinxiEntity;
import com.cl.entity.view.DiscussshipuxinxiView;
import com.cl.service.DiscussshipuxinxiService;
import com.cl.utils.PageUtils;
import com.cl.utils.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("discussshipuxinxiService")
public class DiscussshipuxinxiServiceImpl extends ServiceImpl<DiscussshipuxinxiDao, DiscussshipuxinxiEntity> implements DiscussshipuxinxiService {


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Page<DiscussshipuxinxiEntity> page = this.selectPage(
                new Query<DiscussshipuxinxiEntity>(params).getPage(),
                new EntityWrapper<DiscussshipuxinxiEntity>()
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Wrapper<DiscussshipuxinxiEntity> wrapper) {
        Page<DiscussshipuxinxiView> page = new Query<DiscussshipuxinxiView>(params).getPage();
        page.setRecords(baseMapper.selectListView(page, wrapper));
        PageUtils pageUtil = new PageUtils(page);
        return pageUtil;
    }

    @Override
    public List<DiscussshipuxinxiView> selectListView(Wrapper<DiscussshipuxinxiEntity> wrapper) {
        return baseMapper.selectListView(wrapper);
    }

    @Override
    public DiscussshipuxinxiView selectView(Wrapper<DiscussshipuxinxiEntity> wrapper) {
        return baseMapper.selectView(wrapper);
    }


}
