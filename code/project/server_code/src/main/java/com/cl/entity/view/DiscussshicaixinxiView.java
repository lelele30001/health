package com.cl.entity.view;

import com.baomidou.mybatisplus.annotations.TableName;
import com.cl.entity.DiscussshicaixinxiEntity;
import org.apache.commons.beanutils.BeanUtils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;


/**
 * 食材信息评论表
 * 后端返回视图实体辅助类
 * （通常后端关联的表或者自定义的字段需要返回使用）
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:54
 */
@TableName("discussshicaixinxi")
public class DiscussshicaixinxiView extends DiscussshicaixinxiEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    public DiscussshicaixinxiView() {
    }

    public DiscussshicaixinxiView(DiscussshicaixinxiEntity discussshicaixinxiEntity) {
        try {
            BeanUtils.copyProperties(this, discussshicaixinxiEntity);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
