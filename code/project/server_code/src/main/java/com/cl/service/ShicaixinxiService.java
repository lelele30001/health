package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.ShicaixinxiEntity;
import com.cl.entity.view.ShicaixinxiView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 食材信息
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
public interface ShicaixinxiService extends IService<ShicaixinxiEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ShicaixinxiView> selectListView(Wrapper<ShicaixinxiEntity> wrapper);

    ShicaixinxiView selectView(@Param("ew") Wrapper<ShicaixinxiEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<ShicaixinxiEntity> wrapper);


}

