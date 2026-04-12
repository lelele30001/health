<template>
  <div>
    <div class="center_view">
      <div class="list_search_view">
        <el-form :model="searchQuery" class="search_form">
          <div class="search_view">
            <div class="search_label">帖子标题：</div>
            <div class="search_box">
              <el-input
                class="search_inp"
                v-model="searchQuery.title"
                placeholder="帖子标题"
                clearable
              >
              </el-input>
            </div>
          </div>
          <div class="search_view">
            <div class="search_label">帖子内容：</div>
            <div class="search_box">
              <el-input
                class="search_inp"
                v-model="searchQuery.content"
                placeholder="帖子内容"
                clearable
              >
              </el-input>
            </div>
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
        <div class="btn_view">
          <el-button
            class="add_btn"
            type="success"
            @click="addClick"
            v-if="btnAuth('forum', '新增')"
          >
            新增
          </el-button>
          <el-button
            class="del_btn"
            type="danger"
            :disabled="selRows.length ? false : true"
            @click="delClick(null)"
            v-if="btnAuth('forum', '删除')"
          >
            删除
          </el-button>
        </div>
      </div>
      <div class="forum_list" v-if="btnAuth('forum', '查看')">
        <div class="forum_grid">
          <forum-item
            v-for="(item, index) in list"
            :key="item.id"
            :item="item"
            :index="index"
            :userid="user?.id || 0"
            :forum-show-index="forumShowIndex"
            :btn-auth="btnAuth"
            @mouseenter="forumShowIndex = index"
            @mouseleave="forumShowIndex = -1"
            @click="infoClick(item.id)"
            @edit="editClick"
            @del="delClick"
          />
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
    <formModel ref="formRef" @formModelChange="formModelChange"></formModel>
    <forum-detail
      ref="forumDetailRef"
      :visible="detailVisible"
      :detail-form="detailForm"
      :userid="user?.id || 0"
      :like-count="likeCount"
      :collect-count="collectCount"
      :dislike-count="dislikeCount"
      :is-liked="isLiked"
      :is-collected="isCollected"
      :is-disliked="isDisliked"
      :btn-auth="btnAuth"
      @update:visible="detailVisible = $event"
      @del="delClick"
      @record-behavior="recordBehavior"
      @add-comment="addComment"
      @del-comment="delComment"
      @add-reply="addReply"
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
import { ElMessageBox } from "element-plus";
import { useStore } from "vuex";
const store = useStore();
const user = computed(() => store.getters["user/session"]);
const avatar = ref(store.state.user.avatar);
const context = getCurrentInstance()?.appContext.config.globalProperties;
import formModel from "./formModel.vue";
import ForumItem from "@/components/ForumItem.vue";
import ForumDetail from "@/components/ForumDetail.vue";
//基础信息

const tableName = "forum";
const formName = "美食论坛";
const route = useRoute();
//基础信息
onMounted(() => {});
//列表数据
const list = ref(null);
const table = ref(null);
const listQuery = ref({
  page: 1,
  limit: 20,
  sort: "addtime",
  order: "desc",
});
const searchQuery = ref({});
const selRows = ref([]);
const listLoading = ref(false);
const forumShowIndex = ref(-1);
const detailVisible = ref(false);
const detailForm = ref({});
const likeCount = ref(0);
const collectCount = ref(0);
const dislikeCount = ref(0);
const isLiked = ref(false);
const isCollected = ref(false);
const isDisliked = ref(false);
const forumDetailRef = ref(null);

const getList = () => {
  listLoading.value = true;
  context
    .$http({
      url: `/recipe/public`,
      method: "get",
    })
    .then((res) => {
      console.log("获取帖子列表成功:", res.data);
      list.value = res.data.recipes;
      total.value = res.data.recipes.length;
      listLoading.value = false;
    })
    .catch((error) => {
      console.error("获取帖子列表失败:", error);
      listLoading.value = false;
    });
};
const delClick = (id) => {
  let ids = ref([]);
  if (id) {
    ids.value = [id];
  } else {
    if (selRows.value.length) {
      for (let x in selRows.value) {
        ids.value.push(selRows.value[x].id);
      }
    } else {
      return false;
    }
  }
  ElMessageBox.confirm(`是否删除选中${formName}`, "提示", {
    confirmButtonText: "是",
    cancelButtonText: "否",
    type: "warning",
  }).then(() => {
    context
      .$http({
        url: `${tableName}/delete`,
        method: "post",
        data: ids.value,
      })
      .then((res) => {
        context?.$toolUtil.message("删除成功", "success", () => {
          getList();
        });
      });
  });
};
//多选
const handleSelectionChange = (e) => {
  selRows.value = e;
};
//列表数据
//分页
const total = ref(0);
const layouts = ref(["total", "prev", "pager", "next", "sizes", "jumper"]);
const sizeChange = (size) => {
  listQuery.value.limit = size;
  getList();
};
const currentChange = (page) => {
  listQuery.value.page = page;
  getList();
};
//分页
//权限验证
const btnAuth = (e, a) => {
  return context?.$toolUtil.isAuth(e, a);
};
//搜索
const searchClick = () => {
  listQuery.value.page = 1;
  getList();
};
//表单
const formRef = ref(null);
const formModelChange = () => {
  searchClick();
};
const addClick = () => {
  formRef.value.init();
};
const editClick = (id = null) => {
  if (id) {
    formRef.value.init(id, "edit");
    return;
  }
  if (selRows.value.length) {
    formRef.value.init(selRows.value[0].id, "edit");
  }
};

const infoClick = (id = null) => {
  if (id) {
    getForumDetail(id);
  } else if (selRows.value.length) {
    getForumDetail(selRows.value[0].id);
  }
};

const getForumDetail = (id) => {
  context
    .$http({
      url: `/recipe/info/${id}`,
      method: "get",
    })
    .then((res) => {
      detailForm.value = res.data.recipe;
      // 初始化统计数据
      likeCount.value = 0;
      collectCount.value = 0;
      dislikeCount.value = 0;
      isLiked.value = false;
      isCollected.value = false;
      isDisliked.value = false;
      detailVisible.value = true;
    });
};

const recordBehavior = (type) => {
  // 这里可以添加行为记录逻辑
  console.log("Record behavior:", type);
};

const addComment = (content) => {
  // 这里可以添加评论逻辑
  console.log("Add comment:", content);
};

const delComment = (id) => {
  // 这里可以添加删除评论逻辑
  console.log("Delete comment:", id);
};

const addReply = (commentId, content) => {
  // 这里可以添加回复评论逻辑
  console.log("Add reply:", commentId, content);
};
// 表单
// 预览文件
const preClick = (file) => {
  if (!file) {
    context?.$toolUtil.message("文件不存在", "error");
  }
  window.open(context?.$config.url + file);
};
// 下载文件
const download = (file) => {
  if (!file) {
    context?.$toolUtil.message("文件不存在", "error");
  }
  let arr = file.replace(new RegExp("file/", "g"), "");
  axios
    .get(
      (location.href.split(context?.$config.name).length > 1
        ? location.href.split(context?.$config.name)[0]
        : "") +
        context?.$config.name +
        "/file/download?fileName=" +
        arr,
      {
        headers: {
          token: context?.$toolUtil.storageGet("Token"),
        },
        responseType: "blob",
      },
    )
    .then(({ data }) => {
      const binaryData = [];
      binaryData.push(data);
      const objectUrl = window.URL.createObjectURL(
        new Blob(binaryData, {
          type: "application/pdf;chartset=UTF-8",
        }),
      );
      const a = document.createElement("a");
      a.href = objectUrl;
      a.download = arr;
      // a.click()
      // 下面这个写法兼容火狐
      a.dispatchEvent(
        new MouseEvent("click", {
          bubbles: true,
          cancelable: true,
          view: window,
        }),
      );
      window.URL.revokeObjectURL(data);
    });
};
const setTop = (row) => {
  let o_isTop = row.isTop;
  if (row.isTop == 0) {
    row.topTime = row.addtime;
    row.isTop = 1;
  } else {
    row.topTime = context.$toolUtil.getCurDateTime();
    row.isTop = 0;
  }
  context.$http.post(`${tableName}/update`, row).then((res) => {
    if (res.data.code == 0) {
      if (o_isTop == 0 || o_isTop == 1) {
        context.$message.success("操作成功");
      }
      searchClick();
    }
  });
};
//初始化
const init = () => {
  getList();
};
init();
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

  //头部按钮盒子
  .btn_view {
    display: flex;
    gap: var(--spacing-md);

    // 新增
    :deep(.el-button--success) {
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

    // 删除
    :deep(.el-button--danger) {
      background-color: #f5222d;
      border-color: #f5222d;
      border-radius: var(--border-radius-md);
      font-size: var(--font-size-sm);
      padding: 8px 16px;
      transition: all var(--transition-normal);

      &:hover {
        background-color: #cf1322;
        border-color: #cf1322;
        transform: translateY(-1px);
        box-shadow: var(--shadow-sm);
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

    .btn_view {
      width: 100%;
      justify-content: flex-start;
    }
  }

  .forum_grid {
    grid-template-columns: 1fr;
  }
}
</style>
