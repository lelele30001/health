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
                placeholder="输入评论内容进行搜索"
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
      <div class="comment_list">
        <el-table :data="list" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="content" label="评论内容" min-width="300">
            <template #default="scope">
              <div class="comment-content">{{ scope.row.content }}</div>
            </template>
          </el-table-column>
          <el-table-column
            prop="username"
            label="评论者"
            width="120"
          ></el-table-column>
          <el-table-column prop="recipeTitle" label="关联食谱" width="180">
            <template #default="scope">
              <div class="recipe-title">{{ scope.row.recipeTitle }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="addtime" label="评论时间" width="180">
            <template #default="scope">
              <div class="comment-time">
                {{ formatTime(scope.row.addtime) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="auditStatus" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.auditStatus)">
                {{ getStatusText(scope.row.auditStatus) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button
                type="primary"
                size="small"
                @click="infoClick(scope.row.id)"
              >
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
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
      :type="'comment'"
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
const detailVisible = ref(false);
const detailForm = ref({});
const auditDetailRef = ref(null);
const originalList = ref([]);
const total = ref(0);
const layouts = ref(["total", "prev", "pager", "next", "sizes", "jumper"]);
const listLoading = ref(false);

const getList = () => {
  listLoading.value = true;
  console.log("开始获取评论列表...");
  context
    .$http({
      url: `/comment/all`,
      method: "get",
    })
    .then((res) => {
      console.log("获取评论列表成功:", res.data);
      originalList.value = (res.data.data || []).map((item) => {
        return {
          ...item,
          auditStatus: item.status || "pending",
          recipeTitle: "食谱 #" + item.recipeId,
          addtime: item.createTime,
        };
      });
      console.log("处理后的评论列表:", originalList.value);
      applySearchFilter();
      listLoading.value = false;
    })
    .catch((error) => {
      console.error("获取评论列表失败:", error);
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
      return item.content.includes(keyword);
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
  console.log("过滤后的评论列表:", list.value);
  console.log("总评论数:", total.value);
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
    getCommentDetail(id);
  }
};

const getCommentDetail = (id) => {
  // 从列表中找到评论详情
  const comment = originalList.value.find((item) => item.id === id);
  if (comment) {
    detailForm.value = {
      ...comment,
      userId: comment.userId,
      id: comment.id,
      title: comment.recipeTitle || "食谱 #" + comment.recipeId,
    };
    detailVisible.value = true;
  }
};

const handleAuditSuccess = (data) => {
  ElMessage.success(data.message);
  getList();
};

const formatTime = (time) => {
  if (!time) return "";
  return new Date(time).toLocaleString();
};

const getStatusType = (status) => {
  switch (status) {
    case "pending":
      return "info";
    case "approved":
      return "success";
    case "locked":
      return "danger";
    default:
      return "";
  }
};

const getStatusText = (status) => {
  switch (status) {
    case "pending":
      return "待审核";
    case "approved":
      return "已通过";
    case "locked":
      return "已锁定";
    default:
      return status;
  }
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

// 评论列表
.comment_list {
  margin-top: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  background-color: var(--card-bg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  overflow: hidden;

  :deep(.el-table) {
    border: none;

    .el-table__header-wrapper {
      background-color: var(--card-bg);

      th {
        background-color: var(--card-bg);
        border-bottom: 1px solid var(--border-color);
        font-weight: 500;
        color: var(--text-primary);
      }
    }

    .el-table__body-wrapper {
      .el-table__row {
        border-bottom: 1px solid var(--border-color);

        &:nth-child(even) {
          background-color: rgba(0, 0, 0, 0.02);
        }

        &:hover {
          background-color: rgba(3, 204, 136, 0.05);
        }
      }
    }
  }
}

.comment-content {
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.recipe-title {
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.comment-time {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
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

  .comment_list {
    overflow-x: auto;
  }
}
</style>
