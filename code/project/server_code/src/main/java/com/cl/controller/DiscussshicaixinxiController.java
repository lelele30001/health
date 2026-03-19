package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.DiscussshicaixinxiEntity;
import com.cl.entity.view.DiscussshicaixinxiView;
import com.cl.service.DiscussshicaixinxiService;
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
 * 食材信息评论表
 * 后端接口
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:54
 */
@RestController
@RequestMapping("/discussshicaixinxi")
public class DiscussshicaixinxiController {
    @Autowired
    private DiscussshicaixinxiService discussshicaixinxiService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, DiscussshicaixinxiEntity discussshicaixinxi,
                  HttpServletRequest request) {
        EntityWrapper<DiscussshicaixinxiEntity> ew = new EntityWrapper<DiscussshicaixinxiEntity>();


        PageUtils page = discussshicaixinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussshicaixinxi), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, DiscussshicaixinxiEntity discussshicaixinxi,
                  HttpServletRequest request) {
        EntityWrapper<DiscussshicaixinxiEntity> ew = new EntityWrapper<DiscussshicaixinxiEntity>();

        PageUtils page = discussshicaixinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussshicaixinxi), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(DiscussshicaixinxiEntity discussshicaixinxi) {
        EntityWrapper<DiscussshicaixinxiEntity> ew = new EntityWrapper<DiscussshicaixinxiEntity>();
        ew.allEq(MPUtil.allEQMapPre(discussshicaixinxi, "discussshicaixinxi"));
        return R.ok().put("data", discussshicaixinxiService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(DiscussshicaixinxiEntity discussshicaixinxi) {
        EntityWrapper<DiscussshicaixinxiEntity> ew = new EntityWrapper<DiscussshicaixinxiEntity>();
        ew.allEq(MPUtil.allEQMapPre(discussshicaixinxi, "discussshicaixinxi"));
        DiscussshicaixinxiView discussshicaixinxiView = discussshicaixinxiService.selectView(ew);
        return R.ok("查询食材信息评论表成功").put("data", discussshicaixinxiView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        DiscussshicaixinxiEntity discussshicaixinxi = discussshicaixinxiService.selectById(id);
        discussshicaixinxi = discussshicaixinxiService.selectView(new EntityWrapper<DiscussshicaixinxiEntity>().eq("id", id));
        return R.ok().put("data", discussshicaixinxi);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        DiscussshicaixinxiEntity discussshicaixinxi = discussshicaixinxiService.selectById(id);
        discussshicaixinxi = discussshicaixinxiService.selectView(new EntityWrapper<DiscussshicaixinxiEntity>().eq("id", id));
        return R.ok().put("data", discussshicaixinxi);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody DiscussshicaixinxiEntity discussshicaixinxi, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(discussshicaixinxi);
        discussshicaixinxiService.insert(discussshicaixinxi);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody DiscussshicaixinxiEntity discussshicaixinxi, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(discussshicaixinxi);
        discussshicaixinxiService.insert(discussshicaixinxi);
        return R.ok();
    }

    /**
     * 获取用户密保
     */
    @RequestMapping("/security")
    @IgnoreAuth
    public R security(@RequestParam String username) {
        DiscussshicaixinxiEntity discussshicaixinxi = discussshicaixinxiService.selectOne(new EntityWrapper<DiscussshicaixinxiEntity>().eq("", username));
        return R.ok().put("data", discussshicaixinxi);
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    @IgnoreAuth
    public R update(@RequestBody DiscussshicaixinxiEntity discussshicaixinxi, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(discussshicaixinxi);
        discussshicaixinxiService.updateById(discussshicaixinxi);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        discussshicaixinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
     * 前端智能排序
     */
    @IgnoreAuth
    @RequestMapping("/autoSort")
    public R autoSort(@RequestParam Map<String, Object> params, DiscussshicaixinxiEntity discussshicaixinxi, HttpServletRequest request, String pre) {
        EntityWrapper<DiscussshicaixinxiEntity> ew = new EntityWrapper<DiscussshicaixinxiEntity>();
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
        PageUtils page = discussshicaixinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, discussshicaixinxi), params), params));
        return R.ok().put("data", page);
    }


}
