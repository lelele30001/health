<template>
  <div>
    <div class="center_view">
      <div class="list_search_view">
        <el-form :model="searchQuery" class="search_form">
          <div class="search_view">
            <div class="search_label">搜索：</div>
            <div class="search_box">
              <el-input
                class="search_inp"
                v-model="searchQuery.keyword"
                placeholder="输入标题或内容进行搜索"
                clearable
              >
              </el-input>
            </div>
          </div>
          <div class="status_filter">
            <div class="search_label">状态：</div>
            <el-select
              v-model="searchQuery.status"
              placeholder="选择状态"
              clearable
            >
              <el-option label="待审核" value="pending"></el-option>
              <el-option label="已通过" value="approved"></el-option>
              <el-option label="已锁定" value="locked"></el-option>
            </el-select>
          </div>
          <div class="search_btn_view">
            <el-button
              class="search_btn"
              type="primary"
              @click="searchClick()"
              size="small"
              >搜索</el-button
            >
          </div>
        </el-form>
      </div>
      <div class="tab_view">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="待审核" name="pending"></el-tab-pane>
          <el-tab-pane label="已处理" name="processed"></el-tab-pane>
        </el-tabs>
      </div>
      <div class="forum_list">
        <div class="forum_grid">
          <div
            v-for="(item, index) in list"
            :key="item.id"
            class="forum_item_wrapper"
          >
            <forum-item
              :item="item"
              :index="index"
              :userid="user?.id || 0"
              :forum-show-index="forumShowIndex"
              :btn-auth="btnAuth"
              @click="infoClick(item.id)"
            />
          </div>
        </div>
      </div>
      <el-pagination
        background
        :layout="layouts.join(',')"
        :total="total"
        :page-size="listQuery.limit"
        v-model:current-page="listQuery.page"
        prev-text="➨"
        next-text="➨"
        :hide-on-single-page="false"
        :style="{}"
        :page-sizes="[10, 20, 30, 40, 50, 100]"
        @size-change="sizeChange"
        @current-change="currentChange"
      />
    </div>
    <audit-detail
      ref="auditDetailRef"
      :visible="detailVisible"
      :detail-form="detailForm"
      :type="'recipe'"
      @update:visible="detailVisible = $event"
      @audit-success="handleAuditSuccess"
    />
  </div>
</template>
<script setup>
import axios from "axios";
import {
  reactive,
  ref,
  getCurrentInstance,
  nextTick,
  onMounted,
  watch,
  computed,
} from "vue";
import { useRoute, useRouter } from "vue-router";
import { ElMessageBox, ElMessage } from "element-plus";
import { useStore } from "vuex";
const store = useStore();
const user = computed(() => store.getters["user/session"]);
const avatar = ref(store.state.user.avatar);
const context = getCurrentInstance()?.appContext.config.globalProperties;
import ForumItem from "@/components/ForumItem.vue";
import AuditDetail from "@/components/AuditDetail.vue";

const activeTab = ref("pending");
const list = ref(null);
const table = ref(null);
const listQuery = ref({
  page: 1,
  limit: 20,
  sort: "addtime",
  order: "desc",
});
const searchQuery = ref({
  keyword: "",
  status: "",
});
const forumShowIndex = ref(-1);
const detailVisible = ref(false);
const detailForm = ref({});
const auditDetailRef = ref(null);
const originalList = ref([]);
const total = ref(0);
const layouts = ref(["total", "prev", "pager", "next", "sizes", "jumper"]);
const listLoading = ref(false);

const getList = () => {
  listLoading.value = true;
  context
    .$http({
      url: `/recipe/all`,
      method: "get",
    })
    .then((res) => {
      console.log("获取帖子列表成功:", res.data);
      // 处理帖子列表数据，确保cover_image字段存在
      originalList.value = (res.data.recipes || []).map((item) => {
        return {
          ...item,
          // 确保cover_image字段存在
          cover_image: item.coverImage || item.cover_image || "",
          // 使用状态字段作为审核状态
          auditStatus: item.status || "pending",
        };
      });
      // 应用搜索过滤
      applySearchFilter();
      listLoading.value = false;
    })
    .catch((error) => {
      console.error("获取帖子列表失败:", error);
      listLoading.value = false;
    });
};

const applySearchFilter = () => {
  const keyword = searchQuery.value.keyword.trim();
  const status = searchQuery.value.status;

  let filteredList = [...originalList.value];

  // 应用关键词过滤
  if (keyword) {
    filteredList = filteredList.filter((item) => {
      if (!item) return false;
      const title = item.title || "";
      const description = item.description || "";
      return title.includes(keyword) || description.includes(keyword);
    });
  }

  // 应用状态过滤
  if (status) {
    filteredList = filteredList.filter((item) => item.auditStatus === status);
  }

  // 应用标签过滤
  if (activeTab.value === "pending") {
    list.value = filteredList.filter((item) => item.auditStatus === "pending");
  } else {
    list.value = filteredList.filter((item) => item.auditStatus !== "pending");
  }

  total.value = list.value.length;
};

const handleTabClick = (tab) => {
  activeTab.value = tab.props.name;
  applySearchFilter();
};

const searchClick = () => {
  listQuery.value.page = 1;
  applySearchFilter();
};

const sizeChange = (size) => {
  listQuery.value.limit = size;
  getList();
};

const currentChange = (page) => {
  listQuery.value.page = page;
  getList();
};

const btnAuth = (e, a) => {
  return context?.$toolUtil.isAuth(e, a);
};

const infoClick = (id = null) => {
  if (id) {
    getRecipeDetail(id);
  }
};

const getRecipeDetail = (id) => {
  context
    .$http({
      url: `/recipe/info/${id}`,
      method: "get",
    })
    .then((res) => {
      detailForm.value = res.data.recipe;
      // 确保审核状态存在
      detailForm.value.auditStatus = detailForm.value.auditStatus || "pending";
      detailVisible.value = true;
    });
};

const handleAuditSuccess = (data) => {
  ElMessage.success(data.message);
  getList();
};

const init = () => {
  getList();
};

onMounted(() => {
  init();
});
</script>
<style lang="scss" scoped>
.center_view {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-xl) var(--spacing-lg);
}

// 操作盒子
.list_search_view {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-lg);
  background-color: var(--card-bg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);

  // 搜索盒子
  .search_form {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);

    // 子盒子
    .search_view {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);

      // 搜索label
      .search_label {
        font-size: var(--font-size-md);
        color: var(--text-secondary);
        font-weight: 500;
      }

      // 搜索item
      .search_box {
        // 输入框
        :deep(.search_inp) {
          width: 200px;

          .el-input__inner {
            border-radius: var(--border-radius-md);
            border-color: var(--border-color);
            transition: all var(--transition-normal);

            &:focus {
              border-color: var(--primary-color);
              box-shadow: 0 0 0 2px rgba(3, 204, 136, 0.1);
            }
          }
        }
      }
    }

    .status_filter {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);

      .search_label {
        font-size: var(--font-size-md);
        color: var(--text-secondary);
        font-weight: 500;
      }

      :deep(.el-select) {
        width: 120px;

        .el-select__inner {
          border-radius: var(--border-radius-md);
          border-color: var(--border-color);
          transition: all var(--transition-normal);

          &:focus {
            border-color: var(--primary-color);
            box-shadow: 0 0 0 2px rgba(3, 204, 136, 0.1);
          }
        }
      }
    }

    // 搜索按钮盒子
    .search_btn_view {
      // 搜索按钮
      .search_btn {
        background-color: var(--primary-color);
        border-color: var(--primary-color);
        border-radius: var(--border-radius-md);
        font-size: var(--font-size-sm);
        padding: 8px 16px;
        transition: all var(--transition-normal);

        &:hover {
          background-color: #02b37a;
          border-color: #02b37a;
          transform: translateY(-1px);
          box-shadow: var(--shadow-sm);
        }
      }
    }
  }
}

.tab_view {
  margin-bottom: var(--spacing-lg);

  :deep(.el-tabs) {
    .el-tabs__header {
      margin-bottom: var(--spacing-lg);

      .el-tabs__nav {
        border-bottom: 1px solid var(--border-color);

        .el-tabs__item {
          font-size: var(--font-size-md);
          color: var(--text-secondary);
          padding: 10px 20px;
          margin-right: 20px;

          &:hover {
            color: var(--primary-color);
          }

          &.is-active {
            color: var(--primary-color);
            font-weight: 500;
            border-bottom: 2px solid var(--primary-color);
          }
        }
      }
    }
  }
}

// 帖子列表
.forum_list {
  margin-top: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.forum_grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: var(--spacing-lg);
}

.forum_item_wrapper {
  position: relative;
}

// 分页器
.el-pagination {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
  align-items: center;

  // 总页码
  :deep(.el-pagination__total) {
    color: var(--text-secondary);
    margin-right: var(--spacing-md);
  }

  // 页码
  :deep(.el-pager) {
    // 数字
    .number {
      border-radius: var(--border-radius-sm);
      margin: 0 4px;

      &:hover {
        color: var(--primary-color);
      }

      &.is-active {
        background-color: var(--primary-color);
        color: white;
      }
    }
  }

  // 上一页/下一页
  :deep(.btn-prev),
  :deep(.btn-next) {
    border-radius: var(--border-radius-sm);
    margin: 0 4px;

    &:hover {
      color: var(--primary-color);
    }
  }
}

@media (max-width: 768px) {
  .center_view {
    padding: var(--spacing-lg) var(--spacing-md);
  }

  .list_search_view {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);

    .search_form {
      flex-wrap: wrap;
      width: 100%;
    }
  }

  .forum_grid {
    grid-template-columns: 1fr;
  }
}
</style>
