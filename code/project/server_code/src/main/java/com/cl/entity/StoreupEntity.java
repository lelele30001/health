package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 我的收藏
 */
@TableName("storeup")
public class StoreupEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    private Date addtime;

    /**
     * refid
     */
    private Long refid;

    /**
     * 表名
     */
    private String tablename;

    /**
     * 名称
     */
    private String name;

    /**
     * 图片
     */
    private String picture;

    /**
     * 类型(1:收藏,21:赞,22:踩,31:竞拍参与,41:关注)
     */
    private String type;

    /**
     * 推荐类型
     */
    private String inteltype;

    /**
     * 备注
     */
    private String remark;

    /**
     * 用户id
     */
    private Long userid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
    }

    public Long getRefid() {
        return refid;
    }

    public void setRefid(Long refid) {
        this.refid = refid;
    }

    public String getTablename() {
        return tablename;
    }

    public void setTablename(String tablename) {
        this.tablename = tablename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInteltype() {
        return inteltype;
    }

    public void setInteltype(String inteltype) {
        this.inteltype = inteltype;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }
}
