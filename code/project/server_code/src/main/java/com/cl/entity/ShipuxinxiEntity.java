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
 * 食谱信息
 * 数据库通用操作实体类（普通增删改查）
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
@TableName("shipuxinxi")
public class ShipuxinxiEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 食谱名称
     */

    private String shipumingcheng;
    /**
     * 食谱图片
     */

    private String shiputupian;
    /**
     * 食谱分类
     */

    private String shipufenlei;
    /**
     * 食材
     */

    private String shicai;
    /**
     * 配料
     */

    private String peiliao;
    /**
     * 烹饪步骤
     */

    private String pengrenbuzhou;
    /**
     * 制作方式
     */

    private String zhizuofangshi;
    /**
     * 食谱介绍
     */

    private String shipujieshao;
    /**
     * 适宜人群
     */

    private String shiyirenqun;
    /**
     * 相关技巧
     */

    private String xiangguanjiqiao;
    /**
     * 最近点击时间
     */

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date clicktime;
    /**
     * 收藏数
     */

    private Integer storeupNumber;
    /**
     * 评论数
     */

    private Integer discussNumber;
    /**
     * 点击次数
     */

    private Integer clickNumber;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;

    public ShipuxinxiEntity() {

    }


    public ShipuxinxiEntity(T t) {
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
     * 获取：食谱图片
     */
    public String getShiputupian() {
        return shiputupian;
    }

    /**
     * 设置：食谱图片
     */
    public void setShiputupian(String shiputupian) {
        this.shiputupian = shiputupian;
    }

    /**
     * 获取：食谱分类
     */
    public String getShipufenlei() {
        return shipufenlei;
    }

    /**
     * 设置：食谱分类
     */
    public void setShipufenlei(String shipufenlei) {
        this.shipufenlei = shipufenlei;
    }

    /**
     * 获取：食材
     */
    public String getShicai() {
        return shicai;
    }

    /**
     * 设置：食材
     */
    public void setShicai(String shicai) {
        this.shicai = shicai;
    }

    /**
     * 获取：配料
     */
    public String getPeiliao() {
        return peiliao;
    }

    /**
     * 设置：配料
     */
    public void setPeiliao(String peiliao) {
        this.peiliao = peiliao;
    }

    /**
     * 获取：烹饪步骤
     */
    public String getPengrenbuzhou() {
        return pengrenbuzhou;
    }

    /**
     * 设置：烹饪步骤
     */
    public void setPengrenbuzhou(String pengrenbuzhou) {
        this.pengrenbuzhou = pengrenbuzhou;
    }

    /**
     * 获取：制作方式
     */
    public String getZhizuofangshi() {
        return zhizuofangshi;
    }

    /**
     * 设置：制作方式
     */
    public void setZhizuofangshi(String zhizuofangshi) {
        this.zhizuofangshi = zhizuofangshi;
    }

    /**
     * 获取：食谱介绍
     */
    public String getShipujieshao() {
        return shipujieshao;
    }

    /**
     * 设置：食谱介绍
     */
    public void setShipujieshao(String shipujieshao) {
        this.shipujieshao = shipujieshao;
    }

    /**
     * 获取：适宜人群
     */
    public String getShiyirenqun() {
        return shiyirenqun;
    }

    /**
     * 设置：适宜人群
     */
    public void setShiyirenqun(String shiyirenqun) {
        this.shiyirenqun = shiyirenqun;
    }

    /**
     * 获取：相关技巧
     */
    public String getXiangguanjiqiao() {
        return xiangguanjiqiao;
    }

    /**
     * 设置：相关技巧
     */
    public void setXiangguanjiqiao(String xiangguanjiqiao) {
        this.xiangguanjiqiao = xiangguanjiqiao;
    }

    /**
     * 获取：最近点击时间
     */
    public Date getClicktime() {
        return clicktime;
    }

    /**
     * 设置：最近点击时间
     */
    public void setClicktime(Date clicktime) {
        this.clicktime = clicktime;
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

    /**
     * 获取：评论数
     */
    public Integer getDiscussNumber() {
        return discussNumber;
    }

    /**
     * 设置：评论数
     */
    public void setDiscussNumber(Integer discussNumber) {
        this.discussNumber = discussNumber;
    }

    /**
     * 获取：点击次数
     */
    public Integer getClickNumber() {
        return clickNumber;
    }

    /**
     * 设置：点击次数
     */
    public void setClickNumber(Integer clickNumber) {
        this.clickNumber = clickNumber;
    }

}
