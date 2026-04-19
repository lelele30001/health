import { createRouter, createWebHashHistory } from "vue-router";
import news from "@/views/news/list";
import shipuanpai from "@/views/shipuanpai/list";
import shipuxinxi from "@/views/shipuxinxi/list";
import discussshicaixinxi from "@/views/discussshicaixinxi/list";
import shipufenlei from "@/views/shipufenlei/list";
import storeup from "@/views/storeup/list";
import users from "@/views/users/list";
import forum from "@/views/forum/list";
import yonghu from "@/views/yonghu/list";
import discussshipuxinxi from "@/views/discussshipuxinxi/list";
import shicaixinxi from "@/views/shicaixinxi/list";
import shicaileixing from "@/views/shicaileixing/list";
import config from "@/views/config/list";
import usersCenter from "@/views/users/center";
import stats from "@/views/stats/list";
import foods from "@/views/foods/list";
import recipeAudit from "@/views/audit/recipe/list";
import commentAudit from "@/views/audit/comment/list";

export const routes = [
  {
    path: "/login",
    name: "login",
    component: () => import("../views/login.vue"),
  },
  {
    path: "/",
    name: "首页",
    component: () => import("../views/index"),
    children: [
      {
        path: "/",
        name: "首页1",
        component: yonghu,
        meta: {
          affix: true,
        },
      },
      {
        path: "/updatepassword",
        name: "修改密码",
        component: () => import("../views/updatepassword.vue"),
      },

      {
        path: "/usersCenter",
        name: "管理员个人中心",
        component: usersCenter,
      },
      {
        path: "/news",
        name: "公告信息",
        component: news,
      },
      {
        path: "/shipuanpai",
        name: "食谱安排",
        component: shipuanpai,
      },
      {
        path: "/shipuxinxi",
        name: "食谱信息",
        component: shipuxinxi,
      },
      {
        path: "/discussshicaixinxi",
        name: "食材信息评论-评论",
        component: discussshicaixinxi,
      },
      {
        path: "/shipufenlei",
        name: "食谱分类",
        component: shipufenlei,
      },
      {
        path: "/storeup",
        name: "我的收藏",
        component: storeup,
      },
      {
        path: "/users",
        name: "管理员",
        component: users,
      },
      {
        path: "/forum",
        name: "帖子管理",
        component: forum,
      },
      {
        path: "/yonghu",
        name: "用户",
        component: yonghu,
      },
      {
        path: "/discussshipuxinxi",
        name: "食谱信息评论-评论",
        component: discussshipuxinxi,
      },
      {
        path: "/shicaixinxi",
        name: "食材信息",
        component: shicaixinxi,
      },
      {
        path: "/shicaileixing",
        name: "食材类型",
        component: shicaileixing,
      },
      {
        path: "/config",
        name: "轮播图",
        component: config,
      },
      {
        path: "/stats",
        name: "统计",
        component: stats,
      },
      {
        path: "/foods",
        name: "食材管理",
        component: foods,
      },
      {
        path: "/audit/recipe",
        name: "食谱审核",
        component: recipeAudit,
      },
      {
        path: "/audit/comment",
        name: "评论审核",
        component: commentAudit,
      },
    ],
  },
];

const router = createRouter({
  history: createWebHashHistory(process.env.BASE_URL),
  routes,
});

export default router;
