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
        </div>
      </el-form>
    </div>
  </div>
</template>
<script setup>
import { ref, getCurrentInstance, nextTick, onMounted } from "vue";
import { useStore } from "vuex";
const store = useStore();
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
      verifySlider.value.reset();
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
            "loginForm",
            JSON.stringify(loginForm1),
          );
        } else {
          context?.$toolUtil.storageRemove("loginForm");
        }
        context?.$toolUtil.storageSet("Token", res.data.token);
        context?.$toolUtil.storageSet("role", loginForm.value.role);
        context?.$toolUtil.storageSet("sessionTable", tableName.value);
        context?.$toolUtil.storageSet("adminName", loginForm.value.username);
        store.dispatch("user/getSession"); //vuex中获取用户信息并保存
        context?.$router.push("/");
      },
      (err) => {},
    );
};
//获取菜单
const getMenu = () => {
  let params = {
    page: 1,
    limit: 1,
    sort: "id",
  };

  context
    ?.$http({
      url: "menu/list",
      method: "get",
      params: params,
    })
    .then((res) => {
      if (
        res &&
        res.data &&
        res.data.data &&
        res.data.data.list &&
        res.data.data.list[0] &&
        res.data.data.list[0].menujson
      ) {
        menus.value = JSON.parse(res.data.data.list[0].menujson);
        for (let i = 0; i < menus.value.length; i++) {
          if (menus.value[i] && menus.value[i].hasBackLogin == "是") {
            userList.value.push(menus.value[i]);
          }
        }
        //获取缓存是否有保存的账号密码
        let form = context?.$toolUtil.storageGet("loginForm");
        if (form) {
        } else if (userList.value.length > 0) {
          loginForm.value.role = userList.value[0].roleName;
        }
        context?.$toolUtil.storageSet("menus", JSON.stringify(menus.value));
      }
    })
    .catch((error) => {
      console.error("获取菜单失败:", error);
    });
};
//初始化
const init = () => {
  getMenu();
  //获取缓存是否有保存的账号密码
  let form = context?.$toolUtil.storageGet("loginForm");
  if (form) {
    loginForm.value = JSON.parse(form);
  }
};
onMounted(() => {
  init();
});
</script>

<style lang="scss" scoped>
.login_view {
  background-image: url("http://codegen.caihongy.cn/20250110/77e5dc5f8b2a41c3befbae43f31623cf.jpg");
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
    // 登录
    .login {
    }
  }
}
</style>
<style>
.login_view {
  min-height: 100vh;
  position: relative;
  background: url(http://codegen.caihongy.cn/20250110/77e5dc5f8b2a41c3befbae43f31623cf.jpg)
    no-repeat center center / cover;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.login_view .login_form {
  width: 600px;
  margin: 40px 10% 0 0;
  box-shadow: rgb(187, 187, 187) 0px 4px 9px;
  padding: 30px 60px 30px 20px;
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  border-radius: 10px;
  background: #fff;
  border: 1px solid #ddd;
}

.login_view .outTitle_view {
  display: flex;
  align-items: center;
  padding: 0px 0px 20px;
  margin: 0 10% 0px 0;
}
.login_view .outTitle_view .outTilte {
  color: rgb(51, 51, 51);
  font-size: 30px;
  font-weight: 600;
  -webkit-box-reflect: below 2px
    linear-gradient(transparent, rgba(0, 0, 0, 0.1));
  background: linear-gradient(
    90deg,
    var(--theme) 0%,
    var(--theme) 50%,
    var(--theme) 100%
  );
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.login_view .login_form .list_item {
  display: flex;
  align-items: center;
  width: 100%;
  justify-content: center;
  margin: 0px 10px 20px 0px;
}
.login_view .login_form .list_item .list_label {
  width: 120px;
  text-align: right;
  font-size: 16px;
}
.login_view .login_form .list_item .list_inp {
  height: 36px;
  line-height: 36px;
  border: none;
  border-bottom: 1px solid rgb(221, 221, 221);
  padding: 0px 10px;
  width: calc(100% - 120px);
  font-size: 16px;
}

.login_view .login_form .list_type {
  display: flex;
  align-items: center;
  width: 100%;
  justify-content: center;
  margin: 0px 10px 20px 0px;
}
.login_view .login_form .list_type .list_label {
  width: 120px;
  text-align: right;
  font-size: 16px;
  color: #333;
}
.login_view .login_form .list_type .el-select {
  line-height: 36px;
  border: none;
  border-bottom: 1px solid rgb(221, 221, 221);
  box-sizing: border-box;
  width: calc(100% - 120px);
  padding: 0px 10px;
  border-radius: 0px;
  font-size: 16px;
  color: rgb(102, 102, 102);
}
.login_view .login_form .list_type .el-select .el-input__inner {
  font-size: 16px;
}

.login_view .login_form .listCode_view {
  display: flex;
  align-items: center;
  width: 100%;
  justify-content: center;
  margin: 0px 10px 10px 0px;
}
.login_view .login_form .listCode_view .listCode_label {
  width: 120px;
  text-align: right;
  font-size: 16px;
}
.login_view .login_form .listCode_view .listCode_inp {
  height: 36px;
  line-height: 36px;
  border: none;
  border-bottom: 1px solid rgb(221, 221, 221);
  padding: 0px 10px;
  width: calc(100% - 220px);
  font-size: 16px;
}
.login_view .login_form .listCode_view .listCode_btn {
  width: 100px;
  border-style: solid;
  border-color: rgb(221, 221, 221);
  border-image: initial;
  height: 36px;
  line-height: 36px;
  text-align: center;
  border-width: 0px 0px 1px 0px;
}

.login_view .login_form .btn_view {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  padding: 0px 0px 0px 120px;
  width: 100%;
}
.login_view .login_form .btn_view .login {
  margin: 0px 20px 12px 0px;
  padding: 0px 10px;
  width: auto;
  height: 34px;
  font-size: 16px;
  color: rgb(255, 255, 255);
  border-radius: 0px;
  border: 0px;
  background: var(--theme);
  cursor: pointer;
  min-width: 80px;
}
.login_view .login_form .btn_view .register {
  margin: 0px 20px 12px 0px;
  padding: 0px;
  width: auto;
  height: 34px;
  font-size: 16px;
  color: #333;
  border-radius: 4px;
  border: 0px;
  background: none;
  cursor: pointer;
}
.login_view .login_form .btn_view .register:hover {
  color: var(--theme);
}
.login_view .login_form .btn_view .forget {
  margin: 0px 10px 12px 0px;
  padding: 0px;
  width: 100%;
  height: 44px;
  font-size: 16px;
  color: rgb(102, 102, 102);
  border-radius: 4px;
  border: 0px;
  background: none;
  cursor: pointer;
  text-align: right;
}
.login_view .login_form .btn_view .forget:hover {
  text-decoration: underline;
}
.login_view .login_form .face {
  font-size: 16px;
  color: rgb(102, 102, 102);
  cursor: pointer;
  padding: 0px 0px 0px 120px;
  text-align: left;
  width: 100%;
}
</style>
