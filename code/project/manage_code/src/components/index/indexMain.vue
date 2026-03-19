<template>
  <div style="height: 100%; display: flex">
    <index-aside
      :collapse="collapse"
      :class="collapse ? 'index-aside-collapse' : 'index-aside'"
    ></index-aside>
    <el-main
      class="main_view main-container index_transition"
      :class="collapse ? 'main_view-collapse' : ''"
    >
      <index-header
        class="index_header index_transition"
        :collapse="collapse"
        @collapseChange="collapseChange"
      >
      </index-header>
      <index-tags class="index_tags"> </index-tags>
      <router-view class="router-view index_transition"> </router-view>
    </el-main>
  </div>
</template>

<script setup>
import IndexAside from "@/components/index/indexMenu";
import IndexHeader from "@/components/index/indexTop";
import IndexTags from "@/components/index/indexTags";
import menu from "@/utils/menu";
import router from "../../router";
import { ref, getCurrentInstance } from "vue";
const context = getCurrentInstance()?.appContext.config.globalProperties;
const collapse = ref(false);
const collapseChange = () => {
  collapse.value = !collapse.value;
};
const menuList = ref(null);
const role = ref("");
const init = () => {
  const menus = menu.list();
  if (menus) {
    menuList.value = menus;
  }
  role.value = context?.$toolUtil.storageGet("role");
  for (let i = 0; i < menuList.value.length; i++) {
    if (menuList.value[i].roleName == role.value) {
      menuList.value = menuList.value[i].backMenu;
      break;
    }
  }
  let arr = makeMenu(menuList.value);

  router.addRoute(arr);
};
const makeMenu = (menu) => {
  let brr = {
    path: "/1",
    component: () => import("../../views/index"),
    children: [],
  };
  for (let x in menu) {
    for (let i in menu[x].child) {
      brr.children.push({
        path: "/" + menu[x].child[i].tableName,
        name: menu[x].child[i].menu,
        component: () =>
          import(`../../views/${menu[x].child[i].tableName}/list.vue`),
      });
    }
  }
  return brr;
};
// init()
</script>
<style lang="scss" scoped>
.el-main {
  padding: 0;
  margin: 0;
  overflow: hidden;
}

.main_view {
  position: relative;
  flex: 1;
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  background-color: var(--bg-color);
}

.main_view-collapse {
  margin-left: 0 !important;
  transition: all 0.3s ease-in-out 0s;
}

.index-aside {
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 1000;
  transition: all 0.3s ease-in-out 0s;
}

.index-aside-collapse {
  width: 0;
  overflow: hidden;
  transition: all 0.3s ease-in-out 0s;
}

.index_header {
  width: 100%;
  z-index: 999;
  flex-shrink: 0;
}

.index_tags {
  width: 100%;
  z-index: 998;
  flex-shrink: 0;
}

.router-view {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-lg);
}

.index_transition {
  transition: all 0.3s ease-in-out 0s;
}
</style>
