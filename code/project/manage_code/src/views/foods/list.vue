<template>
  <div class="foods_view" :style="{}">
    <div class="back_view" v-if="centerType">
      <el-button class="back_btn" @click="backClick" type="primary"
        >返回</el-button
      >
    </div>

    <!-- 搜索框 -->
    <el-form :model="searchQuery" class="list_search">
      <div class="search_container">
        <div class="search_label">食材名称：</div>
        <div class="search_box">
          <el-input
            class="search_inp"
            v-model="searchQuery.name"
            placeholder="食材名称"
            size="small"
            clearable
          >
          </el-input>
        </div>
        <el-button class="search_btn" type="primary" @click="searchClick"
          >搜索</el-button
        >
      </div>
    </el-form>

    <!-- 主内容区域 -->
    <div class="main_content_area">
      <!-- 列表 -->
      <div class="all_posts_section">
        <el-table :data="list" style="width: 100%" stripe>
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column
            prop="code"
            label="食材编码"
            width="120"
          ></el-table-column>
          <el-table-column prop="name" label="食材名称"></el-table-column>
          <el-table-column
            prop="calory"
            label="热量(kcal/100g)"
            width="120"
          ></el-table-column>
          <el-table-column
            prop="weight"
            label="重量"
            width="80"
          ></el-table-column>
          <el-table-column
            prop="unit"
            label="单位"
            width="80"
          ></el-table-column>
          <el-table-column prop="thumbImageUrl" label="图片" width="100">
            <template #default="scope">
              <el-image
                :src="scope.row.thumbImageUrl"
                :preview-src-list="[scope.row.thumbImageUrl]"
                fit="cover"
                style="width: 50px; height: 50px"
              ></el-image>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination_container">
          <el-pagination
            background
            :layout="layouts.join(',')"
            :total="total"
            :page-size="listQuery.limit"
            v-model:current-page="listQuery.page"
            prev-text="上一页"
            next-text="下一页"
            :hide-on-single-page="false"
            :style="{}"
            @size-change="sizeChange"
            @current-change="currentChange"
          />
        </div>
      </div>
    </div>

    <el-dialog
      v-model="formVisible"
      :title="formTitle"
      width="80%"
      custom-class="edit_view"
      destroy-on-close
    >
      <foods-form
        ref="formRef"
        :form="form"
        :rules="rules"
        @ok="save"
      ></foods-form>
      <template #footer>
        <span class="formModel_btn_box">
          <el-button class="formModel_cancel" @click="formVisible = false"
            >关闭</el-button
          >
          <el-button
            class="formModel_confirm"
            type="primary"
            @click="handleSubmit"
          >
            {{ form.id ? "修改保存" : "新增保存" }}
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, getCurrentInstance } from "vue";
import { ElMessageBox } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import foodsForm from "./formModel.vue";

const route = useRoute();
const router = useRouter();
const context = getCurrentInstance()?.appContext.config.globalProperties;

// 权限验证
const btnAuth = (e, a) => {
  // 暂时返回true，确保按钮显示
  return true;
  // return context?.$toolUtil.isAuth(e, a);
};

// 列表数据
const list = ref([]);
const total = ref(0);
const listLoading = ref(false);

// 搜索参数
const searchQuery = ref({});
const listQuery = ref({
  page: 1,
  limit: 15,
  sort: "id",
  order: "desc",
});

// 分页
const layouts = ref(["total", "prev", "pager", "next", "sizes", "jumper"]);
const sizeChange = (size) => {
  listQuery.value.limit = size;
  getList();
};
const currentChange = (page) => {
  listQuery.value.page = page;
  getList();
};

// 搜索
const searchClick = () => {
  console.log("搜索按钮点击，搜索参数:", searchQuery.value);
  listQuery.value.page = 1;
  getList();
};

// 获取列表
const getList = () => {
  listLoading.value = true;
  console.log("获取食材列表，参数:", {
    ...listQuery.value,
    ...searchQuery.value,
  });
  context
    .$http({
      url: "foods/list",
      method: "get",
      params: {
        ...listQuery.value,
        ...searchQuery.value,
      },
    })
    .then((res) => {
      console.log("获取食材列表响应:", res);
      if (res && res.data && res.data.code === 0) {
        list.value = res.data.data.list || [];
        total.value = res.data.data.total || 0;
      } else {
        list.value = [];
        total.value = 0;
      }
      listLoading.value = false;
    })
    .catch((error) => {
      listLoading.value = false;
      console.error("获取食材列表失败:", error);
    });
};

// 批量选择
const multipleSelection = ref([]);
const handleSelectionChange = (val) => {
  multipleSelection.value = val;
};

// 新增
const formVisible = ref(false);
const formTitle = ref("");
const form = ref({});
const rules = ref({});
const formRef = ref(null);

const addClick = () => {
  formTitle.value = "新增食材";
  form.value = {};
  formVisible.value = true;
};

// 编辑
const editClick = (id) => {
  formTitle.value = "编辑食材";
  context
    .$http({
      url: `foods/info/${id}`,
      method: "get",
    })
    .then((res) => {
      if (res && res.data && res.data.code === 0) {
        form.value = res.data.data;
        formVisible.value = true;
      }
    })
    .catch((error) => {
      console.error("获取食材信息失败:", error);
    });
};

// 删除
const delClick = (id) => {
  ElMessageBox.confirm("确定要删除这个食材吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      context
        .$http({
          url: `foods/delete/${id}`,
          method: "delete",
        })
        .then((res) => {
          if (res && res.data && res.data.code === 0) {
            context.$toolUtil.message("删除成功", "success");
            getList();
          } else {
            context.$toolUtil.message("删除失败", "error");
          }
        })
        .catch((error) => {
          console.error("删除食材失败:", error);
          context.$toolUtil.message("删除失败", "error");
        });
    })
    .catch(() => {
      // 取消删除
    });
};

// 批量删除
const batchDelClick = () => {
  if (multipleSelection.value.length === 0) {
    context.$toolUtil.message("请选择要删除的食材", "warning");
    return;
  }

  const ids = multipleSelection.value.map((item) => item.id);
  ElMessageBox.confirm(`确定要删除这 ${ids.length} 个食材吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(() => {
      context
        .$http({
          url: "foods/batchDelete",
          method: "post",
          data: {
            ids,
          },
        })
        .then((res) => {
          if (res && res.data && res.data.code === 0) {
            context.$toolUtil.message("批量删除成功", "success");
            getList();
            multipleSelection.value = [];
          } else {
            context.$toolUtil.message("批量删除失败", "error");
          }
        })
        .catch((error) => {
          console.error("批量删除食材失败:", error);
          context.$toolUtil.message("批量删除失败", "error");
        });
    })
    .catch(() => {
      // 取消删除
    });
};

// 保存
const handleSubmit = () => {
  if (formRef.value) {
    formRef.value.validate((valid) => {
      if (valid) {
        save();
      }
    });
  }
};

const save = () => {
  const url = form.value.id ? "foods/update" : "foods/save";
  context
    .$http({
      url,
      method: "post",
      data: form.value,
    })
    .then((res) => {
      if (res && res.data && res.data.code === 0) {
        context.$toolUtil.message(
          form.value.id ? "修改成功" : "新增成功",
          "success",
        );
        formVisible.value = false;
        getList();
      } else {
        context.$toolUtil.message(
          form.value.id ? "修改失败" : "新增失败",
          "error",
        );
      }
    })
    .catch((error) => {
      console.error("保存食材失败:", error);
      context.$toolUtil.message("保存失败", "error");
    });
};

// 返回
const backClick = () => {
  router.go(-1);
};

// 时间格式化
const formatDate = (timestamp) => {
  if (!timestamp) return "";
  // 检查timestamp是否已经是日期字符串
  if (typeof timestamp === "string") {
    return timestamp;
  }
  // 检查timestamp是否是毫秒级时间戳
  let date;
  if (timestamp.toString().length === 10) {
    // 秒级时间戳
    date = new Date(timestamp * 1000);
  } else {
    // 毫秒级时间戳或其他
    date = new Date(timestamp);
  }
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// 初始化
onMounted(() => {
  getList();
});
</script>

<style scoped>
.foods_view {
  width: 1200px;
  margin: 20px auto;
  padding: 0px;
  background: #fff;
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

.list_search {
  margin: 30px auto;
  width: 100%;
  display: flex;
  justify-content: flex-start;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.search_container {
  display: flex;
  align-items: center;
  gap: 10px;
}

.search_label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.search_box {
  width: 300px;
}

.search_inp {
  width: 100%;
}

.search_btn_view {
  display: flex;
  gap: 10px;
}

.search_btn,
.add_btn,
.del_btn {
  padding: 8px 16px;
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.search_btn:hover,
.add_btn:hover,
.del_btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.main_content_area {
  flex: 1;
  min-height: 600px;
}

.all_posts_section {
  margin-top: 20px;
}

.el-table {
  border-radius: 4px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.el-table th {
  background-color: #f8f9fa;
  font-weight: 600;
  color: #495057;
  border-bottom: 1px solid #e9ecef;
}

.el-table tr:hover {
  background-color: #f8f9fa;
}

.el-table__row:nth-child(even) {
  background-color: #f8f9fa;
}

.pagination_container {
  margin-top: 20px;
  text-align: right;
}

.back_view {
  border-radius: 4px;
  padding: 10px 0px;
  margin: 10px auto;
  background: none;
  width: 100%;
  text-align: right;
}

.back_btn {
  border: 1px solid var(--theme);
  cursor: pointer;
  border-radius: 0px;
  padding: 0 24px;
  color: #fff;
  background: var(--theme);
  width: auto;
  font-size: 14px;
  height: 34px;
}

.edit_view {
  border-radius: 8px;
  overflow: hidden;
}

.edit_view .el-dialog__header {
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
  padding: 16px 24px;
}

.edit_view .el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.edit_view .el-dialog__body {
  padding: 24px;
  background-color: #fff;
}

.edit_view .el-dialog__footer {
  padding: 16px 24px;
  background-color: #f8f9fa;
  border-top: 1px solid #e9ecef;
}

.formModel_btn_box {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.formModel_cancel,
.formModel_confirm {
  padding: 8px 16px;
  font-size: 14px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.formModel_cancel:hover,
.formModel_confirm:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

@media screen and (max-width: 1200px) {
  .foods_view {
    width: 95%;
  }

  .list_search {
    flex-direction: column;
    align-items: flex-start;
  }

  .search_box {
    width: 100%;
  }

  .search_btn_view {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>
