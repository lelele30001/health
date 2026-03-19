package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;


/**
 * 食谱安排
 * 数据库通用操作实体类（普通增删改查）
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:54
 */
@TableName("shipuanpai")
public class ShipuanpaiEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 日期
     */

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat
    private Date riqi;
    /**
     * 食谱名称
     */

    private String shipumingcheng;
    /**
     * 封面
     */

    private String fengmian;
    /**
     * 详情
     */

    private String xiangqing;
    /**
     * 收藏数
     */

    private Integer storeupNumber;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public ShipuanpaiEntity() {

    }


    public ShipuanpaiEntity(T t) {
        try {
            BeanUtils.copyProperties(this, t);
        } catch (IllegalAccessException | InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取：日期
     */
    public Date getRiqi() {
        return riqi;
    }

    /**
     * 设置：日期
     */
    public void setRiqi(Date riqi) {
        this.riqi = riqi;
    }

    /**
     * 获取：食谱名称
     */
    public String getShipumingcheng() {
        return shipumingcheng;
    }

    /**
     * 设置：食谱名称
     */
    public void setShipumingcheng(String shipumingcheng) {
        this.shipumingcheng = shipumingcheng;
    }

    /**
     * 获取：封面
     */
    public String getFengmian() {
        return fengmian;
    }

    /**
     * 设置：封面
     */
    public void setFengmian(String fengmian) {
        this.fengmian = fengmian;
    }

    /**
     * 获取：详情
     */
    public String getXiangqing() {
        return xiangqing;
    }

    /**
     * 设置：详情
     */
    public void setXiangqing(String xiangqing) {
        this.xiangqing = xiangqing;
    }

    /**
     * 获取：收藏数
     */
    public Integer getStoreupNumber() {
        return storeupNumber;
    }

    /**
     * 设置：收藏数
     */
    public void setStoreupNumber(Integer storeupNumber) {
        this.storeupNumber = storeupNumber;
    }

}
