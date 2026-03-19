<template>
  <div id="tags-view-container" class="tags-view-container">
    <scroll-pane ref="scrollPane" class="tags-view-wrapper">
      <router-link
        v-for="tag in visitedViews"
        ref="tag"
        :key="tag.path"
        :class="isActive(tag) ? 'active' : ''"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
        tag="span"
        class="tags-view-item"
        @click.middle.native="closeSelectedTag(tag)"
        @contextmenu.prevent.native="openMenu(tag, $event)"
      >
        {{ tag.name }}
        <el-icon
          class="el-icon-close"
          v-if="!tag.meta.affix"
          @click.prevent.stop="closeSelectedTag(tag)"
        >
          <Close />
        </el-icon>
      </router-link>
    </scroll-pane>
    <ul
      v-show="visible"
      :style="{ left: left + 'px', top: top + 'px' }"
      class="contextmenu"
    >
      <li
        v-if="!(selectedTag.meta && selectedTag.meta.affix)"
        @click="closeSelectedTag(selectedTag)"
      >
        Close
      </li>
      <li @click="closeAllTags(selectedTag)">Close All</li>
    </ul>
  </div>
</template>

<script>
import ScrollPane from "./indexScrollPane";
import { useStore } from "vuex";

export default {
  components: {
    ScrollPane,
  },
  data() {
    return {
      visible: false,
      top: 0,
      left: 0,
      selectedTag: {},
      affixTags: [],
    };
  },
  computed: {
    visitedViews() {
      return this.$store.state.visitedViews;
    },
    routes() {
      return this.$store.state.routes;
    },
  },
  watch: {
    $route() {
      this.addTags();
      this.moveToCurrentTag();
    },
    visible(value) {
      if (value) {
        document.body.addEventListener("click", this.closeMenu);
      } else {
        document.body.removeEventListener("click", this.closeMenu);
      }
    },
  },
  mounted() {
    this.$store.dispatch("updateSideMenus");
    this.initTags();
    this.addTags();
  },
  methods: {
    isActive(route) {
      return route.path === this.$route.path;
    },
    filterAffixTags(routes, basePath = "/") {
      let tags = [];
      routes.forEach((route) => {
        if (route.meta && route.meta.affix) {
          tags.push({
            fullPath: "/",
            path: "/",
            name: route.name,
            meta: {
              ...route.meta,
            },
          });
        }
        if (route.children) {
          const tempTags = this.filterAffixTags(route.children, route.path);
          if (tempTags.length >= 1) {
            tags = [...tags, ...tempTags];
          }
        }
      });
      return tags;
    },
    initTags() {
      const affixTags = (this.affixTags = this.filterAffixTags(this.routes));
      for (const tag of affixTags) {
        // Must have tag name
        if (tag.name) {
          this.$store.dispatch("addVisitedView", tag);
        }
      }
    },
    addTags() {
      const { name } = this.$route;
      if (name) {
        this.$store.dispatch("addView", this.$route);
      }
      return false;
    },
    moveToCurrentTag() {
      const tags = this.$refs.tag;
      this.$nextTick(() => {
        for (const tag of tags) {
          if (tag.to.path === this.$route.path) {
            this.$refs.scrollPane.moveToTarget(tag);
            // when query is different then update
            if (tag.to.fullPath !== this.$route.fullPath) {
              this.$store.dispatch("updateVisitedView", this.$route);
            }
            break;
          }
        }
      });
    },
    closeSelectedTag(view) {
      this.$store.dispatch("delView", view).then(({ visitedViews }) => {
        if (this.isActive(view)) {
          this.toLastView(visitedViews, view);
        }
      });
    },
    closeAllTags(view) {
      this.$store.dispatch("delAllViews").then(({ visitedViews }) => {
        if (this.affixTags.some((tag) => tag.path === view.path)) {
          return;
        }
        this.toLastView(visitedViews, view);
      });
    },
    toLastView(visitedViews, view) {
      const latestView = visitedViews.slice(-1)[0];
      if (latestView.name == "首页") {
        this.$router.push("/");
        return false;
      }
      if (latestView) {
        this.$router.push(latestView);
      } else {
        // now the default is to redirect to the home page if there is no tags-view,
        // you can adjust it according to your needs.
        if (view.name === "首页") {
          // to reload home page
          this.$router.replace({
            path: "/redirect" + view.fullPath,
          });
        } else {
          this.$router.push("/");
        }
      }
    },
    openMenu(tag, e) {
      const menuMinWidth = 105;
      const offsetLeft = this.$el.getBoundingClientRect().left; // container margin left
      const offsetWidth = this.$el.offsetWidth; // container width
      const maxLeft = offsetWidth - menuMinWidth; // left boundary
      const left = e.clientX - offsetLeft + 15; // 15: margin right

      if (left > maxLeft) {
        this.left = maxLeft;
      } else {
        this.left = left;
      }

      this.top = 36;
      this.visible = true;
      this.selectedTag = tag;
    },
    closeMenu() {
      this.visible = false;
    },
  },
};
</script>

<style lang="scss" scoped>
.tags-view-container {
  width: 100%;
  background: var(--card-bg);
  border-bottom: 1px solid var(--divider-color);
  height: 44px;
  overflow: hidden;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);

  .tags-view-wrapper {
    display: flex;
    align-items: center;
    height: 100%;
    padding: 0 var(--spacing-md);
    overflow-x: auto;
    overflow-y: hidden;

    /* 隐藏滚动条但保留功能 */
    &::-webkit-scrollbar {
      height: 4px;
    }

    &::-webkit-scrollbar-track {
      background: var(--divider-color);
      border-radius: 2px;
    }

    &::-webkit-scrollbar-thumb {
      background: var(--text-light);
      border-radius: 2px;

      &:hover {
        background: var(--text-secondary);
      }
    }

    .tags-view-item {
      display: inline-flex;
      align-items: center;
      padding: 6px 12px;
      margin-right: var(--spacing-sm);
      border-radius: var(--border-radius-md);
      background-color: var(--divider-color);
      color: var(--text-secondary);
      font-size: var(--font-size-sm);
      transition: all var(--transition-fast);
      cursor: pointer;
      white-space: nowrap;

      &:hover {
        background-color: rgba(3, 204, 136, 0.1);
        color: var(--primary-color);
      }

      &.active {
        background-color: var(--primary-color);
        color: white;

        .el-icon-close {
          color: white;

          &:hover {
            background-color: rgba(255, 255, 255, 0.2);
          }
        }
      }
    }
  }

  .contextmenu {
    position: fixed;
    background: var(--card-bg);
    border-radius: var(--border-radius-md);
    box-shadow: var(--shadow-md);
    border: 1px solid var(--border-color);
    padding: var(--spacing-xs);
    z-index: 1000;

    li {
      padding: var(--spacing-sm) var(--spacing-md);
      border-radius: var(--border-radius-sm);
      cursor: pointer;
      transition: all var(--transition-fast);

      &:hover {
        background-color: rgba(3, 204, 136, 0.1);
        color: var(--primary-color);
      }
    }
  }
}
</style>

<style lang="scss">
a {
  text-decoration-line: none;
}

// 标签页关闭按钮样式
.tags-view-item {
  .el-icon-close {
    width: 16px;
    height: 16px;
    vertical-align: middle;
    border-radius: 50%;
    text-align: center;
    transition: all var(--transition-fast);
    margin-left: var(--spacing-sm);
    padding: 2px;

    &:hover {
      background-color: rgba(0, 0, 0, 0.1);
    }
  }
}
</style>
