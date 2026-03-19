package com.cl.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.cl.entity.UserProfileEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 用户档案DAO
 */
@Mapper
public interface UserProfileDao extends BaseMapper<UserProfileEntity> {
    
    UserProfileEntity getByUserId(@Param("userId") Long userId);
}
