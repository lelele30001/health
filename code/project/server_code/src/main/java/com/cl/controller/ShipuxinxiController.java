package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.ShipuxinxiEntity;
import com.cl.entity.StoreupEntity;
import com.cl.entity.view.ShipuxinxiView;
import com.cl.service.ShipuxinxiService;
import com.cl.service.StoreupService;
import com.cl.utils.MPUtil;
import com.cl.utils.PageUtils;
import com.cl.utils.R;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 食谱信息
 * 后端接口
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
@RestController
@RequestMapping("/shipuxinxi")
public class ShipuxinxiController {
    @Autowired
    private ShipuxinxiService shipuxinxiService;

    @Autowired
    private StoreupService storeupService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, ShipuxinxiEntity shipuxinxi,
                  HttpServletRequest request) {
        EntityWrapper<ShipuxinxiEntity> ew = new EntityWrapper<ShipuxinxiEntity>();


        PageUtils page = shipuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shipuxinxi), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ShipuxinxiEntity shipuxinxi,
                  HttpServletRequest request) {
        EntityWrapper<ShipuxinxiEntity> ew = new EntityWrapper<ShipuxinxiEntity>();

        PageUtils page = shipuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shipuxinxi), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(ShipuxinxiEntity shipuxinxi) {
        EntityWrapper<ShipuxinxiEntity> ew = new EntityWrapper<ShipuxinxiEntity>();
        ew.allEq(MPUtil.allEQMapPre(shipuxinxi, "shipuxinxi"));
        return R.ok().put("data", shipuxinxiService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ShipuxinxiEntity shipuxinxi) {
        EntityWrapper<ShipuxinxiEntity> ew = new EntityWrapper<ShipuxinxiEntity>();
        ew.allEq(MPUtil.allEQMapPre(shipuxinxi, "shipuxinxi"));
        ShipuxinxiView shipuxinxiView = shipuxinxiService.selectView(ew);
        return R.ok("查询食谱信息成功").put("data", shipuxinxiView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        ShipuxinxiEntity shipuxinxi = shipuxinxiService.selectById(id);
        if (null == shipuxinxi.getClickNumber()) {
            shipuxinxi.setClickNumber(0);
        }
        shipuxinxi.setClickNumber(shipuxinxi.getClickNumber() + 1);
        shipuxinxi.setClicktime(new Date());
        shipuxinxiService.updateById(shipuxinxi);
        shipuxinxi = shipuxinxiService.selectView(new EntityWrapper<ShipuxinxiEntity>().eq("id", id));
        return R.ok().put("data", shipuxinxi);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        ShipuxinxiEntity shipuxinxi = shipuxinxiService.selectById(id);
        if (null == shipuxinxi.getClickNumber()) {
            shipuxinxi.setClickNumber(0);
        }
        shipuxinxi.setClickNumber(shipuxinxi.getClickNumber() + 1);
        shipuxinxi.setClicktime(new Date());
        shipuxinxiService.updateById(shipuxinxi);
        shipuxinxi = shipuxinxiService.selectView(new EntityWrapper<ShipuxinxiEntity>().eq("id", id));
        return R.ok().put("data", shipuxinxi);
    }


    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ShipuxinxiEntity shipuxinxi, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(shipuxinxi);
        shipuxinxiService.insert(shipuxinxi);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ShipuxinxiEntity shipuxinxi, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(shipuxinxi);
        shipuxinxiService.insert(shipuxinxi);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ShipuxinxiEntity shipuxinxi, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(shipuxinxi);
        shipuxinxiService.updateById(shipuxinxi);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        shipuxinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
     * 前端智能排序
     */
    @IgnoreAuth
    @RequestMapping("/autoSort")
    public R autoSort(@RequestParam Map<String, Object> params, ShipuxinxiEntity shipuxinxi, HttpServletRequest request, String pre) {
        EntityWrapper<ShipuxinxiEntity> ew = new EntityWrapper<ShipuxinxiEntity>();
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
        params.put("sort", "click_number");
        params.put("order", "desc");
        PageUtils page = shipuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shipuxinxi), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 按收藏推荐
     */
    @RequestMapping("/autoSort2")
    public R autoSort2(@RequestParam Map<String, Object> params, ShipuxinxiEntity shipuxinxi, HttpServletRequest request) {
        String userId = request.getSession().getAttribute("userId").toString();
        String inteltypeColumn = "shipufenlei";
        List<StoreupEntity> storeups = storeupService.selectList(new EntityWrapper<StoreupEntity>().eq("type", 1).eq("userid", userId).eq("tablename", "shipuxinxi").orderBy("addtime", false));
        List<String> inteltypes = new ArrayList<String>();
        Integer limit = params.get("limit") == null ? 10 : Integer.parseInt(params.get("limit").toString());
        List<ShipuxinxiEntity> shipuxinxiList = new ArrayList<ShipuxinxiEntity>();
        //去重
        if (storeups != null && storeups.size() > 0) {
            for (StoreupEntity s : storeups) {
                shipuxinxiList.addAll(shipuxinxiService.selectList(new EntityWrapper<ShipuxinxiEntity>().eq(inteltypeColumn, s.getInteltype())));
            }
        }
        EntityWrapper<ShipuxinxiEntity> ew = new EntityWrapper<ShipuxinxiEntity>();
        params.put("sort", "id");
        params.put("order", "desc");
        PageUtils page = shipuxinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shipuxinxi), params), params));
        List<ShipuxinxiEntity> pageList = (List<ShipuxinxiEntity>) page.getList();
        if (shipuxinxiList.size() < limit) {
            int toAddNum = (limit - shipuxinxiList.size()) <= pageList.size() ? (limit - shipuxinxiList.size()) : pageList.size();
            for (ShipuxinxiEntity o1 : pageList) {
                boolean addFlag = true;
                for (ShipuxinxiEntity o2 : shipuxinxiList) {
                    if (o1.getId().intValue() == o2.getId().intValue()) {
                        addFlag = false;
                        break;
                    }
                }
                if (addFlag) {
                    shipuxinxiList.add(o1);
                    if (--toAddNum == 0) break;
                }
            }
        } else if (shipuxinxiList.size() > limit) {
            shipuxinxiList = shipuxinxiList.subList(0, limit);
        }
        page.setList(shipuxinxiList);
        return R.ok().put("data", page);
    }


}
