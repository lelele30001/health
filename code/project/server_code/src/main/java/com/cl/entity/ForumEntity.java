package com.cl.entity;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;


/**
 * 美食论坛
 * 数据库通用操作实体类（普通增删改查）
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
@TableName("forum")
public class ForumEntity<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * 帖子标题
     */

    private String title;
    /**
     * 帖子内容
     */

    private String content;
    /**
     * 父节点id
     */

    private Long parentid;
    /**
     * 用户id
     */

    private Long userid;
    /**
     * 用户名
     */

    private String username;
    /**
     * 头像
     */

    private String avatarurl;
    /**
     * 状态
     */

    private String isdone;
    /**
     * 是否置顶
     */

    private Integer isTop;
    /**
     * 置顶时间
     */

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date topTime;
    /**
     * 赞
     */

    private Integer thumbsupNumber;
    /**
     * 踩
     */

    private Integer crazilyNumber;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat
    private Date addtime;
    @TableField(exist = false)
    private List<ForumEntity> childs;


    public ForumEntity() {

    }

    public ForumEntity(T t) {
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

    public List<ForumEntity> getChilds() {
        return childs;
    }

    public void setChilds(List<ForumEntity> childs) {
        this.childs = childs;
    }

    /**
     * 获取：帖子标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置：帖子标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取：帖子内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 设置：帖子内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 获取：父节点id
     */
    public Long getParentid() {
        return parentid;
    }

    /**
     * 设置：父节点id
     */
    public void setParentid(Long parentid) {
        this.parentid = parentid;
    }

    /**
     * 获取：用户id
     */
    public Long getUserid() {
        return userid;
    }

    /**
     * 设置：用户id
     */
    public void setUserid(Long userid) {
        this.userid = userid;
    }

    /**
     * 获取：用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置：用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取：头像
     */
    public String getAvatarurl() {
        return avatarurl;
    }

    /**
     * 设置：头像
     */
    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl;
    }

    /**
     * 获取：状态
     */
    public String getIsdone() {
        return isdone;
    }

    /**
     * 设置：状态
     */
    public void setIsdone(String isdone) {
        this.isdone = isdone;
    }

    /**
     * 获取：是否置顶
     */
    public Integer getIsTop() {
        return isTop;
    }

    /**
     * 设置：是否置顶
     */
    public void setIsTop(Integer isTop) {
        this.isTop = isTop;
    }

    /**
     * 获取：置顶时间
     */
    public Date getTopTime() {
        return topTime;
    }

    /**
     * 设置：置顶时间
     */
    public void setTopTime(Date topTime) {
        this.topTime = topTime;
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

}
