package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.FoodsEntity;
import com.cl.service.FoodsService;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 食材管理
 * 后端接口
 */
@RestController
@RequestMapping("/foods")
public class FoodsController {
    @Autowired
    private FoodsService foodsService;

    /**
     * 列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, FoodsEntity foods, HttpServletRequest request) {
        EntityWrapper<FoodsEntity> ew = new EntityWrapper<FoodsEntity>();
        PageUtils page = foodsService.queryPage(params, ew);
        return R.ok().put("data", page);
    }

    /**
     * 搜索
     */
    @IgnoreAuth
    @RequestMapping("/search")
    public R search(@RequestParam Map<String, Object> params, FoodsEntity foods, HttpServletRequest request) {
        String keyword = (String) params.get("keyword");
        EntityWrapper<FoodsEntity> ew = new EntityWrapper<FoodsEntity>();
        if (keyword != null && !keyword.isEmpty()) {
            ew.like("name", keyword);
        }
        PageUtils page = foodsService.queryPage(params, ew);
        return R.ok().put("data", page);
    }
}
