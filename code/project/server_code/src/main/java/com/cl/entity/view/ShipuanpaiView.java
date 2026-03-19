package com.cl.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.cl.entity.ShipuanpaiEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


/**
 * 食谱安排
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:54
 */
@TableName("shipuanpai")
public class ShipuanpaiView extends ShipuanpaiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public ShipuanpaiView() {
    }

    public ShipuanpaiView(ShipuanpaiEntity shipuanpaiEntity) {
        try {
            BeanUtils.copyProperties(this, shipuanpaiEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
