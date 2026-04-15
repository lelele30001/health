<template>
  <div>
    <div class="login_view">
      <div class="outTitle_view">
        <div class="outTilte">美食推荐分享系统登录</div>
      </div>
      <el-form :model="loginForm" class="login_form">
        <div class="list_item" v-if="loginType == 1">
          <div class="list_label">账号：</div>
          <input
            class="list_inp"
            v-model="loginForm.username"
            name="username"
            placeholder="请输入账号"
          />
        </div>
        <div class="list_item" v-if="loginType == 1">
          <div class="list_label">密码：</div>
          <input
            class="list_inp"
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            @keydown.enter.native="handleLogin"
          />
        </div>
        <div class="list_type" v-if="userList.length > 1">
          <div class="list_label">用户类型：</div>
          <el-select v-model="loginForm.role" placeholder="请选择用户类型">
            <el-option
              v-for="(item, index) in userList"
              :label="item.roleName"
              :value="item.roleName"
            ></el-option>
          </el-select>
        </div>
        <div class="remember_view" v-if="loginType == 1">
          <el-checkbox
            v-model="rememberPassword"
            label="记住密码"
            size="large"
            :true-label="true"
            :false-label="false"
          />
        </div>
        <div class="btn_view">
          <el-button
            class="login"
            v-if="loginType == 1"
            type="success"
            @click="handleLogin"
            >登录</el-button
          >
          <el-button
            class="register"
            type="primary"
            @click="handleRegister('yonghu')"
            >注册用户</el-button
          >
        </div>
      </el-form>
    </div>
  </div>
</template>
<script setup>
import { ref, getCurrentInstance, nextTick, onMounted } from "vue";
import { useStore } from "vuex";
const store = useStore();
import menu from "@/utils/menu";
const userList = ref([]);
const menus = ref([]);
const loginForm = ref({
  role: "",
  username: "",
  password: "",
});
const tableName = ref("");
const loginType = ref(1);
//是否记住密码
const rememberPassword = ref(true);
const context = getCurrentInstance()?.appContext.config.globalProperties;
//注册
const handleRegister = (tableName) => {
  context?.$router.push(`/${tableName}Register`);
};
const handleLogin = () => {
  if (!loginForm.value.username) {
    context?.$toolUtil.message("请输入用户名", "error");
    return;
  }
  if (!loginForm.value.password) {
    context?.$toolUtil.message("请输入密码", "error");
    return;
  }
  if (userList.value.length > 1) {
    if (!loginForm.value.role) {
      context?.$toolUtil.message("请选择角色", "error");
      verifySlider.reset();
      return;
    }
    for (let i = 0; i < menus.value.length; i++) {
      if (menus.value[i].roleName == loginForm.value.role) {
        tableName.value = menus.value[i].pathName || menus.value[i].tableName;
      }
    }
  } else {
    tableName.value = userList.value[0].pathName || userList.value[0].tableName;
    loginForm.value.role = userList.value[0].roleName;
  }
  login();
};
const login = () => {
  context
    ?.$http({
      url: `${tableName.value}/login?username=${loginForm.value.username}&password=${loginForm.value.password}`,
      method: "post",
    })
    .then(
      (res) => {
        //是否保存当前账号密码至缓存
        if (rememberPassword.value) {
          let loginForm1 = JSON.parse(JSON.stringify(loginForm.value));
          delete loginForm1.code;
          context?.$toolUtil.storageSet(
            "frontLoginForm",
            JSON.stringify(loginForm1),
          );
        } else {
          context?.$toolUtil.storageRemove("frontLoginForm");
        }
        context?.$toolUtil.storageSet("frontToken", res.data.token);
        context?.$toolUtil.storageSet("frontRole", loginForm.value.role);
        context?.$toolUtil.storageSet("frontSessionTable", tableName.value);
        store.dispatch("user/getSession");
        let path = context?.$toolUtil.storageGet("toPath");
        if (path && path != "/login") {
          context?.$router.push(path);
          context?.$toolUtil.storageRemove("toPath");
          return;
        }
        context?.$router.push(`/index/${tableName.value}Center`);
      },
      (err) => {},
    );
};
//获取菜单
const getMenu = async () => {
  try {
    let arr = menu.list();
    if (!arr) {
      let res = await context?.$http.get("menu/list");
      if (
        res &&
        res.data &&
        res.data.data &&
        res.data.data.list &&
        res.data.data.list[0] &&
        res.data.data.list[0].menujson
      ) {
        context?.$toolUtil.storageSet("menus", res.data.data.list[0].menujson);
        arr = JSON.parse(res.data.data.list[0].menujson);
      }
    }
    if (arr && Array.isArray(arr)) {
      menus.value = arr;
      for (let i = 0; i < menus.value.length; i++) {
        if (menus.value[i] && menus.value[i].hasFrontLogin == "是") {
          userList.value.push(menus.value[i]);
        }
      }
    }
  } catch (error) {
    console.error("获取菜单失败:", error);
  }
};
//初始化
const init = async () => {
  await getMenu();
  //获取缓存是否有保存的账号密码
  let form = context?.$toolUtil.storageGet("frontLoginForm");
  if (form) {
    loginForm.value = JSON.parse(form);
  } else if (userList.value.length > 0) {
    loginForm.value.role = userList.value[0].roleName;
  }
};
onMounted(() => {
  init();
});
</script>

<style lang="scss" scoped>
.login_view {
  background: #fff3e0; /* 浅橙色背景 */
  // 标题盒子
  .outTitle_view {
    .outTilte {
    }
  }
  // 表单盒子
  .login_form {
  }
  // item盒子
  .list_item {
    // label
    .list_label {
    }
    // 输入框
    .list_inp {
    }
  }
  .list_type {
    .list_label {
    }
    // 下拉框样式
    :deep(.el-select) {
      //去掉默认样式
      .select-trigger {
        height: 100%;
        .el-input {
          height: 100%;
        }
      }
    }
  }
  // 记住密码样式
  .remember_view {
    padding: 0 0 0 130px;
    margin: 0 0 20px;
    width: 100%;

    // 未选中样式
    :deep(.el-checkbox) {
      margin: 0;
      display: flex;
      width: 20%;
      justify-content: center;
      align-items: center;
      // 复选框
      .el-checkbox__inner {
        border: 1px solid #dcdfe6;
        background: #fff;
      }
      // 提示文字
      .el-checkbox__label {
        color: #999;
      }
    }
    // 选中样式
    :deep(.is-checked) {
      //复选框
      .el-checkbox__inner {
        background-color: var(--theme);
        border-color: var(--theme);
      }
      // 提示文字
      .el-checkbox__label {
        color: var(--theme);
      }
    }
  }
  // 按钮盒子
  .btn_view {
    display: flex;
    flex-wrap: nowrap;
    align-items: center;
    justify-content: center;
    width: 100%;
    padding: 0;
    // 登录
    .login {
    }
    // 注册
    .register {
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
}

/* 登录页面容器 */
.login_view {
  min-height: 100vh;
  position: relative;
  background: #fff3e0; /* 浅橙色背景 */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px;
  font-family: "Arial", sans-serif;
  background-image: none !important; /* 确保背景图片被移除 */
}

/* 标题样式 */
.login_view .outTitle_view {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  text-align: center;
}

.login_view .outTitle_view .outTilte {
  color: #4cd964;
  font-size: 32px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.login_view .outTitle_view .outTilte::before {
  content: "🏋️‍♀️";
  font-size: 36px;
}

/* 登录表单 */
.login_view .login_form {
  width: 100%;
  max-width: 450px;
  background: var(--white);
  border-radius: var(--border-radius);
  box-shadow: var(--shadow);
  padding: 40px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  transition: transform 0.3s ease;
}

.login_view .login_form:hover {
  transform: translateY(-5px);
}

/* 表单项目 */
.login_view .login_form .list_item {
  display: flex;
  align-items: center;
  width: 100%;
  margin-bottom: 20px;
}

.login_view .login_form .list_item .list_label {
  width: 100px;
  text-align: right;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-right: 15px;
}

/* 输入框样式 */
.login_view .login_form .list_item .list_inp {
  height: 48px;
  line-height: 48px;
  border: 1px solid var(--medium-gray);
  border-radius: 8px;
  padding: 0 15px;
  width: calc(100% - 115px);
  font-size: 16px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.login_view .login_form .list_item .list_inp:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(255, 138, 61, 0.1);
}

/* 用户类型选择 */
.login_view .login_form .list_type {
  display: flex;
  align-items: center;
  width: 100%;
  margin-bottom: 20px;
}

.login_view .login_form .list_type .list_label {
  width: 100px;
  text-align: right;
  font-size: 16px;
  font-weight: 500;
  color: #333;
  margin-right: 15px;
}

.login_view .login_form .list_type .el-select {
  width: calc(100% - 115px);
}

.login_view .login_form .list_type .el-select .el-input__inner {
  height: 48px;
  border-radius: 8px;
  border: 1px solid var(--medium-gray);
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.login_view .login_form .list_type .el-select .el-input__inner:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(255, 138, 61, 0.1);
}

/* 记住密码 */
.login_view .login_form .remember_view {
  padding: 0 0 0 115px;
  margin: 0 0 20px;
  width: 100%;
}

.login_view .login_form .remember_view .el-checkbox {
  margin: 0;
  display: flex;
  align-items: center;
}

.login_view .login_form .remember_view .el-checkbox .el-checkbox__label {
  color: #666;
  font-size: 14px;
  font-weight: 500;
}

.login_view .login_form .remember_view .el-checkbox .el-checkbox__inner {
  border: 1px solid var(--medium-gray);
  background: var(--white);
  transition: all 0.3s ease;
}

.login_view
  .login_form
  .remember_view
  .el-checkbox.is-checked
  .el-checkbox__inner {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

/* 按钮容器 */
.login_view .login_form .btn_view {
  display: flex;
  flex-wrap: nowrap;
  align-items: center;
  justify-content: center;
  width: 100%;
  padding: 0;
  margin-top: 20px;
}

/* 登录按钮 */
.login_view .login_form .btn_view .login {
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
}

.login_view .login_form .btn_view .login:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(76, 217, 100, 0.4);
}

/* 注册按钮 */
.login_view .login_form .btn_view .register {
  margin: 0px 20px 12px 0px;
  padding: 0px 30px;
  height: 48px;
  font-size: 18px;
  font-weight: 600;
  color: #4cd964;
  border-radius: 24px;
  border: 2px solid #4cd964;
  background: var(--white);
  cursor: pointer;
  transition: all 0.3s ease;
}

.login_view .login_form .btn_view .register:hover {
  background: #4cd964;
  color: var(--white);
  transform: scale(1.05);
}

/* 忘记密码 */
.login_view .login_form .btn_view .forget {
  margin: 10px 0 0;
  padding: 0px;
  width: 100%;
  text-align: center;
  font-size: 14px;
  color: var(--dark-gray);
  background: none;
  border: none;
  cursor: pointer;
  transition: all 0.3s ease;
}

.login_view .login_form .btn_view .forget:hover {
  color: var(--primary-color);
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login_view .login_form {
    padding: 30px;
  }

  .login_view .login_form .list_item .list_label {
    width: 80px;
  }

  .login_view .login_form .list_item .list_inp {
    width: calc(100% - 95px);
  }

  .login_view .login_form .list_type .list_label {
    width: 80px;
  }

  .login_view .login_form .list_type .el-select {
    width: calc(100% - 95px);
  }

  .login_view .login_form .remember_view {
    padding: 0 0 0 95px;
  }

  .login_view .login_form .btn_view {
    padding: 0 0 0 95px;
  }
}
</style>
