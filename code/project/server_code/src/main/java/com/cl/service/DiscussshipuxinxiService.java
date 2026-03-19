package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.DiscussshipuxinxiEntity;
import com.cl.entity.view.DiscussshipuxinxiView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 食谱信息评论表
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:54
 */
public interface DiscussshipuxinxiService extends IService<DiscussshipuxinxiEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<DiscussshipuxinxiView> selectListView(Wrapper<DiscussshipuxinxiEntity> wrapper);

    DiscussshipuxinxiView selectView(@Param("ew") Wrapper<DiscussshipuxinxiEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<DiscussshipuxinxiEntity> wrapper);


}

