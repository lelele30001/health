<template>
  <div>
    <div class="center_view">
      <div class="list_search_view">
        <el-form :model="searchQuery" class="search_form">
          <div class="search_view">
            <div class="search_label">用户名：</div>
            <div class="search_box">
              <el-input
                class="search_inp"
                v-model="searchQuery.username"
                placeholder="用户名"
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
            v-if="btnAuth('users', '新增')"
          >
            新增
          </el-button>
          <el-button
            class="del_btn"
            type="danger"
            :disabled="selRows.length ? false : true"
            @click="delClick(null)"
            v-if="btnAuth('users', '删除')"
          >
            批量删除
          </el-button>
        </div>
      </div>
      <div class="user_list" v-if="btnAuth('users', '查看')">
        <div class="user_grid">
          <div
            v-for="(item, index) in list"
            :key="item.id"
            class="user_item_wrapper"
          >
            <el-checkbox
              v-model="item.checked"
              @change="handleItemCheck(item)"
            ></el-checkbox>
            <div class="user_item" @click="infoClick(item.id)">
              <div class="user_avatar">
                <div v-if="item.touxiang">
                  <el-image
                    v-if="item.touxiang.substring(0, 4) == 'http'"
                    preview-teleported
                    :preview-src-list="[item.touxiang.split(',')[0]]"
                    :src="item.touxiang.split(',')[0]"
                    style="width: 100px; height: 100px"
                  ></el-image>
                  <el-image
                    v-else
                    preview-teleported
                    :preview-src-list="[
                      $config.url + item.touxiang.split(',')[0],
                    ]"
                    :src="$config.url + item.touxiang.split(',')[0]"
                    style="width: 100px; height: 100px"
                  >
                  </el-image>
                </div>
                <div v-else class="no-avatar">无图片</div>
              </div>
              <div class="user_info">
                <div class="user_username">{{ item.username }}</div>
                <div class="user_role">{{ item.role }}</div>
              </div>
            </div>
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
        :page-sizes="[20, 40, 60, 80, 100]"
        @size-change="sizeChange"
        @current-change="currentChange"
      />
    </div>
    <formModel ref="formRef" @formModelChange="formModelChange"></formModel>
    <el-dialog
      :model-value="userDetailVisible"
      @update:model-value="userDetailVisible = $event"
      :title="''"
      width="60%"
      destroy-on-close
      custom-class="modern-dialog"
    >
      <div class="user_detail">
        <h1>{{ userDetail.username }}</h1>
        <div class="user_detail_info">
          <div class="info-item">
            <span class="info-label">角色：</span>
            <span class="info-value">{{ userDetail.role }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">头像：</span>
            <div v-if="userDetail.touxiang" class="avatar-container">
              <el-image
                v-if="userDetail.touxiang.substring(0, 4) == 'http'"
                preview-teleported
                :preview-src-list="[userDetail.touxiang.split(',')[0]]"
                :src="userDetail.touxiang.split(',')[0]"
                style="width: 100px; height: 100px"
              ></el-image>
              <el-image
                v-else
                preview-teleported
                :preview-src-list="[
                  $config.url + userDetail.touxiang.split(',')[0],
                ]"
                :src="$config.url + userDetail.touxiang.split(',')[0]"
                style="width: 100px; height: 100px"
              >
              </el-image>
            </div>
            <div v-else class="no-avatar">无图片</div>
          </div>
        </div>
      </div>
    </el-dialog>
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
//基础信息

const tableName = "users";
const formName = "管理员";
const route = useRoute();
//基础信息
onMounted(() => {
  console.log("用户列表页面加载了修改后的代码");
  console.log("当前页面路径:", window.location.href);
  init();
});
//列表数据
const list = ref(null);
const table = ref(null);
const listQuery = ref({
  page: 1,
  limit: 20,
  sort: "id",
  order: "desc",
});
const searchQuery = ref({});
const selRows = ref([]);
const listLoading = ref(false);
const userDetailVisible = ref(false);
const userDetail = ref({});
//列表
const getList = () => {
  listLoading.value = true;
  let params = JSON.parse(JSON.stringify(listQuery.value));
  params["sort"] = "id";
  params["order"] = "desc";
  if (searchQuery.value.username && searchQuery.value.username != "") {
    params["username"] = "%" + searchQuery.value.username + "%";
  }
  context
    .$http({
      url: `${tableName}/page`,
      method: "get",
      params: params,
    })
    .then((res) => {
      listLoading.value = false;
      console.log("获取到的用户数据:", res.data.data.list);
      console.log("用户数据长度:", res.data.data.list.length);
      list.value = res.data.data.list;
      total.value = Number(res.data.data.total);
    });
};
//删
const delClick = (id) => {
  let ids = [];
  if (id) {
    ids = [id];
  } else {
    if (selRows.value.length) {
      ids = selRows.value.map((row) => row.id);
    } else {
      context?.$toolUtil.message("请选择要删除的用户", "warning");
      return false;
    }
  }
  ElMessageBox.confirm(`是否删除选中的用户`, "提示", {
    confirmButtonText: "是",
    cancelButtonText: "否",
    type: "warning",
  }).then(() => {
    context
      .$http({
        url: `${tableName}/delete`,
        method: "post",
        data: ids,
      })
      .then((res) => {
        if (res.data.code === 0) {
          getList();
          selRows.value = [];
          context?.$toolUtil.message("删除成功", "success");
        } else {
          context?.$toolUtil.message(res.data.msg || "删除失败", "error");
        }
      });
  });
};
//多选
const handleSelectionChange = (e) => {
  selRows.value = e;
};
// 处理单个项目的复选框点击
const handleItemCheck = (item) => {
  if (item.checked) {
    // 添加到选中列表
    if (!selRows.value.some((row) => row.id === item.id)) {
      selRows.value.push(item);
    }
  } else {
    // 从选中列表中移除
    selRows.value = selRows.value.filter((row) => row.id !== item.id);
  }
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

const infoClick = (id = null) => {
  if (id) {
    getUserDetail(id);
  } else if (selRows.value.length) {
    getUserDetail(selRows.value[0].id);
  }
};

const getUserDetail = (id) => {
  context
    .$http({
      url: `${tableName}/info/${id}`,
      method: "get",
    })
    .then((res) => {
      if (res.data.code === 0) {
        userDetail.value = res.data.data;
        userDetailVisible.value = true;
      } else {
        context?.$toolUtil.message(res.data.msg || "获取用户信息失败", "error");
      }
    });
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
//初始化
const init = () => {
  getList();
};
</script>
<style lang="scss" scoped>
// 操作盒子
.list_search_view {
  // 搜索盒子
  .search_form {
    // 子盒子
    .search_view {
      // 搜索label
      .search_label {
      }
      // 搜索item
      .search_box {
        // 输入框
        :deep(.search_inp) {
        }
      }
    }
    // 搜索按钮盒子
    .search_btn_view {
      // 搜索按钮
      .search_btn {
      }
      // 搜索按钮-悬浮
      .search_btn:hover {
      }
    }
  }
  //头部按钮盒子
  .btn_view {
    // 其他
    :deep(.el-button--default) {
    }
    // 其他-悬浮
    :deep(.el-button--default:hover) {
    }
    // 新增
    :deep(.el-button--success) {
    }
    // 新增-悬浮
    :deep(.el-button--success:hover) {
    }
    // 删除
    :deep(.el-button--danger) {
    }
    // 删除-悬浮
    :deep(.el-button--danger:hover) {
    }
    // 统计
    :deep(.el-button--warning) {
    }
    // 统计-悬浮
    :deep(.el-button--warning:hover) {
    }
  }
}
// 用户列表样式
.center_view {
  width: 100%;
  padding: 0 10px;
  box-sizing: border-box;
}

.user_list {
  margin-top: 20px;
  width: 100%;
}

.user_grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr) !important;
  gap: 20px;
  width: 100%;
  box-sizing: border-box;
}

.user_item_wrapper {
  position: relative;
}

.user_item_wrapper .el-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
  z-index: 10;
}

.user_item {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.08);
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  padding-top: 40px;
}

.user_item:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.12);
}

.user_avatar {
  margin-bottom: 15px;
}

.no-avatar {
  width: 100px;
  height: 100px;
  background: #f0f0f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
  margin: 0 auto;
}

.user_info {
  margin-top: 15px;
}

.user_username {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.user_role {
  font-size: 14px;
  color: #666;
}

// 用户详情弹窗样式
.user_detail {
  padding: 30px;
}

.user_detail h1 {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}

.user_detail_info {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.info-label {
  font-size: 16px;
  font-weight: 600;
  color: #666;
  width: 80px;
}

.info-value {
  font-size: 16px;
  color: #333;
}

.avatar-container {
  margin-top: 10px;
}

// 分页器
.el-pagination {
  margin-top: 30px;
  // 总页码
  :deep(.el-pagination__total) {
  }
  // 上一页
  :deep(.btn-prev) {
  }
  // 下一页
  :deep(.btn-next) {
  }
  // 上一页禁用
  :deep(.btn-prev:disabled) {
  }
  // 下一页禁用
  :deep(.btn-next:disabled) {
  }
  // 页码
  :deep(.el-pager) {
    // 数字
    .number {
    }
    // 数字悬浮
    .number:hover {
    }
    // 选中
    .number.is-active {
    }
  }
  // sizes
  :deep(.el-pagination__sizes) {
    display: inline-block;
    vertical-align: top;
    font-size: 13px;
    line-height: 28px;
    height: 28px;
    .el-select {
    }
  }
  // 跳页
  :deep(.el-pagination__jump) {
    // 输入框
    .el-input {
    }
  }
}
</style>
