<template>
  <div class="menu_wrapper">
    <el-scrollbar wrap-class="scrollbar-wrapper" class="menu_scrollbar">
      <div class="user_info">
        <el-image :src="store.getters['user/avatar']"></el-image>
        <div class="user-name">
          <div style="text-align: center">
            {{ $toolUtil.storageGet("adminName") }}
          </div>
        </div>
      </div>
      <el-menu
        :default-openeds="[]"
        :unique-opened="false"
        default-active="0"
        class="menu_view"
        :collapse="collapse"
      >
        <el-menu-item class="first-item" :index="0" @click="menuHandler('')">
          <i class="iconfont icon-zhuye2" v-if="collapse ? false : true"></i>
          <template #title>
            <span>首页</span>
          </template>
        </el-menu-item>
        <template v-for="(item, index) in menuList.backMenu">
          <el-sub-menu
            v-if="item.child.length > 1"
            class="first-item"
            :index="index + 2 + ''"
          >
            <template #title>
              <i
                class="iconfont"
                :class="item.fontClass"
                v-if="collapse ? false : true"
              ></i>
              <span>{{ item.menu }}</span>
            </template>
            <el-menu-item
              class="second-item"
              v-for="(child, sort) in item.child"
              :key="sort"
              :index="index + 2 + '-' + sort"
              @click="
                menuHandler(child.classname || child.tableName, child.menuJump)
              "
              >{{ child.menu }}
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item
            v-else
            class="first-item"
            :index="index + 2 + ''"
            @click="
              menuHandler(
                item.child[0].classname || item.child[0].tableName,
                item.child[0].menuJump
              )
            "
          >
            <i
              class="iconfont"
              :class="item.fontClass"
              v-if="collapse ? false : true"
            ></i>
            <template #title>
              <span>{{ item.child[0].menu }}</span>
            </template>
          </el-menu-item>
        </template>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script setup>
import menu from "@/utils/menu";
import { ref, toRefs, getCurrentInstance, nextTick } from "vue";
import { useStore } from "vuex";
const store = useStore();
const context = getCurrentInstance()?.appContext.config.globalProperties;
//props
const props = defineProps({
  collapse: Boolean,
});
const { collapse } = toRefs(props);
//data
const menuList = ref([]);
const role = ref("");
//权限验证
const btnAuth = (e, a) => {
  return context?.$toolUtil.isAuth(e, a);
};
const init = () => {
  const menus = menu.list();
  if (menus) {
    menuList.value = menus;
  }
  role.value = context?.$toolUtil.storageGet("role");

  for (let i = 0; i < menuList.value.length; i++) {
    if (menuList.value[i].roleName == role.value) {
      menuList.value = menuList.value[i];
      break;
    }
  }
};
const menuHandler = (name, menuJump) => {
  if (name == "center") {
    context.$router.push(`/${role.value}Center`);
  } else if (name == "storeup") {
    context.$router.push(`/storeup?type=${menuJump}`);
  } else if (name == "exampaper" && menuJump == "12") {
    context.$router.push("/exampaperlist");
  } else if (name == "examrecord" && menuJump == "22") {
    context.$router.push("/examfailrecord");
  } else {
    context.$router.push(`/${name}${menuJump ? "?menuJump=" + menuJump : ""}`);
  }
};
init();
</script>

<style lang="scss" scoped>
// 总盒子
:deep(.menu_scrollbar) {
  // 菜单盒子-展开样式
  .menu_view {
    // 无二级菜单
    .el-menu-item {
      .iconfont {
      }
    }

    // 无二级悬浮
    .el-menu-item:hover {
    }

    // 无二级选中
    .el-menu-item.is-active {
    }

    // 有二级盒子
    .el-sub-menu {
      // 有二级item
      .el-sub-menu__title {
        .iconfont {
        }
        .el-sub-menu__icon-arrow {
        }
      }

      // 有二级item悬浮
      .el-sub-menu__title:hover {
      }
    }
    //二级选中
    .is-active {
      .el-sub-menu__title {
      }
    }
    // 二级盒子
    .el-menu--inline {
      // 二级菜单
      .second-item {
      }
      // 二级悬浮
      .second-item:hover {
      }
      // 二级选中
      .is-active.second-item {
      }
    }
  }
  // 菜单盒子-关闭样式
  .el-menu--collapse {
    padding: 0;
    background: none;
    height: 100%;

    // 无二级菜单
    .el-menu-item {
      border: none;
      padding: 0 10px;
      color: #ccc;
      background: none;
      line-height: 50px;
      height: 50px;
    }

    // 无二级悬浮
    .el-menu-item:hover {
      padding: 0 10px;
      color: #ccc;
      background: none;
      line-height: 50px;
      height: 50px;
    }

    // 无二级选中
    .el-menu-item.is-active {
      padding: 0 10px;
      color: #ccc;
      background: none;
      line-height: 50px;
      height: 50px;
    }

    // 有二级盒子
    .el-sub-menu {
      cursor: pointer;
      padding: 0 0;
      white-space: nowrap;
      background: none;
      position: relative;

      // 有二级item
      .el-sub-menu__title {
        border: none;
        padding: 0 10px;
        color: #ccc;
        background: none;
        line-height: 50px;
        height: 50px;
        .el-sub-menu__icon-arrow {
          margin: -3px 0 0 8px;
          color: inherit;
          vertical-align: middle;
          font-size: 12px;
          position: static;
        }
      }

      // 有二级item悬浮
      .el-sub-menu__title:hover {
        padding: 0 10px;
        color: #ccc;
        background: none;
        line-height: 50px;
        height: 50px;
      }
    }
    //二级选中
    .is-active {
      .el-sub-menu__title {
        padding: 0 10px;
        color: #ccc;
        background: none;
        line-height: 50px;
        height: 50px;
      }
    }
    // 二级盒子
    .el-menu--inline {
      border: none;
      padding: 0px;
      background: none;
      // 二级菜单
      .second-item {
        padding: 0 40px;
        color: #666;
        background: none;
        line-height: 50px;
        height: 50px;
      }
      // 二级悬浮
      .second-item:hover {
        padding: 0 40px;
        color: #f00;
        background: greenyellow;
        line-height: 50px;
        height: 50px;
      }
      // 二级选中
      .is-active.second-item {
        padding: 0 40px;
        color: #000;
        background: none;
        line-height: 50px;
        height: 50px;
      }
    }
  }
}
</style>
<style lang="scss">
.el-menu-item .el-menu-tooltip__trigger {
  padding: 0;
}
.el-popper {
  .el-menu--popup-container {
    .el-menu--popup {
      border: none;
      padding: 0px;
      background: none;
      // 二级菜单
      .second-item {
        padding: 0 40px;
        color: #666;
        background: none;
        line-height: 50px;
        height: 50px;
      }
      // 二级悬浮
      .second-item:hover {
        padding: 0 40px;
        color: #f00;
        background: greenyellow;
        line-height: 50px;
        height: 50px;
      }
      // 二级选中
      .is-active.second-item {
        padding: 0 40px;
        color: #000;
        background: none;
        line-height: 50px;
        height: 50px;
      }
    }
  }
}
</style>
<style>
.main-container {
  margin-left: 260px;
  background: var(--bg-color);
  min-height: 100vh;
  transition: all 0.3s ease-in-out 0s;
}

.menu_wrapper {
  width: 260px;
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  background: linear-gradient(135deg, #3a4a58, #2c3e50);
  box-shadow: var(--shadow-md);
  transition: all 0.3s ease-in-out 0s;
  overflow-y: auto;
}

.menu_wrapper .el-scrollbar {
  height: 100%;
}

.menu_wrapper .el-scrollbar__view {
  padding: var(--spacing-lg);
}

.menu_wrapper .user_info {
  text-align: center;
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg) 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.menu_wrapper .user_info .el-image {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  margin: 0 auto var(--spacing-md);
  border: 3px solid rgba(255, 255, 255, 0.2);
  box-shadow: var(--shadow-md);
}

.menu_wrapper .user_info .user-name {
  color: #fff;
  font-size: var(--font-size-md);
  font-weight: 600;
  line-height: 1.4;
  margin-bottom: var(--spacing-xs);
}

.menu_wrapper .user_info .user-role {
  color: rgba(255, 255, 255, 0.7);
  font-size: var(--font-size-xs);
}

.menu_wrapper .menu_view.el-menu {
  border: none;
  background: transparent;
}

.menu_wrapper .el-menu-item {
  color: rgba(255, 255, 255, 0.8);
  border-radius: var(--border-radius-md);
  margin-bottom: var(--spacing-sm);
  height: 48px;
  line-height: 48px;
  padding: 0 var(--spacing-md);
  transition: all var(--transition-fast);

  &:hover {
    background-color: rgba(255, 255, 255, 0.1);
    color: #fff;
  }

  &.is-active {
    background-color: var(--primary-color);
    color: #fff;
  }
}

.menu_wrapper .el-menu-item.first-item {
  padding: 0 var(--spacing-md) !important;
}

.menu_wrapper .el-menu-item i {
  font-size: 18px;
  margin-right: var(--spacing-md);
  color: inherit;
}

.menu_wrapper .el-sub-menu {
  margin-bottom: var(--spacing-sm);

  .el-sub-menu__title {
    color: rgba(255, 255, 255, 0.8);
    border-radius: var(--border-radius-md);
    height: 48px;
    line-height: 48px;
    padding: 0 var(--spacing-md);
    transition: all var(--transition-fast);
    background: transparent;
    border: none;

    &:hover {
      background-color: rgba(255, 255, 255, 0.1);
      color: #fff;
    }

    i {
      font-size: 18px;
      margin-right: var(--spacing-md);
      color: inherit;
    }

    .el-sub-menu__icon-arrow {
      color: rgba(255, 255, 255, 0.6);
      transition: all var(--transition-fast);
    }
  }
}

.menu_wrapper .el-menu--inline {
  background: rgba(255, 255, 255, 0.05);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-xs);
  margin-top: var(--spacing-xs);
  border: none;

  .el-menu-item {
    margin-bottom: var(--spacing-xs);
    padding-left: calc(var(--spacing-md) * 2.5) !important;
    height: 40px;
    line-height: 40px;
    font-size: var(--font-size-sm);

    &:hover {
      background-color: rgba(255, 255, 255, 0.15);
    }

    &.is-active {
      background-color: rgba(3, 204, 136, 0.8);
    }
  }
}

.menu_wrapper .el-scrollbar__bar.is-vertical {
  width: 4px;

  .el-scrollbar__thumb {
    background-color: rgba(255, 255, 255, 0.3);
    border-radius: 2px;

    &:hover {
      background-color: rgba(255, 255, 255, 0.5);
    }
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .main-container {
    margin-left: 0;
  }

  .menu_wrapper {
    transform: translateX(-100%);
    z-index: 1100;
  }

  .menu_wrapper.open {
    transform: translateX(0);
  }
}
</style>
