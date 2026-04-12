package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.cl.entity.StoreupEntity;
import com.cl.entity.view.StoreupView;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StoreupDao extends BaseMapper<StoreupEntity> {

    List<StoreupView> selectListView(@Param("ew") Wrapper<StoreupEntity> wrapper);

    List<StoreupView> selectListView(Pagination page, @Param("ew") Wrapper<StoreupEntity> wrapper);

    StoreupView selectView(@Param("ew") Wrapper<StoreupEntity> wrapper);

}
