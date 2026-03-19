<template>
  <div class="top_view">
    <div class="top_left_view">
      <div
        class="fold_view"
        @click="toggleClick"
        :class="{ is_collapse: collapse }"
      >
        <el-icon class="icons">
          <Fold v-if="!collapse" />
          <Expand v-else />
        </el-icon>
      </div>
    </div>

    <div class="projectTitle">美食推荐分享系统</div>
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
import {
  toRefs,
  defineEmits,
  getCurrentInstance,
  ref,
  onBeforeUnmount,
  computed,
} from "vue";

import { useRouter, useRoute } from "vue-router";
const router = useRouter();
const context = getCurrentInstance()?.appContext.config.globalProperties;
const emit = defineEmits(["collapseChange"]);
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

const props = defineProps({
  collapse: Boolean,
});
const { collapse } = toRefs(props);

//获取用户信息
import { useStore } from "vuex";
const store = useStore();
const user = computed(() => store.getters["user/session"]);
const avatar = ref(store.state.user.avatar);
if (!store.state.user.session.id) {
  store.dispatch("user/getSession");
}
const toggleClick = () => {
  emit("collapseChange");
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
  router.push(`/updatepassword`);
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
  display: flex;
  align-items: center;
  background: var(--card-bg);
  height: 64px;
  width: 100%;
  border: none;
  box-shadow: var(--shadow-sm);
  padding: 0 var(--spacing-lg);
  justify-content: space-between;
  flex-wrap: nowrap;
  position: relative;
  z-index: 999;
}

.top_view .top_left_view {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.top_view .fold_view {
  cursor: pointer;
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-md);
  transition: all var(--transition-fast);

  &:hover {
    background-color: var(--divider-color);
  }

  .icons {
    font-size: 20px;
    color: var(--text-secondary);
  }

  &.is_collapse {
    transform: rotate(180deg);
  }
}

.top_view .projectTitle {
  display: block;
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--primary-color);
  margin-left: var(--spacing-md);
}

.top_view .top_right_view {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
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
    background-color: var(--divider-color);
  }
}

.top_view img.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  object-fit: cover;
}

.top_view .avatar-wrapper .nickname {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  font-weight: 500;
}

.top_view .avatar-wrapper i {
  font-size: 14px;
  color: var(--text-light);
  transition: all var(--transition-fast);
}

.top_view .el-dropdown-menu {
  border-radius: var(--border-radius-md);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-color);
  padding: var(--spacing-xs);
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
