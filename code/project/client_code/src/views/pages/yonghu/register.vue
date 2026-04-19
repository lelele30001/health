<template>
  <div>
    <div class="register_view">
      <div class="outTitle_view">
        <div class="outTilte">{{ projectName }}注册</div>
      </div>
      <el-form :model="registerForm" class="register_form">
        <div class="list_item">
          <div class="list_label">
            <span class="required">*</span>用户账号：
          </div>
          <input
            class="list_inp"
            v-model="registerForm.yonghuzhanghao"
            placeholder="请输入用户账号"
            type="text"
          />
        </div>
        <div class="list_item">
          <div class="list_label">
            <span class="required">*</span>用户密码：
          </div>
          <input
            class="list_inp"
            v-model="registerForm.yonghumima"
            placeholder="请输入用户密码"
            type="password"
          />
        </div>
        <div class="list_item">
          <div class="list_label">
            <span class="required">*</span>确认用户密码：
          </div>
          <input
            class="list_inp"
            v-model="registerForm.yonghumima2"
            type="password"
            placeholder="请输入确认用户密码"
          />
        </div>
        <div class="list_item">
          <div class="list_label">
            <span class="required">*</span>用户姓名：
          </div>
          <input
            class="list_inp"
            v-model="registerForm.yonghuxingming"
            placeholder="请输入用户姓名"
            type="text"
          />
        </div>
        <div class="list_item">
          <div class="list_label"><span class="required">*</span>性别：</div>
          <el-select
            class="list_sel"
            v-model="registerForm.xingbie"
            placeholder="请选择性别"
          >
            <el-option
              v-for="item in yonghuxingbieLists"
              :label="item"
              :value="item"
            ></el-option>
          </el-select>
        </div>
        <div class="list_item">
          <div class="list_label">
            <span class="required">*</span>手机号码：
          </div>
          <input
            class="list_inp"
            v-model="registerForm.shoujihaoma"
            placeholder="请输入手机号码"
            type="text"
          />
        </div>

        <div class="list_item">
          <div class="list_label">
            <span class="required">*</span>身高(cm)：
          </div>
          <input
            class="list_inp"
            v-model="registerForm.height"
            placeholder="请输入身高"
            type="number"
          />
        </div>
        <div class="list_item">
          <div class="list_label">
            <span class="required">*</span>体重(kg)：
          </div>
          <input
            class="list_inp"
            v-model="registerForm.weight"
            placeholder="请输入体重"
            type="number"
          />
        </div>
        <div class="list_item">
          <div class="list_label">
            <span class="required">*</span>健身目标：
          </div>
          <el-select
            class="list_sel"
            v-model="registerForm.fitnessGoal"
            placeholder="请选择健身目标"
          >
            <el-option
              v-for="item in jianshenmubiaoLists"
              :label="item"
              :value="item"
            ></el-option>
          </el-select>
        </div>
        <div class="list_btn">
          <el-button class="register" type="success" @click="handleRegister"
            >注册</el-button
          >
          <div class="r-login" @click="close">已有账号，直接登录</div>
        </div>
      </el-form>
    </div>
  </div>
</template>
<script setup>
import { ref, getCurrentInstance, nextTick, onMounted } from "vue";
const context = getCurrentInstance()?.appContext.config.globalProperties;
const projectName = context?.$project.projectName;
//获取注册类型
import { useRoute } from "vue-router";
const route = useRoute();
const tableName = ref("yonghu");

//公共方法
const getUUID = () => {
  return new Date().getTime();
};

const registerForm = ref({
  xingbie: "",
});
const yonghuxingbieLists = ref([]);
const jianshenmubiaoLists = ref([]);
const init = () => {
  yonghuxingbieLists.value = "男,女".split(",");
  jianshenmubiaoLists.value = "增肌,减脂,维持".split(",");
};
// 多级联动参数
//注册按钮
const handleRegister = () => {
  let url = tableName.value + "/register";
  if (!registerForm.value.yonghuzhanghao) {
    context?.$toolUtil.message(`用户账号不能为空`, "error");
    return false;
  }
  if (!registerForm.value.yonghumima) {
    context?.$toolUtil.message(`用户密码不能为空`, "error");
    return false;
  }
  if (registerForm.value.yonghumima != registerForm.value.yonghumima2) {
    context?.$toolUtil.message("两次密码输入不一致", "error");
    return false;
  }
  if (!registerForm.value.yonghuxingming) {
    context?.$toolUtil.message(`用户姓名不能为空`, "error");
    return false;
  }
  if (!registerForm.value.xingbie) {
    context?.$toolUtil.message(`性别不能为空`, "error");
    return false;
  }
  // 自动生成DiceBear头像URL
  registerForm.value.touxiang = `https://api.dicebear.com/7.x/avataaars/svg?seed=${registerForm.value.yonghuzhanghao}`;
  if (
    registerForm.value.shoujihaoma &&
    !context?.$toolUtil.isMobile(registerForm.value.shoujihaoma)
  ) {
    context?.$toolUtil.message(`手机号码应输入手机格式`, "error");
    return false;
  }

  if (!registerForm.value.height) {
    context?.$toolUtil.message(`身高不能为空`, "error");
    return false;
  }
  if (!registerForm.value.weight) {
    context?.$toolUtil.message(`体重不能为空`, "error");
    return false;
  }
  if (!registerForm.value.fitnessGoal) {
    context?.$toolUtil.message(`健身目标不能为空`, "error");
    return false;
  }
  context
    ?.$http({
      url: url,
      method: "post",
      data: registerForm.value,
    })
    .then((res) => {
      context?.$toolUtil.message("注册成功", "success", (obj) => {
        context?.$router.push({
          path: "/login",
        });
      });
    });
};
//返回登录
const close = () => {
  context?.$router.push({
    path: "/login",
  });
};
init();
onMounted(() => {});
</script>
<style lang="scss" scoped>
.register_view {
  background: #fff3e0; /* 浅橙色背景 */
  // 标题盒子
  .outTitle_view {
    .outTilte {
    }
  }
  // 表单盒子
  .register_form {
  }
  // item盒子
  .list_item {
    // label
    .list_label {
    }
    // 输入框
    :deep(.list_inp) {
    }
  }
  //下拉框样式
  :deep(.list_sel) {
    //去掉默认样式
    .select-trigger {
      height: 100%;
      .el-input {
        height: 100%;
      }
    }
  }
  //图片上传样式
  .list_file_list {
    //提示语
    :deep(.el-upload__tip) {
    }
    //外部盒子
    :deep(.el-upload--picture-card) {
      //图标
      .el-icon {
      }
    }
    :deep(.el-upload-list__item) {
    }
  }
  //按钮盒子
  .list_btn {
    //注册按钮
    .register {
    }
    //注册按钮悬浮样式
    .register:hover {
    }
    //已有账号
    .r-login {
    }
  }
}
</style>
<style>
/* 全局样式 */
:root {
  --primary-color: #ff8a3d; /* 活力橙 */
  --secondary-color: #4cd964; /* 健身绿 */
  --accent-color: #4da3ff; /* 天空蓝 */
  --light-gray: #f5f5f5;
  --medium-gray: #e0e0e0;
  --dark-gray: #999999;
  --white: #ffffff;
  --shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  --border-radius: 12px;
  --required-color: #ff4d4f; /* 红色，用于必填项星号 */
}

/* 必填项星号样式 */
.required {
  color: var(--required-color);
  margin-right: 4px;
  font-size: 16px;
  font-weight: bold;
}

/* 注册页面容器 */
.register_view {
  min-height: 100vh;
  position: relative;
  background: #f5f5f5;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  font-family: "Arial", sans-serif;
}

/* 标题样式 */
.register_view .outTitle_view {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  text-align: center;
}

.register_view .outTitle_view .outTilte {
  color: #4cd964;
  font-size: 32px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.register_view .outTitle_view .outTilte::before {
  content: "🏋️‍♀️";
  font-size: 36px;
}

/* 注册表单 */
.register_view .register_form {
  width: 100%;
  max-width: 550px;
  background: var(--white);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow);
  padding: 40px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  transition: transform 0.3s ease;
  max-height: 80vh;
}

.register_view .register_form:hover {
  transform: translateY(-5px);
}

/* 表单项目 */
.register_view .register_form .list_item {
  display: flex;
  align-items: center;
  width: 100%;
  margin-bottom: 20px;
}

.register_view .register_form .list_item .list_label {
  width: 120px;
  text-align: right;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-right: 15px;
}

/* 输入框样式 */
.register_view .register_form .list_item .list_inp {
  height: 48px;
  line-height: 48px;
  border: 1px solid var(--medium-gray);
  border-radius: 8px;
  padding: 0 15px;
  width: calc(100% - 135px);
  font-size: 16px;
  transition: all 0.3s ease;
  font-weight: 500;
  color: #333;
}

.register_view .register_form .list_item .list_inp:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(255, 138, 61, 0.1);
}

/* 日期选择器 */
.register_view .register_form .list_item .list_date {
  width: calc(100% - 135px) !important;
  height: 48px;
  border-radius: 8px;
  border: 1px solid var(--medium-gray);
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  color: #333;
}

.register_view .register_form .list_item .list_date:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(255, 138, 61, 0.1);
}

/* 下拉选择框 */
.register_view .register_form .list_item .list_sel {
  width: calc(100% - 135px);
}

.register_view .register_form .list_item .list_sel .el-input__inner {
  height: 48px;
  border-radius: 8px;
  border: 1px solid var(--medium-gray);
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  color: #333;
}

.register_view .register_form .list_item .list_sel .el-input__inner:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(255, 138, 61, 0.1);
}

/* 文件上传 */
.register_view .register_form .list_item .list_file_list {
  width: calc(100% - 135px);
}

.register_view .register_form .list_item .el-upload--picture-card {
  background-color: var(--light-gray);
  width: 90px;
  height: 60px;
  line-height: 60px;
  text-align: center;
  border: 2px dashed var(--medium-gray);
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.register_view .register_form .list_item .el-upload--picture-card:hover {
  border-color: var(--primary-color);
  background-color: rgba(255, 138, 61, 0.1);
}

.register_view .register_form .list_item .el-upload--picture-card .el-icon {
  font-size: 24px;
  color: var(--primary-color);
}

/* 单选框 */
.register_view .register_form .list_item .list_radio {
  display: flex;
  width: calc(100% - 135px);
  align-items: center;
  flex-wrap: wrap;
}

.register_view .register_form .list_item .list_radio .el-radio {
  width: auto;
  margin: 0px 20px 0px 0px;
  display: flex;
  align-items: center;
  font-weight: 500;
}

.register_view
  .register_form
  .list_item
  .list_radio
  .el-radio
  .el-radio__inner {
  border-color: var(--medium-gray);
  background: var(--white);
  transition: all 0.3s ease;
}

.register_view
  .register_form
  .list_item
  .list_radio
  .el-radio.is-checked
  .el-radio__inner {
  border-color: var(--primary-color);
  background: var(--primary-color);
}

.register_view
  .register_form
  .list_item
  .list_radio
  .el-radio
  .el-radio__label {
  color: #333;
  font-size: 16px;
  font-weight: 500;
}

.register_view
  .register_form
  .list_item
  .list_radio
  .el-radio.is-checked
  .el-radio__label {
  color: var(--primary-color);
}

/* 复选框 */
.register_view .register_form .list_item .list_checkbox {
  display: flex;
  width: calc(100% - 135px);
  flex-wrap: wrap;
  align-items: center;
}

.register_view .register_form .list_item .list_checkbox .el-checkbox {
  width: auto;
  margin: 0px 15px 0px 0px;
  display: flex;
  justify-content: center;
  align-items: center;
  font-weight: 500;
}

.register_view
  .register_form
  .list_item
  .list_checkbox
  .el-checkbox
  .el-checkbox__inner {
  border-color: var(--medium-gray);
  background: var(--white);
  transition: all 0.3s ease;
}

.register_view
  .register_form
  .list_item
  .list_checkbox
  .el-checkbox.is-checked
  .el-checkbox__inner {
  border-color: var(--primary-color);
  background: var(--primary-color);
}

.register_view
  .register_form
  .list_item
  .list_checkbox
  .el-checkbox
  .el-checkbox__label {
  color: #333;
  font-size: 16px;
  font-weight: 500;
}

.register_view
  .register_form
  .list_item
  .list_checkbox
  .el-checkbox.is-checked
  .el-checkbox__label {
  color: var(--primary-color);
}

/* 验证码 */
.register_view .register_form .list_code {
  display: flex;
  align-items: center;
  width: 100%;
  margin-bottom: 20px;
}

.register_view .register_form .list_code .list_code_label {
  width: 120px;
  text-align: right;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-right: 15px;
}

.register_view .register_form .list_code .list_code_item {
  width: calc(100% - 135px);
  display: flex;
  align-items: center;
  gap: 10px;
}

.register_view .register_form .list_code .list_code_item .list_code_inp {
  height: 48px;
  line-height: 48px;
  border: 1px solid var(--medium-gray);
  border-radius: 8px;
  padding: 0 15px;
  flex: 1;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  color: #333;
}

.register_view .register_form .list_code .list_code_item .list_code_inp:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(255, 138, 61, 0.1);
}

.register_view .register_form .list_code .list_code_btn {
  padding: 0px 20px;
  height: 48px;
  line-height: 48px;
  text-align: center;
  border-radius: 8px;
  border: none;
  background: linear-gradient(
    135deg,
    var(--primary-color),
    var(--secondary-color)
  );
  color: var(--white);
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 138, 61, 0.3);
}

.register_view .register_form .list_code .list_code_btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(255, 138, 61, 0.4);
}

/* 按钮容器 */
.register_view .register_form .list_btn {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  justify-content: center;
  width: 100%;
  margin: 20px 0px 0px;
  padding: 0;
}

/* 注册按钮 */
.register_view .register_form .list_btn .register {
  margin: 0px 20px 12px 0px;
  padding: 0px 30px;
  height: 48px;
  font-size: 18px;
  font-weight: 600;
  color: var(--white);
  border-radius: 24px;
  border: none;
  background: #4cd964;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(76, 217, 100, 0.3);
  min-width: 120px;
}

.register_view .register_form .list_btn .register:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(76, 217, 100, 0.4);
}

/* 已有账号 */
.register_view .register_form .list_btn .r-login {
  width: 100%;
  text-align: center;
  cursor: pointer;
  padding: 10px 0px 0px;
  color: var(--dark-gray);
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.register_view .register_form .list_btn .r-login:hover {
  color: var(--primary-color);
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .register_view .register_form {
    padding: 30px;
  }

  .register_view .register_form .list_item .list_label {
    width: 100px;
  }

  .register_view .register_form .list_item .list_inp {
    width: calc(100% - 115px);
  }

  .register_view .register_form .list_item .list_date {
    width: calc(100% - 115px) !important;
  }

  .register_view .register_form .list_item .list_sel {
    width: calc(100% - 115px);
  }

  .register_view .register_form .list_item .list_file_list {
    width: calc(100% - 115px);
  }

  .register_view .register_form .list_item .list_radio {
    width: calc(100% - 115px);
  }

  .register_view .register_form .list_item .list_checkbox {
    width: calc(100% - 115px);
  }

  .register_view .register_form .list_code .list_code_label {
    width: 100px;
  }

  .register_view .register_form .list_code .list_code_item {
    width: calc(100% - 115px);
  }

  .register_view .register_form .list_btn {
    padding: 0 0 0 115px;
  }
}
</style>
