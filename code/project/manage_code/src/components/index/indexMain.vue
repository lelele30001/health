<template>
  <div style="height: 100%; display: flex; flex-direction: column">
    <el-main class="main_view index_transition">
      <index-header class="index_header index_transition"> </index-header>
      <index-tags class="index_tags"> </index-tags>
      <router-view class="router-view index_transition"> </router-view>
    </el-main>
  </div>
</template>

<script setup>
import IndexHeader from "@/components/index/indexTop";
import IndexTags from "@/components/index/indexTags";
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
