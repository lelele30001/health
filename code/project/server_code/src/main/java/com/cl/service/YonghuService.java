package com.cl.service;

import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.service.IService;
import com.cl.entity.YonghuEntity;
import com.cl.entity.view.YonghuView;
import com.cl.utils.PageUtils;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


/**
 * 用户
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
public interface YonghuService extends IService<YonghuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<YonghuView> selectListView(Wrapper<YonghuEntity> wrapper);

    YonghuView selectView(@Param("ew") Wrapper<YonghuEntity> wrapper);

    PageUtils queryPage(Map<String, Object> params, Wrapper<YonghuEntity> wrapper);


}

