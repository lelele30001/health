<template>
  <div>
    <div class="center_view">
      <div class="list_search_view">
        <el-form :model="searchQuery" class="search_form">
          <div class="search_view">
            <div class="search_label">用户账号：</div>
            <div class="search_box">
              <el-input
                class="search_inp"
                v-model="searchQuery.yonghuzhanghao"
                placeholder="用户账号"
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
            v-if="btnAuth('yonghu', '新增')"
          >
            新增
          </el-button>
          <el-button
            class="del_btn"
            type="danger"
            :disabled="selRows.length ? false : true"
            @click="delClick(null)"
            v-if="btnAuth('yonghu', '删除')"
          >
            批量删除
          </el-button>
        </div>
      </div>
      <div class="dataList" v-if="btnAuth('yonghu', '查看')">
        <div class="item" v-for="(item, index) in list" :key="item.id">
          <el-checkbox
            v-model="item.checked"
            @change="handleItemCheck(item)"
          ></el-checkbox>
          <template v-if="item.touxiang">
            <el-image
              v-if="item.touxiang.substring(0, 4) == 'http'"
              preview-teleported
              :preview-src-list="[item.touxiang.split(',')[0]]"
              :src="item.touxiang.split(',')[0]"
            ></el-image>
            <el-image
              v-else
              preview-teleported
              :preview-src-list="[$config.url + item.touxiang.split(',')[0]]"
              :src="$config.url + item.touxiang.split(',')[0]"
            >
            </el-image>
          </template>
          <div class="user_info" @click="infoClick(item.id)">
            <div class="user_username">{{ item.yonghuzhanghao }}</div>
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
      :title="'用户信息'"
      width="30%"
      destroy-on-close
      custom-class="modern-dialog"
    >
      <div class="user_detail">
        <div class="user_detail_header">
          <h1>{{ userDetail.yonghuzhanghao }}</h1>
        </div>
        <div class="user_detail_content">
          <div class="user_detail_header_info">
            <div class="avatar_section">
              <div v-if="userDetail.touxiang" class="avatar-container">
                <el-image
                  v-if="userDetail.touxiang.substring(0, 4) == 'http'"
                  preview-teleported
                  :preview-src-list="[userDetail.touxiang.split(',')[0]]"
                  :src="userDetail.touxiang.split(',')[0]"
                  style="width: 80px; height: 80px"
                ></el-image>
                <el-image
                  v-else
                  preview-teleported
                  :preview-src-list="[
                    $config.url + userDetail.touxiang.split(',')[0],
                  ]"
                  :src="$config.url + userDetail.touxiang.split(',')[0]"
                  style="width: 80px; height: 80px"
                >
                </el-image>
              </div>
              <div v-else class="no-avatar">无图片</div>
            </div>
            <div class="basic_info">
              <div class="info-row">
                <div class="info-item">
                  <span class="info-label">用户账号：</span>
                  <span class="info-value">{{
                    userDetail.yonghuzhanghao
                  }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">用户姓名：</span>
                  <span class="info-value">{{
                    userDetail.yonghuxingming || "无"
                  }}</span>
                </div>
              </div>
              <div class="info-row">
                <div class="info-item">
                  <span class="info-label">性别：</span>
                  <span class="info-value">{{
                    userDetail.xingbie || "无"
                  }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">手机号：</span>
                  <span class="info-value">{{
                    userDetail.shoujihaoma || "无"
                  }}</span>
                </div>
              </div>
              <div class="info-row">
                <div class="info-item">
                  <span class="info-label">身份证：</span>
                  <span class="info-value">{{
                    userDetail.shenfenzheng || "无"
                  }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">邮箱：</span>
                  <span class="info-value">{{
                    userDetail.youxiang || "无"
                  }}</span>
                </div>
              </div>
            </div>
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

const tableName = "yonghu";
const formName = "用户";
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
  if (
    searchQuery.value.yonghuzhanghao &&
    searchQuery.value.yonghuzhanghao != ""
  ) {
    params["yonghuzhanghao"] = "%" + searchQuery.value.yonghuzhanghao + "%";
  }
  context
    .$http({
      url: `${tableName}/page`,
      method: "get",
      params: params,
    })
    .then((res) => {
      listLoading.value = false;
      if (res.data && res.data.data) {
        console.log("获取到的用户数据:", res.data.data.list);
        console.log("用户数据长度:", res.data.data.list.length);
        list.value = res.data.data.list;
        total.value = Number(res.data.data.total);
      } else {
        console.error("服务器返回数据结构异常:", res);
        list.value = [];
        total.value = 0;
      }
    })
    .catch((error) => {
      listLoading.value = false;
      console.error("请求失败:", error);
      list.value = [];
      total.value = 0;
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
        console.log("用户详情数据:", res.data.data);
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
init();
</script>
<style lang="scss" scoped>
.center_view {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-xl) var(--spacing-lg);
  box-sizing: border-box;
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

// 用户列表
.dataList {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  margin-bottom: var(--spacing-lg);
  width: 100%;
  box-sizing: border-box;
  justify-content: center;

  .item {
    position: relative;
    background-color: var(--card-bg);
    border-radius: var(--border-radius-lg);
    box-shadow: var(--shadow-sm);
    padding: 15px;
    transition: all var(--transition-normal);
    text-align: center;
    width: 100%;
    min-width: 250px;
    box-sizing: border-box;
    margin: 0 6px 20px;

    &:hover {
      box-shadow: var(--shadow-md);
      transform: translateY(-4px);
    }

    .el-checkbox {
      position: absolute;
      top: 10px;
      left: 10px;
      z-index: 10;
    }

    el-image {
      width: 120px;
      height: 120px;
      border-radius: var(--border-radius-md);
      margin: 0 auto 10px;

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .user_info {
      margin-top: 10px;
      cursor: pointer;

      .user_username {
        font-size: 14px;
        font-weight: 600;
        color: #333;
        text-align: center;
      }
    }
  }
}

// 用户详情弹窗样式
.user_detail {
  padding: 15px;
}

.user_detail_header {
  text-align: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e0e0e0;
}

.user_detail_header h1 {
  color: #333;
  font-size: 16px;
  margin: 0;
}

.user_detail_content {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.user_detail_header_info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.avatar_section {
  flex-shrink: 0;
}

.avatar_section .avatar-container {
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar_section .no-avatar {
  width: 80px;
  height: 80px;
  background: #f0f0f0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 14px;
}

.basic_info {
  width: 100%;
}

.info-row {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.info-label {
  font-size: 14px;
  font-weight: 600;
  color: #666;
  width: 70px;
  text-align: right;
}

.info-value {
  font-size: 14px;
  color: #333;
  flex: 1;
  word-break: break-word;
}

.avatar-container img {
  width: 80px !important;
  height: 80px !important;
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

  .dataList {
    grid-template-columns: 1fr;
  }
}
</style>
