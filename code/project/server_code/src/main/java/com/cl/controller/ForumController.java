package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.ForumEntity;
import com.cl.entity.view.ForumView;
import com.cl.service.ForumService;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 美食论坛
 * 后端接口
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
@RestController
@RequestMapping("/forum")
public class ForumController {
    @Autowired
    private ForumService forumService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, ForumEntity forum,
                  HttpServletRequest request) {
        if (!request.getSession().getAttribute("role").toString().equals("管理员")) {
            forum.setUserid((Long) request.getSession().getAttribute("userId"));
        }
        EntityWrapper<ForumEntity> ew = new EntityWrapper<ForumEntity>();


        PageUtils page = forumService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, forum), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ForumEntity forum,
                  HttpServletRequest request) {
        if (!request.getSession().getAttribute("role").toString().equals("管理员")) {
            forum.setUserid((Long) request.getSession().getAttribute("userId"));
        }
        EntityWrapper<ForumEntity> ew = new EntityWrapper<ForumEntity>();

        PageUtils page = forumService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, forum), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @IgnoreAuth
    @RequestMapping("/flist")
    public R flist(@RequestParam Map<String, Object> params, ForumEntity forum, HttpServletRequest request) {
        EntityWrapper<ForumEntity> ew = new EntityWrapper<ForumEntity>();
        PageUtils page = forumService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, forum), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ForumEntity forum) {
        EntityWrapper<ForumEntity> ew = new EntityWrapper<ForumEntity>();
        ew.allEq(MPUtil.allEQMapPre(forum, "forum"));
        ForumView forumView = forumService.selectView(ew);
        return R.ok("查询美食论坛成功").put("data", forumView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        ForumEntity forum = forumService.selectById(id);
        forum = forumService.selectView(new EntityWrapper<ForumEntity>().eq("id", id));
        return R.ok().put("data", forum);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        ForumEntity forum = forumService.selectById(id);
        forum = forumService.selectView(new EntityWrapper<ForumEntity>().eq("id", id));
        return R.ok().put("data", forum);
    }

    /**
     * 论坛详情
     */
    @IgnoreAuth
    @RequestMapping("/list/{id}")
    public R list(@PathVariable("id") String id) {
        ForumEntity forum = forumService.selectById(id);
        getChilds(forum);
        return R.ok().put("data", forum);
    }

    private ForumEntity getChilds(ForumEntity forum) {
        List<ForumEntity> childs = new ArrayList<ForumEntity>();
        childs = forumService.selectList(new EntityWrapper<ForumEntity>().eq("parentid", forum.getId()));
        if (childs == null || childs.size() == 0) {
            return null;
        }
        forum.setChilds(childs);
        for (ForumEntity forumEntity : childs) {
            getChilds(forumEntity);
        }
        return forum;
    }


    /**
     * 赞或踩
     */
    @RequestMapping("/thumbsup/{id}")
    public R vote(@PathVariable("id") String id, String type) {
        ForumEntity forum = forumService.selectById(id);
        if (type.equals("1")) {
            forum.setThumbsupNumber(forum.getThumbsupNumber() + 1);
        } else {
            forum.setCrazilyNumber(forum.getCrazilyNumber() + 1);
        }
        forumService.updateById(forum);
        return R.ok("投票成功");
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ForumEntity forum, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(forum);
        forum.setUserid((Long) request.getSession().getAttribute("userId"));
        forumService.insert(forum);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ForumEntity forum, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(forum);
        forum.setUserid((Long) request.getSession().getAttribute("userId"));
        forumService.insert(forum);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ForumEntity forum, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(forum);
        forumService.updateById(forum);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        forumService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


}
