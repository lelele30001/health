package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.ShicaileixingEntity;
import com.cl.entity.view.ShicaileixingView;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * 食材类型
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
public interface ShicaileixingDao extends BaseMapper<ShicaileixingEntity> {

    List<ShicaileixingView> selectListView(@Param("ew") Wrapper<ShicaileixingEntity> wrapper);

    List<ShicaileixingView> selectListView(Pagination page, @Param("ew") Wrapper<ShicaileixingEntity> wrapper);

    ShicaileixingView selectView(@Param("ew") Wrapper<ShicaileixingEntity> wrapper);


}
