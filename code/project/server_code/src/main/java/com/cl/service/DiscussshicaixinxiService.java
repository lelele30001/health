package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.DiscussshicaixinxiEntity;
import com.cl.entity.view.DiscussshicaixinxiView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 食材信息评论表
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:54
 */
public interface DiscussshicaixinxiService extends IService<DiscussshicaixinxiEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<DiscussshicaixinxiView> selectListView(Wrapper<DiscussshicaixinxiEntity> wrapper);

    DiscussshicaixinxiView selectView(@Param("ew") Wrapper<DiscussshicaixinxiEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<DiscussshicaixinxiEntity> wrapper);


}

