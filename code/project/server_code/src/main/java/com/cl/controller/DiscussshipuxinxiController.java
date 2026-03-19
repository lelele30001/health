package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.DiscussshipuxinxiEntity;
import com.cl.entity.view.DiscussshipuxinxiView;
import com.cl.service.DiscussshipuxinxiService;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 食谱信息评论表
 * 后端接口
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:54
 */
@RestController
@RequestMapping("/discussshipuxinxi")
public class DiscussshipuxinxiController {
    @Autowired
    private DiscussshipuxinxiService discussshipuxinxiService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, DiscussshipuxinxiEntity discussshipuxinxi,
                  HttpServletRequest request) {
        EntityWrapper<DiscussshipuxinxiEntity> ew = new EntityWrapper<DiscussshipuxinxiEntity>();


        PageUtils page = discussshipuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussshipuxinxi), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, DiscussshipuxinxiEntity discussshipuxinxi,
                  HttpServletRequest request) {
        EntityWrapper<DiscussshipuxinxiEntity> ew = new EntityWrapper<DiscussshipuxinxiEntity>();

        PageUtils page = discussshipuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussshipuxinxi), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(DiscussshipuxinxiEntity discussshipuxinxi) {
        EntityWrapper<DiscussshipuxinxiEntity> ew = new EntityWrapper<DiscussshipuxinxiEntity>();
        ew.allEq(MPUtil.allEQMapPre(discussshipuxinxi, "discussshipuxinxi"));
        return R.ok().put("data", discussshipuxinxiService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(DiscussshipuxinxiEntity discussshipuxinxi) {
        EntityWrapper<DiscussshipuxinxiEntity> ew = new EntityWrapper<DiscussshipuxinxiEntity>();
        ew.allEq(MPUtil.allEQMapPre(discussshipuxinxi, "discussshipuxinxi"));
        DiscussshipuxinxiView discussshipuxinxiView = discussshipuxinxiService.selectView(ew);
        return R.ok("查询食谱信息评论表成功").put("data", discussshipuxinxiView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        DiscussshipuxinxiEntity discussshipuxinxi = discussshipuxinxiService.selectById(id);
        discussshipuxinxi = discussshipuxinxiService.selectView(new EntityWrapper<DiscussshipuxinxiEntity>().eq("id", id));
        return R.ok().put("data", discussshipuxinxi);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        DiscussshipuxinxiEntity discussshipuxinxi = discussshipuxinxiService.selectById(id);
        discussshipuxinxi = discussshipuxinxiService.selectView(new EntityWrapper<DiscussshipuxinxiEntity>().eq("id", id));
        return R.ok().put("data", discussshipuxinxi);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DiscussshipuxinxiEntity discussshipuxinxi, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(discussshipuxinxi);
        discussshipuxinxiService.insert(discussshipuxinxi);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody DiscussshipuxinxiEntity discussshipuxinxi, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(discussshipuxinxi);
        discussshipuxinxiService.insert(discussshipuxinxi);
        return R.ok();
    }

    /**
     * 获取用户密保
     */
    @RequestMapping("/security")
    @IgnoreAuth
    public R security(@RequestParam String username) {
        DiscussshipuxinxiEntity discussshipuxinxi = discussshipuxinxiService.selectOne(new EntityWrapper<DiscussshipuxinxiEntity>().eq("", username));
        return R.ok().put("data", discussshipuxinxi);
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    @IgnoreAuth
    public R update(@RequestBody DiscussshipuxinxiEntity discussshipuxinxi, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(discussshipuxinxi);
        discussshipuxinxiService.updateById(discussshipuxinxi);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        discussshipuxinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
     * 前端智能排序
     */
    @IgnoreAuth
    @RequestMapping("/autoSort")
    public R autoSort(@RequestParam Map<String, Object> params, DiscussshipuxinxiEntity discussshipuxinxi, HttpServletRequest request, String pre) {
        EntityWrapper<DiscussshipuxinxiEntity> ew = new EntityWrapper<DiscussshipuxinxiEntity>();
        Map<String, Object> newMap = new HashMap<String, Object>();
        Map<String, Object> param = new HashMap<String, Object>();
        Iterator<Map.Entry<String, Object>> it = param.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Object> entry = it.next();
            String key = entry.getKey();
            String newKey = entry.getKey();
            if (pre.endsWith(".")) {
                newMap.put(pre + newKey, entry.getValue());
            } else if (StringUtils.isEmpty(pre)) {
                newMap.put(newKey, entry.getValue());
            } else {
                newMap.put(pre + "." + newKey, entry.getValue());
            }
        }
        params.put("sort", "clicktime");
        params.put("order", "desc");
        PageUtils page = discussshipuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussshipuxinxi), params), params));
        return R.ok().put("data", page);
    }


}
