package com.cl.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cl.annotation.IgnoreAuth;
import com.cl.entity.ShicaixinxiEntity;
import com.cl.entity.StoreupEntity;
import com.cl.entity.view.ShicaixinxiView;
import com.cl.service.ShicaixinxiService;
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
 * 食材信息
 * 后端接口
 *
 * @author
 * @email
 * @date 2025-02-26 11:22:53
 */
@RestController
@RequestMapping("/shicaixinxi")
public class ShicaixinxiController {
    @Autowired
    private ShicaixinxiService shicaixinxiService;

    @Autowired
    private StoreupService storeupService;


    /**
     * 后台列表
     */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, ShicaixinxiEntity shicaixinxi,
                  HttpServletRequest request) {
        EntityWrapper<ShicaixinxiEntity> ew = new EntityWrapper<ShicaixinxiEntity>();


        PageUtils page = shicaixinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shicaixinxi), params), params));
        return R.ok().put("data", page);
    }


    /**
     * 前端列表
     */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, ShicaixinxiEntity shicaixinxi,
                  HttpServletRequest request) {
        EntityWrapper<ShicaixinxiEntity> ew = new EntityWrapper<ShicaixinxiEntity>();

        PageUtils page = shicaixinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shicaixinxi), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/lists")
    public R list(ShicaixinxiEntity shicaixinxi) {
        EntityWrapper<ShicaixinxiEntity> ew = new EntityWrapper<ShicaixinxiEntity>();
        ew.allEq(MPUtil.allEQMapPre(shicaixinxi, "shicaixinxi"));
        return R.ok().put("data", shicaixinxiService.selectListView(ew));
    }

    /**
     * 查询
     */
    @RequestMapping("/query")
    public R query(ShicaixinxiEntity shicaixinxi) {
        EntityWrapper<ShicaixinxiEntity> ew = new EntityWrapper<ShicaixinxiEntity>();
        ew.allEq(MPUtil.allEQMapPre(shicaixinxi, "shicaixinxi"));
        ShicaixinxiView shicaixinxiView = shicaixinxiService.selectView(ew);
        return R.ok("查询食材信息成功").put("data", shicaixinxiView);
    }

    /**
     * 后端详情
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        ShicaixinxiEntity shicaixinxi = shicaixinxiService.selectById(id);
        if (null == shicaixinxi.getClickNumber()) {
            shicaixinxi.setClickNumber(0);
        }
        shicaixinxi.setClickNumber(shicaixinxi.getClickNumber() + 1);
        shicaixinxi.setClicktime(new Date());
        shicaixinxiService.updateById(shicaixinxi);
        shicaixinxi = shicaixinxiService.selectView(new EntityWrapper<ShicaixinxiEntity>().eq("id", id));
        return R.ok().put("data", shicaixinxi);
    }

    /**
     * 前端详情
     */
    @IgnoreAuth
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id) {
        ShicaixinxiEntity shicaixinxi = shicaixinxiService.selectById(id);
        if (null == shicaixinxi.getClickNumber()) {
            shicaixinxi.setClickNumber(0);
        }
        shicaixinxi.setClickNumber(shicaixinxi.getClickNumber() + 1);
        shicaixinxi.setClicktime(new Date());
        shicaixinxiService.updateById(shicaixinxi);
        shicaixinxi = shicaixinxiService.selectView(new EntityWrapper<ShicaixinxiEntity>().eq("id", id));
        return R.ok().put("data", shicaixinxi);
    }


    /**
     * 赞或踩
     */
    @RequestMapping("/thumbsup/{id}")
    public R vote(@PathVariable("id") String id, String type) {
        ShicaixinxiEntity shicaixinxi = shicaixinxiService.selectById(id);
        if (type.equals("1")) {
            shicaixinxi.setThumbsupNumber(shicaixinxi.getThumbsupNumber() + 1);
        } else {
            shicaixinxi.setCrazilyNumber(shicaixinxi.getCrazilyNumber() + 1);
        }
        shicaixinxiService.updateById(shicaixinxi);
        return R.ok("投票成功");
    }

    /**
     * 后端保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody ShicaixinxiEntity shicaixinxi, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(shicaixinxi);
        shicaixinxiService.insert(shicaixinxi);
        return R.ok();
    }

    /**
     * 前端保存
     */
    @RequestMapping("/add")
    public R add(@RequestBody ShicaixinxiEntity shicaixinxi, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(shicaixinxi);
        shicaixinxiService.insert(shicaixinxi);
        return R.ok();
    }


    /**
     * 修改
     */
    @RequestMapping("/update")
    @Transactional
    public R update(@RequestBody ShicaixinxiEntity shicaixinxi, HttpServletRequest request) {
        //ValidatorUtils.validateEntity(shicaixinxi);
        shicaixinxiService.updateById(shicaixinxi);//全部更新
        return R.ok();
    }


    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        shicaixinxiService.deleteBatchIds(Arrays.asList(ids));
        return R.ok();
    }


    /**
     * 前端智能排序
     */
    @IgnoreAuth
    @RequestMapping("/autoSort")
    public R autoSort(@RequestParam Map<String, Object> params, ShicaixinxiEntity shicaixinxi, HttpServletRequest request, String pre) {
        EntityWrapper<ShicaixinxiEntity> ew = new EntityWrapper<ShicaixinxiEntity>();
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
        PageUtils page = shicaixinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shicaixinxi), params), params));
        return R.ok().put("data", page);
    }

    /**
     * 按收藏推荐
     */
    @RequestMapping("/autoSort2")
    public R autoSort2(@RequestParam Map<String, Object> params, ShicaixinxiEntity shicaixinxi, HttpServletRequest request) {
        String userId = request.getSession().getAttribute("userId").toString();
        String inteltypeColumn = "shicaileixing";
        List<StoreupEntity> storeups = storeupService.selectList(new EntityWrapper<StoreupEntity>().eq("type", 1).eq("userid", userId).eq("tablename", "shicaixinxi").orderBy("addtime", false));
        List<String> inteltypes = new ArrayList<String>();
        Integer limit = params.get("limit") == null ? 10 : Integer.parseInt(params.get("limit").toString());
        List<ShicaixinxiEntity> shicaixinxiList = new ArrayList<ShicaixinxiEntity>();
        //去重
        if (storeups != null && storeups.size() > 0) {
            for (StoreupEntity s : storeups) {
                shicaixinxiList.addAll(shicaixinxiService.selectList(new EntityWrapper<ShicaixinxiEntity>().eq(inteltypeColumn, s.getInteltype())));
            }
        }
        EntityWrapper<ShicaixinxiEntity> ew = new EntityWrapper<ShicaixinxiEntity>();
        params.put("sort", "id");
        params.put("order", "desc");
        PageUtils page = shicaixinxiService.queryPage(params, MPUtil.sort(MPUtil.between(MPUtil.likeOrEq(ew, shicaixinxi), params), params));
        List<ShicaixinxiEntity> pageList = (List<ShicaixinxiEntity>) page.getList();
        if (shicaixinxiList.size() < limit) {
            int toAddNum = (limit - shicaixinxiList.size()) <= pageList.size() ? (limit - shicaixinxiList.size()) : pageList.size();
            for (ShicaixinxiEntity o1 : pageList) {
                boolean addFlag = true;
                for (ShicaixinxiEntity o2 : shicaixinxiList) {
                    if (o1.getId().intValue() == o2.getId().intValue()) {
                        addFlag = false;
                        break;
                    }
                }
                if (addFlag) {
                    shicaixinxiList.add(o1);
                    if (--toAddNum == 0) break;
                }
            }
        } else if (shicaixinxiList.size() > limit) {
            shicaixinxiList = shicaixinxiList.subList(0, limit);
        }
        page.setList(shicaixinxiList);
        return R.ok().put("data", page);
    }


}
