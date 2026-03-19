package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.DiscussshipuxinxiEntity;
import com.cl.entity.view.DiscussshipuxinxiView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 食谱信息评论表
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:54
 */
public interface DiscussshipuxinxiDao extends BaseMapper<DiscussshipuxinxiEntity> {

    List<DiscussshipuxinxiView> selectListView(@Param("ew") Wrapper<DiscussshipuxinxiEntity> wrapper);

    List<DiscussshipuxinxiView> selectListView(Pagination page, @Param("ew") Wrapper<DiscussshipuxinxiEntity> wrapper);

    DiscussshipuxinxiView selectView(@Param("ew") Wrapper<DiscussshipuxinxiEntity> wrapper);


}
