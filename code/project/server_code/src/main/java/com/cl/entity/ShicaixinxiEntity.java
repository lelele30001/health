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
 * 食材信息
 * 数据库通用操作实体类（普通增删改查）
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
@TableName("shicaixinxi")
public class ShicaixinxiEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 食材名称
     */

    private String shicaimingcheng;
    /**
     * 食材图片
     */

    private String shicaitupian;
    /**
     * 食材类型
     */

    private String shicaileixing;
    /**
     * 食材功效
     */

    private String shicaigongxiao;
    /**
     * 保存方式
     */

    private String baocunfangshi;
    /**
     * 食材用途
     */

    private String shicaiyongtu;
    /**
     * 食材详情
     */

    private String shicaixiangqing;
    /**
     * 最近点击时间
     */

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date clicktime;
    /**
     * 赞
     */

    private Integer thumbsupNumber;
    /**
     * 踩
     */

    private Integer crazilyNumber;
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

    public ShicaixinxiEntity() {

    }


    public ShicaixinxiEntity(T t) {
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
     * 获取：食材名称
     */
    public String getShicaimingcheng() {
        return shicaimingcheng;
    }

    /**
     * 设置：食材名称
     */
    public void setShicaimingcheng(String shicaimingcheng) {
        this.shicaimingcheng = shicaimingcheng;
    }

    /**
     * 获取：食材图片
     */
    public String getShicaitupian() {
        return shicaitupian;
    }

    /**
     * 设置：食材图片
     */
    public void setShicaitupian(String shicaitupian) {
        this.shicaitupian = shicaitupian;
    }

    /**
     * 获取：食材类型
     */
    public String getShicaileixing() {
        return shicaileixing;
    }

    /**
     * 设置：食材类型
     */
    public void setShicaileixing(String shicaileixing) {
        this.shicaileixing = shicaileixing;
    }

    /**
     * 获取：食材功效
     */
    public String getShicaigongxiao() {
        return shicaigongxiao;
    }

    /**
     * 设置：食材功效
     */
    public void setShicaigongxiao(String shicaigongxiao) {
        this.shicaigongxiao = shicaigongxiao;
    }

    /**
     * 获取：保存方式
     */
    public String getBaocunfangshi() {
        return baocunfangshi;
    }

    /**
     * 设置：保存方式
     */
    public void setBaocunfangshi(String baocunfangshi) {
        this.baocunfangshi = baocunfangshi;
    }

    /**
     * 获取：食材用途
     */
    public String getShicaiyongtu() {
        return shicaiyongtu;
    }

    /**
     * 设置：食材用途
     */
    public void setShicaiyongtu(String shicaiyongtu) {
        this.shicaiyongtu = shicaiyongtu;
    }

    /**
     * 获取：食材详情
     */
    public String getShicaixiangqing() {
        return shicaixiangqing;
    }

    /**
     * 设置：食材详情
     */
    public void setShicaixiangqing(String shicaixiangqing) {
        this.shicaixiangqing = shicaixiangqing;
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
     * 获取：赞
     */
    public Integer getThumbsupNumber() {
        return thumbsupNumber;
    }

    /**
     * 设置：赞
     */
    public void setThumbsupNumber(Integer thumbsupNumber) {
        this.thumbsupNumber = thumbsupNumber;
    }

    /**
     * 获取：踩
     */
    public Integer getCrazilyNumber() {
        return crazilyNumber;
    }

    /**
     * 设置：踩
     */
    public void setCrazilyNumber(Integer crazilyNumber) {
        this.crazilyNumber = crazilyNumber;
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
