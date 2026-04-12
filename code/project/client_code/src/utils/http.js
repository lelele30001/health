import axios from "axios";
import router from "../router/index";
import toolUtil from "@/utils/toolUtil";
import config from "@/utils/config";
import { ElMessage } from "element-plus";

const http = axios.create({
  timeout: 1000 * 86400,
  withCredentials: true,
  baseURL: process.env.VUE_APP_BASE_API,
  headers: {
    "Content-Type": "application/json; charset=utf-8",
  },
});
// 请求拦截
http.interceptors.request.use(
  (config) => {
    config.headers["Token"] = toolUtil.storageGet("frontToken"); // 请求头带上token
    return config;
  },
  (error) => {
    return Promise.reject(error);
  },
);
// 响应拦截
http.interceptors.response.use(
  (response) => {
    if (response.data && response.data.code == 401) {
      // 401, token失效
      toolUtil.storageClear();
      toolUtil.storageSet("toPath", window.history.state.current);
      // 不显示错误信息，直接跳转到登录页面
      router.push("/login");
      return Promise.resolve(response); // 返回resolve而不是reject，避免未捕获的Promise错误
    } else if (response.data && response.data.code == 0) {
      return response;
    } else {
      // 只在非401错误时显示错误信息
      ElMessage.error(response.data.msg);
      return Promise.reject(response);
    }
  },
  (error) => {
    // 捕获所有错误，避免未捕获的Promise错误
    console.error("请求错误:", error);
    return Promise.reject(error);
  },
);
export default http;
