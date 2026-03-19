package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.ShipuanpaiEntity;
import com.cl.entity.view.ShipuanpaiView;
import com.cl.service.ShipuanpaiService;
import com.cl.service.StoreupService;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * 食谱安排
 * 后端接口
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:54
 */
@RestController
@RequestMapping("/shipuanpai")
public class ShipuanpaiController {
    @Autowired
    private ShipuanpaiService shipuanpaiService;

    @Autowired
    private StoreupService storeupService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, ShipuanpaiEntity shipuanpai,
                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date riqistart,
                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date riqiend,
                  HttpServletRequest request) {
        EntityWrapper<ShipuanpaiEntity> ew = new EntityWrapper<ShipuanpaiEntity>();
        if (riqistart != null) ew.ge("riqi", riqistart);
        if (riqiend != null) ew.le("riqi", riqiend);


        PageUtils page = shipuanpaiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shipuanpai), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ShipuanpaiEntity shipuanpai,
                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date riqistart,
                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date riqiend,
                  HttpServletRequest request) {
        EntityWrapper<ShipuanpaiEntity> ew = new EntityWrapper<ShipuanpaiEntity>();
        if (riqistart != null) ew.ge("riqi", riqistart);
        if (riqiend != null) ew.le("riqi", riqiend);

        PageUtils page = shipuanpaiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shipuanpai), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(ShipuanpaiEntity shipuanpai) {
        EntityWrapper<ShipuanpaiEntity> ew = new EntityWrapper<ShipuanpaiEntity>();
        ew.allEq(MPUtil.allEQMapPre(shipuanpai, "shipuanpai"));
        return R.ok().put("data", shipuanpaiService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ShipuanpaiEntity shipuanpai) {
        EntityWrapper<ShipuanpaiEntity> ew = new EntityWrapper<ShipuanpaiEntity>();
        ew.allEq(MPUtil.allEQMapPre(shipuanpai, "shipuanpai"));
        ShipuanpaiView shipuanpaiView = shipuanpaiService.selectView(ew);
        return R.ok("查询食谱安排成功").put("data", shipuanpaiView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        ShipuanpaiEntity shipuanpai = shipuanpaiService.selectById(id);
        shipuanpai = shipuanpaiService.selectView(new EntityWrapper<ShipuanpaiEntity>().eq("id", id));
        return R.ok().put("data", shipuanpai);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        ShipuanpaiEntity shipuanpai = shipuanpaiService.selectById(id);
        shipuanpai = shipuanpaiService.selectView(new EntityWrapper<ShipuanpaiEntity>().eq("id", id));
        return R.ok().put("data", shipuanpai);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ShipuanpaiEntity shipuanpai, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(shipuanpai);
        shipuanpaiService.insert(shipuanpai);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ShipuanpaiEntity shipuanpai, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(shipuanpai);
        shipuanpaiService.insert(shipuanpai);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ShipuanpaiEntity shipuanpai, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(shipuanpai);
        shipuanpaiService.updateById(shipuanpai);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        shipuanpaiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


}
