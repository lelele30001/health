<template>
  <div>
    <el-dialog
      modal-class="edit_form_modal"
      class="edit_form"
      v-model="formVisible"
      :title="formTitle"
      width="40%"
      destroy-on-close
      :fullscreen="false"
      height="auto"
      custom-class="small-dialog"
    >
      <el-form
        class="formModel_form"
        ref="formRef"
        :model="form"
        :rules="rules"
      >
        <el-row>
          <el-col :span="12">
            <el-form-item label="用户账号" prop="yonghuzhanghao">
              <el-input
                class="list_inp"
                v-model="form.yonghuzhanghao"
                placeholder="用户账号"
                type="text"
                autocomplete="off"
                :readonly="!isAdd || disabledForm.yonghuzhanghao ? true : false"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户密码" prop="yonghumima">
              <el-input
                class="list_inp"
                v-model="form.yonghumima"
                placeholder="用户密码"
                type="password"
                autocomplete="off"
                :readonly="!isAdd || disabledForm.yonghumima ? true : false"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户姓名" prop="yonghuxingming">
              <el-input
                class="list_inp"
                v-model="form.yonghuxingming"
                placeholder="用户姓名"
                type="text"
                :readonly="!isAdd || disabledForm.yonghuxingming ? true : false"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="性别" prop="xingbie">
              <el-select
                class="list_sel"
                :disabled="!isAdd || disabledForm.xingbie ? true : false"
                v-model="form.xingbie"
                placeholder="请选择性别"
                style="width: 100%"
              >
                <el-option
                  v-for="(item, index) in xingbieLists"
                  :label="item"
                  :value="item"
                >
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号码" prop="shoujihaoma">
              <el-input
                class="list_inp"
                v-model="form.shoujihaoma"
                placeholder="手机号码"
                type="text"
                :readonly="!isAdd || disabledForm.shoujihaoma ? true : false"
              />
            </el-form-item>
          </el-col>

          <el-col :span="12">
            <el-form-item label="邮箱" prop="youxiang">
              <el-input
                class="list_inp"
                v-model="form.youxiang"
                placeholder="邮箱"
                type="text"
                :readonly="!isAdd || disabledForm.youxiang ? true : false"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer v-if="isAdd || type == 'logistics' || type == 'reply'">
        <span class="formModel_btn_box">
          <el-button class="cancel_btn" @click="closeClick">取消</el-button>
          <el-button class="confirm_btn" type="primary" @click="save">
            提交
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import {
  reactive,
  ref,
  getCurrentInstance,
  nextTick,
  computed,
  defineEmits,
} from "vue";
import { useStore } from "vuex";
const store = useStore();
const user = computed(() => store.getters["user/session"]);
const context = getCurrentInstance()?.appContext.config.globalProperties;
const emit = defineEmits(["formModelChange"]);
//基础信息
const tableName = "yonghu";
const formName = "用户";
//基础信息
//form表单
const form = ref({});
const disabledForm = ref({
  yonghuzhanghao: false,
  yonghumima: false,
  yonghuxingming: false,
  touxiang: false,
  xingbie: false,
  shoujihaoma: false,
  youxiang: false,
});
const formVisible = ref(false);
const isAdd = ref(false);
const formTitle = ref("");

const rules = ref({
  yonghuzhanghao: [{ required: true, message: "请输入", trigger: "blur" }],
  yonghumima: [{ required: true, message: "请输入", trigger: "blur" }],
  yonghuxingming: [{ required: true, message: "请输入", trigger: "blur" }],
  touxiang: [],
  xingbie: [],
  shoujihaoma: [
    { validator: context.$toolUtil.validator.mobile, trigger: "blur" },
  ],
  youxiang: [{ validator: context.$toolUtil.validator.email, trigger: "blur" }],
});
//表单验证

const formRef = ref(null);
const id = ref(0);
const type = ref("");
//头像上传回调
const touxiangUploadSuccess = (e) => {
  form.value.touxiang = e;
};
//性别列表
const xingbieLists = ref([]);

//获取唯一标识
const getUUID = () => {
  return new Date().getTime();
};
//重置
const resetForm = () => {
  form.value = {
    yonghuzhanghao: "",
    yonghumima: "",
    yonghuxingming: "",
    touxiang: "",
    xingbie: "",
    shoujihaoma: "",
    youxiang: "",
  };
  if (formRef.value) {
    formRef.value.resetFields();
  }
};
//获取info
const getInfo = () => {
  context
    ?.$http({
      url: `${tableName}/info/${id.value}`,
      method: "get",
    })
    .then((res) => {
      let reg = new RegExp("../../../file", "g");
      form.value = res.data.data;
      formVisible.value = true;
    });
};
const crossRow = ref("");
const crossTable = ref("");
const crossTips = ref("");
const crossColumnName = ref("");
const crossColumnValue = ref("");
//初始化
const init = (
  formId = null,
  formType = "add",
  formNames = "",
  row = null,
  table = null,
  statusColumnName = null,
  tips = null,
  statusColumnValue = null,
) => {
  // 重置表单
  form.value = {
    yonghuzhanghao: "",
    yonghumima: "",
    yonghuxingming: "",
    touxiang: "",
    xingbie: "",
    shoujihaoma: "",
    youxiang: "",
  };

  // 重置表单验证状态
  if (formRef.value) {
    formRef.value.resetFields();
  }

  if (formId) {
    id.value = formId;
    type.value = formType;
  }
  if (formType == "add") {
    isAdd.value = true;
    formTitle.value = "新增" + formName;
    formVisible.value = true;

    // 强制清空用户账号和密码字段
    nextTick(() => {
      form.value.yonghuzhanghao = "";
      form.value.yonghumima = "";
      if (formRef.value) {
        formRef.value.resetFields();
      }
    });
  } else if (formType == "info") {
    isAdd.value = false;
    formTitle.value = "查看" + formName;
    getInfo();
  } else if (formType == "edit") {
    isAdd.value = true;
    formTitle.value = "修改" + formName;
    getInfo();
  } else if (formType == "cross") {
    isAdd.value = true;
    formTitle.value = formNames;
    // getInfo()
    for (let x in row) {
      if (x == "yonghuzhanghao") {
        form.value.yonghuzhanghao = row[x];
        disabledForm.value.yonghuzhanghao = true;
        continue;
      }
      if (x == "yonghumima") {
        form.value.yonghumima = row[x];
        disabledForm.value.yonghumima = true;
        continue;
      }
      if (x == "yonghuxingming") {
        form.value.yonghuxingming = row[x];
        disabledForm.value.yonghuxingming = true;
        continue;
      }
      if (x == "touxiang") {
        form.value.touxiang = row[x];
        disabledForm.value.touxiang = true;
        continue;
      }
      if (x == "xingbie") {
        form.value.xingbie = row[x];
        disabledForm.value.xingbie = true;
        continue;
      }
      if (x == "shoujihaoma") {
        form.value.shoujihaoma = row[x];
        disabledForm.value.shoujihaoma = true;
        continue;
      }
      if (x == "youxiang") {
        form.value.youxiang = row[x];
        disabledForm.value.youxiang = true;
        continue;
      }
    }
    if (row) {
      crossRow.value = row;
    }
    if (table) {
      crossTable.value = table;
    }
    if (tips) {
      crossTips.value = tips;
    }
    if (statusColumnName) {
      crossColumnName.value = statusColumnName;
    }
    if (statusColumnValue) {
      crossColumnValue.value = statusColumnValue;
    }
    formVisible.value = true;
  }

  context
    ?.$http({
      url: `${context?.$toolUtil.storageGet("sessionTable")}/session`,
      method: "get",
    })
    .then((res) => {
      var json = res.data.data;
    });
  xingbieLists.value = "男,女".split(",");
};
//初始化
//声明父级调用
defineExpose({
  init,
});
//关闭
const closeClick = () => {
  formVisible.value = false;
};
//富文本
const editorChange = (e, name) => {
  form.value[name] = e;
};
//提交
const save = async () => {
  // 自动生成DiceBear头像URL（与用户端注册逻辑一致）
  if (!form.value.touxiang) {
    form.value.touxiang = `https://api.dicebear.com/7.x/avataaars/svg?seed=${form.value.yonghuzhanghao}`;
  } else if (form.value.touxiang != null) {
    // 保留现有的图片路径处理逻辑
    form.value.touxiang = form.value.touxiang.replace(
      new RegExp(context?.$config.url, "g"),
      "",
    );
  }

  // 添加时间日期字段
  if (!form.value.id) {
    form.value.addtime = new Date()
      .toISOString()
      .slice(0, 19)
      .replace("T", " ");
  }
  var table = crossTable.value;
  var objcross = JSON.parse(JSON.stringify(crossRow.value));
  let crossUserId = "";
  let crossRefId = "";
  let crossOptNum = "";
  if (type.value == "cross") {
    if (crossColumnName.value != "") {
      if (!crossColumnName.value.startsWith("[")) {
        for (let o in objcross) {
          if (o == crossColumnName.value) {
            objcross[o] = crossColumnValue.value;
          }
        }
        //修改跨表数据
        changeCrossData(objcross);
      } else {
        crossUserId = user.value.id;
        crossRefId = objcross["id"];
        crossOptNum = crossColumnName.value.replace(/\[/, "").replace(/\]/, "");
      }
    }
  }
  formRef.value.validate((valid) => {
    if (valid) {
      if (crossUserId && crossRefId) {
        form.value.crossuserid = crossUserId;
        form.value.crossrefid = crossRefId;
        let params = {
          page: 1,
          limit: 1000,
          crossuserid: form.value.crossuserid,
          crossrefid: form.value.crossrefid,
        };
        context
          ?.$http({
            url: `${tableName}/page`,
            method: "get",
            params: params,
          })
          .then((res) => {
            if (res.data.data.total >= crossOptNum) {
              context?.$toolUtil.message(`${crossTips.value}`, "error");
              return false;
            } else {
              context
                ?.$http({
                  url: `${tableName}/${!form.value.id ? "save" : "update"}`,
                  method: "post",
                  data: form.value,
                })
                .then(async (res) => {
                  emit("formModelChange");
                  context?.$toolUtil.message(`操作成功`, "success");
                  formVisible.value = false;
                  resetForm();
                });
            }
          });
      } else {
        context
          ?.$http({
            url: `${tableName}/${!form.value.id ? "save" : "update"}`,
            method: "post",
            data: form.value,
          })
          .then(async (res) => {
            emit("formModelChange");
            context?.$toolUtil.message(`操作成功`, "success");
            formVisible.value = false;
            resetForm();
          });
      }
    } else {
      context.$message.error("请完善信息");
    }
  });
};
//修改跨表数据
const changeCrossData = async (row) => {
  if (type.value == "cross") {
    await context
      ?.$http({
        url: `${crossTable.value}/update`,
        method: "post",
        data: row,
      })
      .then((res) => {});
  }
};
</script>
<style lang="scss" scoped>
// 强制缩小弹窗高度
:deep(.small-dialog) {
  max-height: 30vh !important;
  height: auto !important;
  overflow-y: auto;

  .el-dialog__body {
    padding: 5px 10px !important; /* 进一步减少内边距 */
    max-height: 20vh !important; /* 进一步限制内容高度 */
    overflow-y: auto;
    display: flex;
    flex-direction: column;
  }

  .formModel_form {
    max-height: 15vh !important;
    overflow-y: auto;
    flex: 1;
  }

  .formModel_btn_box {
    margin-top: 10px !important;
    margin-bottom: 10px !important;
  }
}

// 表单
.formModel_form {
  // form item
  :deep(.el-form-item) {
    margin-bottom: 10px; // 减少表单元素之间的间距
    //label
    .el-form-item__label {
    }
    // 内容盒子
    .el-form-item__content {
      // 输入框
      .list_inp {
      }
      // 下拉框
      .list_sel {
        //去掉默认样式
        .select-trigger {
          height: 100%;
          .el-input {
            height: 100%;
          }
        }
      }
      //图片上传样式
      .el-upload-list {
        //提示语
        .el-upload__tip {
        }
        //外部盒子
        .el-upload--picture-card {
          //图标
          .el-icon {
          }
        }
        .el-upload-list__item {
        }
      }
    }
  }
}
// 按钮盒子
.formModel_btn_box {
  .cancel_btn {
  }
  .cancel_btn:hover {
  }

  .confirm_btn {
  }
  .confirm_btn:hover {
  }
}
</style>
