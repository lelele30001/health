package com.cl.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.cl.entity.ShipufenleiEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


/**
 * 食谱分类
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
@TableName("shipufenlei")
public class ShipufenleiView extends ShipufenleiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public ShipufenleiView() {
    }

    public ShipufenleiView(ShipufenleiEntity shipufenleiEntity) {
        try {
            BeanUtils.copyProperties(this, shipufenleiEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
