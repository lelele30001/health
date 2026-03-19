package com.cl.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.cl.entity.ShicaixinxiEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


/**
 * 食材信息
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
@TableName("shicaixinxi")
public class ShicaixinxiView extends ShicaixinxiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public ShicaixinxiView() {
    }

    public ShicaixinxiView(ShicaixinxiEntity shicaixinxiEntity) {
        try {
            BeanUtils.copyProperties(this, shicaixinxiEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
