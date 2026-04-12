<template>
  <div class="home">
    <div class="index_top">
      <div class="index_top_left">
        <div class="index_top_title"><span>智慧膳食分享系统</span></div>
      </div>
      <div class="index_top_right">
        <div class="logo">
          <svg
            t="1773642571598"
            class="icon"
            viewBox="0 0 1024 1024"
            version="1.1"
            xmlns="http://www.w3.org/2000/svg"
            p-id="1806"
            width="40"
            height="40"
          >
            <path
              d="M0 0m409.6 0l204.8 0q409.6 0 409.6 409.6l0 204.8q0 409.6-409.6 409.6l-204.8 0q-409.6 0-409.6-409.6l0-204.8q0-409.6 409.6-409.6Z"
              fill="#FA7301"
              opacity=".2"
              p-id="1807"
            ></path>
            <path
              d="M496.5376 257.536l0.17408 1.88416 7.30112 120.45312a108.7488 108.7488 0 0 1-83.22048 112.36352v275.17952a25.32352 25.32352 0 0 1-50.5856 1.8944l-0.06144-1.8944v-275.2a108.78976 108.78976 0 0 1-83.37408-109.09696l0.1536-3.25632 7.29088-120.44288a25.32352 25.32352 0 0 1 50.59584 1.1776v1.88416l-7.29088 120.45312a58.12224 58.12224 0 0 0 32.6656 55.808v-177.78688a25.32352 25.32352 0 0 1 50.5856-1.8944l0.06144 1.8944v177.78688a58.10176 58.10176 0 0 0 32.768-52.29568v-1.77152l-0.09216-1.75104-7.29088-120.45312a25.32352 25.32352 0 0 1 50.37056-4.93568z m69.86752 3.97312a25.32352 25.32352 0 0 1 22.87616-25.20064 103.04512 103.04512 0 0 1 108.77952 58.7264c36.58752 71.76192 53.248 159.60064 25.14944 207.872-19.31264 33.13664-55.296 52.6848-106.1376 60.01664v204.4928a25.32352 25.32352 0 0 1-50.5856 1.8944l-0.06144-1.8944z m50.64704 26.14272v223.95904l3.64544-0.67584c28.86656-5.69344 47.6672-16.45568 57.45664-31.488l1.28-2.048c12.94336-22.20032 7.60832-70.79936-9.04192-118.36416l-3.072-8.36608a329.4208 329.4208 0 0 0-14.39744-32.6144 52.38784 52.38784 0 0 0-33.6896-29.83936l-2.2016-0.52224z"
              fill="#FA7301"
              p-id="1808"
            ></path>
          </svg>
        </div>
        <el-button
          v-if="!Token"
          class="login"
          type="primary"
          @click="loginClick"
        >
          登录
        </el-button>
        <div class="user" v-if="Token">
          <el-dropdown class="avatar-container" trigger="hover">
            <div class="avatar-wrapper">
              <img
                class="user-avatar"
                :src="store.getters['user/avatar']"
                style="width: 50px"
              />
              <div class="nickname">
                {{ $toolUtil.storageGet("frontName") }}
              </div>
              <el-icon class="el-icon-arrow-down">
                <arrow-down />
              </el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-dropDown" slot="dropdown">
                <el-dropdown-item @click="menuHandler('center')" class="center">
                  <span>个人中心</span>
                </el-dropdown-item>
                <el-dropdown-item @click="loginOut" class="loginOut">
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
    <el-scrollbar class="contain_view">
      <div class="menu-wrapper">
        <el-scrollbar wrap-class="scrollbar-wrapper" class="menu_scrollbar">
          <el-menu
            :default-openeds="[]"
            :unique-opened="true"
            :default-active="menuIndex"
            class="menu_view"
            mode="horizontal"
            :ellipsis="false"
            @select="menuChange"
            :key="menuIndex"
          >
            <el-menu-item
              class="first-item"
              index="0"
              @click="menuHandler('/')"
            >
              <template #title>
                <span>首页</span>
              </template>
            </el-menu-item>
            <template v-for="(menu, index) in menuList" :key="menu.menu">
              <el-sub-menu
                v-if="menu.child.length > 1"
                :index="index + 2 + ''"
                class="first-item"
                :teleported="true"
              >
                <template #title>
                  <span>{{ menu.name }}</span>
                </template>
                <el-menu-item
                  class="second-item"
                  v-for="(child, sort) in menu.child"
                  :key="sort"
                  :index="index + 2 + '-' + sort"
                  @click="menuHandler(child.url)"
                  >{{ child.name }}
                </el-menu-item>
              </el-sub-menu>
              <el-menu-item
                v-else
                :index="index + 2 + ''"
                class="first-item"
                @click="menuHandler(menu.child[0].url)"
              >
                <template #title>
                  <span>{{ menu.child[0].name }}</span>
                </template>
              </el-menu-item>
            </template>
          </el-menu>
        </el-scrollbar>
      </div>

      <router-view />
      <el-backtop :right="100" :bottom="100" />
      <div class="bottom_view">
        <div class="footer-content">
          <div class="footer-section">
            <h3>智慧膳食分享系统</h3>
            <p>
              为健身人群提供个性化的膳食推荐和分享平台，帮助您实现健康饮食目标。
            </p>
          </div>
          <div class="footer-section">
            <h3>快速链接</h3>
            <ul>
              <li><a href="/index/home">首页</a></li>
              <li><a href="/index/shipuxinxiList">食谱浏览</a></li>
              <li><a href="/index/shicaixinxiList">食材信息</a></li>
              <li><a href="/index/shipuanpaiList">食谱安排</a></li>
            </ul>
          </div>
          <div class="footer-section">
            <h3>联系我们</h3>
            <p>邮箱：contact@example.com</p>
            <p>电话：123-456-7890</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>
            &copy; {{ new Date().getFullYear() }} 智慧膳食分享系统.
            保留所有权利.
          </p>
        </div>
      </div>
    </el-scrollbar>
  </div>
</template>
<script setup>
import menu from "@/utils/menu";
import axios from "axios";
import {
  ref,
  onBeforeUnmount,
  getCurrentInstance,
  nextTick,
  computed,
  watch,
} from "vue";
import { useRouter, useRoute } from "vue-router";
import { useStore } from "vuex";
import "@/styles/global.css";
const store = useStore();
const router = useRouter();
const route = useRoute();
const context = getCurrentInstance()?.appContext.config.globalProperties;
const Token = ref("");
const date = ref("");
const timer = ref("");
const interval = ref(null);
if (localStorage.getItem("frontToken") && !store.getters["user/session"].id) {
  store.dispatch("user/getSession");
}
const toBack = () => {
  window.open(`${context.$config.url}manage/index.html#/login`, "_blank");
};
onBeforeUnmount(() => {
  clearInterval(interval.value);
});
// 获取默认菜单index
const setMenuIndex = () => {
  menuIndex.value = "";
  nextTick(() => {
    menuIndex.value = context?.$toolUtil.storageGet("menuIndex")
      ? context?.$toolUtil.storageGet("menuIndex")
      : "0";
  });
};
// 默认菜单index
const menuIndex = ref("0");
watch(
  () => router.currentRoute.value,
  () => {
    Token.value = context?.$toolUtil.storageGet("frontToken");
    setMenuIndex();
  },
  {
    immediate: true,
  }
);
const init = () => {
  // 获取菜单
  getMenu();
  // 赋值token
  Token.value = context?.$toolUtil.storageGet("frontToken");
  // 时间
  interval.value = setInterval(() => {
    date.value = context?.$toolUtil.getCurDate();
    timer.value = context?.$toolUtil.getCurDateTime().split(" ")[1];
  }, 1000);
  if (Token.value) {
    getSession();
  }
};
// 切换菜单保存index
const menuChange = (e) => {
  if (e == "chat") return;
  context?.$toolUtil.storageSet("menuIndex", e);
};
const menuList = ref([]);
const role = ref("");
const getMenu = () => {
  let params = {
    page: 1,
    limit: 1,
    sort: "id",
  };
  context
    ?.$http({
      url: "menu/list",
      method: "get",
      params: params,
    })
    .then((res) => {
      context?.$toolUtil.storageSet("menus", res.data.data.list[0].menujson);
    });
  menuList.value = context?.$config.menuList;
};
const loginClick = () => {
  context?.$toolUtil.storageSet("toPath", window.history.state.current);
  router.push("/login");
};
const loginOut = () => {
  context?.$toolUtil.message("退出成功", "success");
  context?.$toolUtil.storageClear();
  router.replace("/index/home");
  context?.$toolUtil.storageSet("menuIndex", "0");
  Token.value = "";
};
//菜单跳转
const menuHandler = (name) => {
  if (name == "center") {
    name = `${context?.$toolUtil.storageGet("frontSessionTable")}Center`;
    menuChange("center");
  }
  router.push(name);
};
// 获取用户信息
const getSession = () => {
  context
    ?.$http({
      url: `${context?.$toolUtil.storageGet("frontSessionTable")}/session`,
      method: "get",
    })
    .then((res) => {
      context?.$toolUtil.storageSet("userid", res.data.data.id);
      if (context?.$toolUtil.storageGet("frontSessionTable") == "users") {
        context?.$toolUtil.storageSet("headportrait", res.data.data.touxiang);
      }
      if (context?.$toolUtil.storageGet("frontSessionTable") == "yonghu") {
        context?.$toolUtil.storageSet(
          "frontName",
          res.data.data.yonghuzhanghao
        );
      }
      if (context?.$toolUtil.storageGet("frontSessionTable") == "yonghu") {
        context?.$toolUtil.storageSet("headportrait", res.data.data.touxiang);
      }
    });
};
init();
</script>
<style lang="scss" scoped>
/* 头部 */
.index_top {
  .index_top_title {
  }
  .index_top_right {
    line-height: 1;
    .weather_time_view {
      display: flex;
      align-items: center;
      .weather {
        padding: 0 10px;
        flex-direction: row;
        display: none;
        font-size: 16px;
        line-height: 1;
        justify-content: center;
        align-items: center;
        .city {
          padding: 0;
          margin: 0 10px 0 0;
        }
        .wea {
        }
      }
      .time {
        padding: 0 20px;
        flex-direction: row;
        display: flex;
        font-size: 16px;
        line-height: 1;
        justify-content: center;
        align-items: center;
        .date {
          padding: 0;
          margin: 0 10px 0 0;
        }
        .timer {
          padding: 0;
          margin: 0;
        }
      }
    }
  }
  // 登录按钮
  :deep(.el-button--success) {
    border-radius: 4px;
    padding: 10px;
    margin: 0 10px 0 0;
    color: #fff;
    background: none;
    font-size: 16px;
    border-color: transparent;
  }
  :deep(.el-button--success:hover) {
  }
  // 登出按钮
  :deep(.el-button--danger) {
    border-radius: 4px;
    padding: 10px;
    color: #fff;
    background: none;
    font-size: 16px;
    border-color: transparent;
  }
  :deep(.el-button--danger:hover) {
  }
}
// 轮播盒子
.rotation_view {
}
// 底部
.bottom_view {
  // 图片
  .bottom_img {
    border-radius: 100%;
    object-fit: cover;
    display: none;
    width: 44px;
    height: 44px;
  }
  // 公司
  .bottom_company {
    margin: 10px 0 0;
  }
  // 备案号
  .bottom_record {
    margin: 10px 0 0;
  }
  // 联系方式
  .bottom_desc {
    margin: 10px 0 0;
  }
}

.home {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.contain_view {
  margin: 0;
  background: #f6f6f6;
  position: relative;
  flex: 1;
  min-height: calc(100vh - 140px);
}
.el-aside {
  transition: width 0.15s;
  -webkit-transition: width 0.15s;
  -moz-transition: width 0.15s;
  -webkit-transition: width 0.15s;
  -o-transition: width 0.15s;
}
.el-sub-menu__hide-arrow {
  display: block !important;
}
.menu_scrollbar {
}
// 总盒子
.menu_view {
  // 一级菜单
  :deep(.first-item) {
    // 图标

    // 标题
    .el-sub-menu__title,
    span {
    }

    //箭头
    .el-sub-menu__icon-arrow {
    }
  }
  // 选中
  :deep(.is-active) {
  }

  // 悬浮
  :deep(.first-item:hover) {
  }
}
</style>
<style lang="scss">
// 二级
.menu_popper {
  border: none !important;
  background: none !important;

  // 二级总盒子
  .el-menu--popup {
  }

  // 二级菜单
  .second-item {
  }

  // 选中
  .is-active {
  }

  // 悬浮
  .second-item:hover {
  }
}
</style>
<style>
.index_top {
  box-sizing: border-box;
  width: 100%;
  display: flex;
  align-items: center;
  height: 80px;
  background: var(--background-color);
  color: var(--text-color);
  justify-content: space-between;
  padding: 0px var(--spacing-xl);
  z-index: 1002;
  position: relative;
  box-shadow: var(--shadow);
}

.index_top .index_top_left {
  display: flex;
  align-items: center;
}

.index_top .index_top_left .index_top_title {
  font-size: var(--font-size-xl);
  color: var(--text-color);
  font-weight: 700;
  margin-left: 0;
}

.index_top .index_top_right {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  color: inherit;
}

.index_top .index_top_right .logo {
  margin-right: var(--spacing-md);
}

.index_top .notice-btn {
  background: none;
  border: 0px solid #dcdfe6;
  color: #fff;
  margin: 0;
  padding: 10px;
  font-size: 16px;
  border-radius: 0px;
}
.index_top .notice-btn:hover {
  color: #ccc;
}

.index_top .notice-btn .iconfont {
  font-size: 16px;
}

.index_top .index_top_right .chat {
  margin-right: 10px;
}
.index_top .index_top_right .chat:hover {
  color: #ccc;
}
.index_top .index_top_right .chat .iconfont {
  font-size: 16px;
}
.index_top .index_top_right .chat span {
  font-size: 16px;
}

.index_top .index_top_right .cart {
  margin-right: 10px;
  font-size: 16px;
}
.index_top .index_top_right .cart:hover {
  color: #ccc;
}
.index_top .index_top_right .cart .iconfont {
  font-size: 16px;
  margin-right: 5px;
}

.index_top .index_top_right .toBack {
  margin-right: 15px;
}
.index_top .index_top_right .toBack .el-button {
  background: none;
  border: 0;
  padding: 0;
  font-size: 16px;
  color: #fff;
}
.index_top .index_top_right .toBack .el-button:hover {
  color: #ccc;
  background: none;
}
.index_top .index_top_right .login {
}
.index_top .index_top_right .login .el-icon {
  color: #fff !important;
}

.index_top .user .avatar-wrapper {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: inherit;
}

.index_top .user .avatar-wrapper .user-avatar {
  width: 36px !important;
  height: 36px;
  border-radius: 100%;
  margin-right: 5px;
}

.index_top .user .avatar-wrapper .nickname {
  font-size: 16px;
  margin-right: 5px;
  color: #fff;
}

.index_top .user .avatar-wrapper .el-icon-arrow-down {
  color: #fff;
}

.user-dropDown {
  padding: 10px 0;
  margin: 5px 0;
  background: #fff;
  border: 1px solid #e6ebf5;
  border-radius: 4px;
  -webkit-box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.user-dropDown li {
  padding: 0 20px !important;
  line-height: 36px !important;
  font-size: 14px !important;
  color: #606266 !important;
}
.user-dropDown li:hover {
  color: #0076ca !important;
  background: none !important;
}

.user-dropDown li.loginOut {
  background: none !important;
  border-color: none !important;
  color: #666 !important;
}
.user-dropDown li.loginOut:hover {
  border-radius: 0px !important;
  background: none !important;
  color: #0076ca !important;
}
.bottom_view {
  width: 100%;
  background: var(--text-color);
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  padding: var(--spacing-xl) 0;
  min-height: 200px;
  border-top: 1px solid var(--border-color);
  font-size: var(--font-size-sm);
  color: var(--background-color);
  line-height: 1.8;
  margin-top: var(--spacing-xxl);
}

.bottom_view .footer-content {
  max-width: 1200px;
  width: 100%;
  padding: 0 var(--spacing-xl);
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-lg);
}

.bottom_view .footer-section {
  flex: 1;
  min-width: 200px;
  margin-bottom: var(--spacing-lg);
}

.bottom_view .footer-section h3 {
  font-size: var(--font-size-md);
  font-weight: 600;
  margin-bottom: var(--spacing-md);
  color: var(--background-color);
}

.bottom_view .footer-section p {
  margin-bottom: var(--spacing-sm);
  color: rgba(255, 255, 255, 0.8);
}

.bottom_view .footer-section ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.bottom_view .footer-section ul li {
  margin-bottom: var(--spacing-sm);
}

.bottom_view .footer-section ul li a {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  transition: var(--transition);
}

.bottom_view .footer-section ul li a:hover {
  color: var(--primary-color);
}

.bottom_view .footer-bottom {
  width: 100%;
  text-align: center;
  padding-top: var(--spacing-lg);
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.6);
  font-size: var(--font-size-xs);
}

.menu_scrollbar {
  width: 100%;
  background: var(--background-color);
  border-bottom: 1px solid var(--border-color);
  padding: 0 var(--spacing-xl);
}

.menu_scrollbar .el-scrollbar__view {
  padding-bottom: 0;
}

.menu_scrollbar .menu_view {
  background: var(--background-color);
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-start;
  border-bottom: none;
  padding: 0;
}

.menu_scrollbar .menu_view .el-menu-item,
.menu_scrollbar .menu_view .el-sub-menu,
.menu_scrollbar .menu_view .el-sub-menu .el-sub-menu__title {
  height: 60px;
  line-height: 60px;
  border: 0;
  color: var(--text-secondary);
  font-size: var(--font-size-md);
  padding: 0 var(--spacing-lg);
  cursor: pointer;
  white-space: nowrap;
  list-style: none;
  background: var(--background-color);
  text-align: center;
  transition: var(--transition);
}

.menu_scrollbar .menu_view .el-menu-item:hover,
.menu_scrollbar .menu_view .el-sub-menu .el-sub-menu__title:hover {
  color: var(--primary-color);
}

.menu_scrollbar .menu_view .el-menu-item::after,
.menu_scrollbar .menu_view .el-sub-menu .el-sub-menu__title::after {
  content: "";
  height: 3px;
  background: var(--primary-color);
  display: block;
  margin-top: -3px;
  transition: transform 0.3s ease;
  transform: scaleX(0);
  transform-origin: 50% 0;
}

.menu_scrollbar .menu_view .el-menu-item.is-active::after,
.menu_scrollbar .menu_view .el-sub-menu .el-sub-menu__title.is-active::after,
.first-item.el-sub-menu.is-active .el-sub-menu__title::after {
  transform: scaleX(1);
}

.menu_scrollbar .menu_view .el-menu-item.is-active,
.menu_scrollbar .menu_view .el-sub-menu.is-active {
  color: var(--primary-color) !important;
  font-weight: 600;
}

li.el-menu-item.first-item {
}

li.el-menu-item.first-item i {
  vertical-align: middle;
  margin: 0 var(--spacing-sm);
  font-size: var(--font-size-lg);
  color: inherit;
}

li.el-menu-item.first-item span {
  font-size: var(--font-size-md);
  vertical-align: middle;
  color: inherit;
}

li.el-menu-item.first-item.is-active {
  color: var(--primary-color) !important;
}

li.el-menu-item.first-item:hover {
  color: var(--primary-color) !important;
}

.first-item.el-sub-menu .el-sub-menu__title {
  width: 100% !important;
}

.first-item.el-sub-menu.is-opened .el-sub-menu__title {
  width: 100% !important;
  color: var(--primary-color) !important;
}

.first-item.el-sub-menu.is-active .el-sub-menu__title {
  width: 100% !important;
  color: var(--primary-color) !important;
}

.menu_scrollbar .menu_view .el-sub-menu .el-sub-menu__title .el-icon-menu,
.menu_scrollbar .menu_view .el-sub-menu .el-sub-menu__title .iconfont {
  vertical-align: middle;
  margin: 0 var(--spacing-sm);
  font-size: var(--font-size-lg);
  color: inherit;
}

.menu_scrollbar .menu_view .el-sub-menu .el-sub-menu__title span {
  font-size: var(--font-size-md);
  vertical-align: middle;
  color: inherit;
}

.menu_scrollbar
  .menu_view
  .el-sub-menu
  .el-sub-menu__title
  .el-sub-menu__icon-arrow {
  position: static;
  margin: -3px 0 0 var(--spacing-sm);
  font-size: 12px;
  vertical-align: middle;
  color: inherit;
  transition: var(--transition);
}

.el-menu .el-icon {
  flex-shrink: inherit !important;
}

.el-menu--horizontal .el-menu {
  border: none;
  background: var(--background-color);
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow);
  margin-top: var(--spacing-sm);
}

.el-menu--horizontal .el-menu .el-menu-item {
  color: var(--text-secondary);
  height: 48px;
  line-height: 48px;
  padding: 0 var(--spacing-lg);
  background: var(--background-color);
  transition: var(--transition);
}

.el-menu--horizontal .el-menu .el-menu-item:hover {
  color: var(--primary-color) !important;
  background: var(--secondary-color) !important;
}

.el-menu--horizontal .el-menu .el-menu-item.is-active {
  color: var(--primary-color) !important;
  background: var(--secondary-color) !important;
  font-weight: 600;
}

.bread_view {
  width: 100%;
  background: none;
  color: rgb(51, 51, 51);
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  border: 0px solid #eee;
  padding: 15px 20px 10px 50px;
  background: var(--theme);
  border-radius: 0px;
}

.bread_view .el-breadcrumb {
  font-size: 16px;
  line-height: 1;
}

.bread_view .el-breadcrumb .el-breadcrumb__separator {
  margin: 0px 9px;
  font-weight: 500;
  color: #fff;
}

.bread_view .el-breadcrumb .first_breadcrumb span a {
  color: #fff;
  display: inline-block;
}

.bread_view .el-breadcrumb .second_breadcrumb .el-breadcrumb__inner {
  color: #fff;
  display: inline-block;
}
.bread_view .el-breadcrumb .second_breadcrumb .el-breadcrumb__inner.is-link {
  color: #fff !important;
}
.el-breadcrumb__inner a,
.el-breadcrumb__inner.is-link {
  color: #fff !important;
}
</style>
