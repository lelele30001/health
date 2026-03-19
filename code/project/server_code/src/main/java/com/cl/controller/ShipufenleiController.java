package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.ShipufenleiEntity;
import com.cl.entity.view.ShipufenleiView;
import com.cl.service.ShipufenleiService;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

/**
 * 食谱分类
 * 后端接口
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
@RestController
@RequestMapping("/shipufenlei")
public class ShipufenleiController {
    @Autowired
    private ShipufenleiService shipufenleiService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, ShipufenleiEntity shipufenlei,
                  HttpServletRequest request) {
        EntityWrapper<ShipufenleiEntity> ew = new EntityWrapper<ShipufenleiEntity>();


        PageUtils page = shipufenleiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shipufenlei), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ShipufenleiEntity shipufenlei,
                  HttpServletRequest request) {
        EntityWrapper<ShipufenleiEntity> ew = new EntityWrapper<ShipufenleiEntity>();

        PageUtils page = shipufenleiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shipufenlei), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(ShipufenleiEntity shipufenlei) {
        EntityWrapper<ShipufenleiEntity> ew = new EntityWrapper<ShipufenleiEntity>();
        ew.allEq(MPUtil.allEQMapPre(shipufenlei, "shipufenlei"));
        return R.ok().put("data", shipufenleiService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ShipufenleiEntity shipufenlei) {
        EntityWrapper<ShipufenleiEntity> ew = new EntityWrapper<ShipufenleiEntity>();
        ew.allEq(MPUtil.allEQMapPre(shipufenlei, "shipufenlei"));
        ShipufenleiView shipufenleiView = shipufenleiService.selectView(ew);
        return R.ok("查询食谱分类成功").put("data", shipufenleiView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        ShipufenleiEntity shipufenlei = shipufenleiService.selectById(id);
        shipufenlei = shipufenleiService.selectView(new EntityWrapper<ShipufenleiEntity>().eq("id", id));
        return R.ok().put("data", shipufenlei);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        ShipufenleiEntity shipufenlei = shipufenleiService.selectById(id);
        shipufenlei = shipufenleiService.selectView(new EntityWrapper<ShipufenleiEntity>().eq("id", id));
        return R.ok().put("data", shipufenlei);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ShipufenleiEntity shipufenlei, HttpServletRequest request) {
        if (shipufenleiService.selectCount(new EntityWrapper<ShipufenleiEntity>().eq("shipufenlei", shipufenlei.getShipufenlei())) > 0) {
            return R.error("食谱分类已存在");
        }
        //ValidatorUtils.validateEntity(shipufenlei);
        shipufenleiService.insert(shipufenlei);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ShipufenleiEntity shipufenlei, HttpServletRequest request) {
        if (shipufenleiService.selectCount(new EntityWrapper<ShipufenleiEntity>().eq("shipufenlei", shipufenlei.getShipufenlei())) > 0) {
            return R.error("食谱分类已存在");
        }
        //ValidatorUtils.validateEntity(shipufenlei);
        shipufenleiService.insert(shipufenlei);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ShipufenleiEntity shipufenlei, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(shipufenlei);
        shipufenleiService.updateById(shipufenlei);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        shipufenleiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


}
