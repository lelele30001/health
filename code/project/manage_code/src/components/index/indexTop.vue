<template>
  <div class="top_view">
    <div class="top_left_view">
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
      <div class="projectTitle">智慧膳食分享系统后台</div>
      <div class="nav-buttons">
        <el-button class="nav-button" @click="navigateToUsers"
          >用户管理</el-button
        >
        <el-button class="nav-button" @click="navigateToPosts"
          >帖子管理</el-button
        >
        <el-button class="nav-button" @click="navigateToStats">统计</el-button>
      </div>
    </div>

    <div class="top_right_view">
      <el-dropdown class="avatar-container" trigger="hover">
        <div class="avatar-wrapper">
          <div class="nickname">
            欢迎 {{ $toolUtil.storageGet("adminName") }}
          </div>
          <img class="user-avatar" :src="store.getters['user/avatar']" />
          <el-icon class="el-icon-arrow-down">
            <arrow-down />
          </el-icon>
        </div>
        <template #dropdown>
          <el-dropdown-menu class="user-dropDown" slot="dropdown">
            <el-dropdown-item class="center" @click="centerClick">
              个人中心
            </el-dropdown-item>
            <el-dropdown-item class="password" @click="updatepasswordClick">
              修改密码
            </el-dropdown-item>
            <el-dropdown-item class="front">
              <span style="display: block" @click="frontClick">系统前台</span>
            </el-dropdown-item>
            <el-dropdown-item class="loginOut">
              <span style="display: block" @click="onLogout">退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
    <div class="breadcrumb-view">
      <el-breadcrumb separator="—">
        <el-breadcrumb-item
          v-for="(item, index) in breadcrumbList"
          :key="item.path"
        >
          <span @click="router.push(item.path)">{{ item.name }}</span>
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
  </div>
</template>

<script setup>
import axios from "axios";
import moment from "moment";
import { ElMessageBox } from "element-plus";
import { getCurrentInstance, ref, onBeforeUnmount, computed } from "vue";

import { useRouter, useRoute } from "vue-router";
const router = useRouter();
const context = getCurrentInstance()?.appContext.config.globalProperties;
const role = context?.$toolUtil.storageGet("sessionTable");
const roleName = context?.$toolUtil.storageGet("role");
const route = useRoute();
let breadcrumbList = computed(() => {
  let arr = route.matched.map((item) => ({
    name: item.name,
    path: item.path,
  }));
  return arr[arr.length - 1].path == "/" ? [arr[0]] : arr;
});

//获取用户信息
import { useStore } from "vuex";
const store = useStore();
const user = computed(() => store.getters["user/session"]);
const avatar = ref(store.state.user.avatar);
if (!store.state.user.session.id) {
  store.dispatch("user/getSession");
}

// 导航到用户页面
const navigateToUsers = () => {
  router.push("/yonghu");
};

// 导航到帖子页面
const navigateToPosts = () => {
  router.push("/forum");
};

// 导航到统计页面
const navigateToStats = () => {
  router.push("/stats");
};

// 退出登录
const onLogout = () => {
  let toolUtil = context?.$toolUtil;
  store.dispatch("delAllCachedViews");
  store.dispatch("delAllVisitedViews");
  store.dispatch("user/loginOut");
  toolUtil.storageClear();
  router.replace({
    name: "login",
  });
};
// 跳转前台
const frontClick = () => {
  window.open(`${context.$config.url}client/index.html#/index/home`, "_blank");
};
// 个人中心
const centerClick = () => {
  router.push(`/${role}Center`);
};
// 修改密码
const updatepasswordClick = () => {
  router.push("/updatepassword");
};
</script>

<style lang="scss" scoped>
// 总盒子
.top_view {
  // 左边盒子
  .top_left_view {
    // 折叠按钮盒子
    .fold_view {
      // 图标
      .icons {
      }
    }
  }
  // 标题
  .projectTitle {
  }
  // 右部盒子
  .top_right_view {
    // 头像盒子
    .avatar-container {
      .avatar-wrapper {
        // 昵称
        .nickname {
        }
        // 头像
        .user-avatar {
        }
        // 图标
        .el-icon-arrow-down {
        }
      }
    }
  }
}
// 下拉盒子
.el-dropdown-menu {
  // 下拉盒子itme
  :deep(.el-dropdown-menu__item) {
  }
  // item悬浮
  :deep(.el-dropdown-menu__item:hover) {
  }
}
</style>
<style>
.top_view {
  box-sizing: border-box;
  width: 100%;
  display: flex;
  align-items: center;
  height: 80px;
  background: #333;
  color: #fff;
  justify-content: space-between;
  padding: 0px var(--spacing-xl);
  z-index: 1002;
  position: relative;
  box-shadow: var(--shadow);
}

.top_view .top_left_view {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.top_view .logo {
  margin-right: var(--spacing-md);
}

.top_view .projectTitle {
  font-size: var(--font-size-xl);
  color: #fff;
  font-weight: 700;
  margin-left: 0;
}

.top_view .nav-buttons {
  display: flex;
  gap: var(--spacing-md);
  margin-left: var(--spacing-xl);
}

.top_view .nav-buttons .nav-button {
  background: none;
  border: 0;
  padding: 0;
  font-size: 16px;
  color: #fff;
  transition: var(--transition);
  position: relative;
  padding: 0 var(--spacing-lg);
  height: 60px;
  line-height: 60px;
  white-space: nowrap;
  cursor: pointer;

  &::after {
    content: "";
    height: 3px;
    background: var(--primary-color);
    display: block;
    margin-top: -3px;
    transition: transform 0.3s ease;
    transform: scaleX(0);
    transform-origin: 50% 0;
  }

  &:hover {
    color: var(--primary-color);
    background: none;
  }

  &:hover::after {
    transform: scaleX(1);
  }
}

.top_view .top_right_view {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  color: inherit;
}

.top_view .avatar-container {
  cursor: pointer;
}

.top_view .avatar-wrapper {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);

  &:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }
}

.top_view img.user-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  object-fit: cover;
}

.top_view .avatar-wrapper .nickname {
  font-size: 16px;
  color: #fff;
  font-weight: 500;
}

.top_view .avatar-wrapper .el-icon-arrow-down {
  color: #fff;
}

.top_view .el-dropdown-menu {
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-color);
  padding: var(--spacing-xs);
  background: #fff;
}

.top_view .el-dropdown-menu__item {
  padding: var(--spacing-sm) var(--spacing-md);
  margin: var(--spacing-xs);
  border-radius: var(--border-radius-sm);
  font-size: var(--font-size-sm);
  transition: all var(--transition-fast);

  &:focus,
  &:not(.is-disabled):hover {
    color: var(--primary-color);
    background-color: rgba(3, 204, 136, 0.1);
  }
}

.top_view .breadcrumb-view {
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  height: 48px;
  background: var(--card-bg);
  padding: 0 var(--spacing-lg);
  border-top: 1px solid var(--divider-color);
  display: flex;
  align-items: center;
}

.top_view .el-breadcrumb {
  margin: 0;
}

.top_view .el-breadcrumb__inner {
  color: var(--text-secondary) !important;
  font-size: var(--font-size-sm);

  &.is-link {
    color: var(--primary-color) !important;

    &:hover {
      color: darken(var(--primary-color), 10%) !important;
      text-decoration: underline;
    }
  }
}

.top_view .el-breadcrumb__separator {
  color: var(--text-light);
  margin: 0 var(--spacing-sm);
}

.top_view .el-breadcrumb__item {
  height: 48px;
  line-height: 48px;
}
</style>
