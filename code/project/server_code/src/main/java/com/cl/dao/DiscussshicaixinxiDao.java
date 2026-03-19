package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.DiscussshicaixinxiEntity;
import com.cl.entity.view.DiscussshicaixinxiView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 食材信息评论表
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:54
 */
public interface DiscussshicaixinxiDao extends BaseMapper<DiscussshicaixinxiEntity> {

    List<DiscussshicaixinxiView> selectListView(@Param("ew") Wrapper<DiscussshicaixinxiEntity> wrapper);

    List<DiscussshicaixinxiView> selectListView(Pagination page, @Param("ew") Wrapper<DiscussshicaixinxiEntity> wrapper);

    DiscussshicaixinxiView selectView(@Param("ew") Wrapper<DiscussshicaixinxiEntity> wrapper);


}
