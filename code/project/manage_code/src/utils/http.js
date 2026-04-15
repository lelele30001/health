import axios from "axios";
import router from "../router/index";
import toolUtil from "@/utils/toolUtil";
import config from "@/utils/config";
import { ElMessage } from "element-plus";

const http = axios.create({
  timeout: 1000 * 86400,
  withCredentials: true,
  baseURL:
    process.env.VUE_APP_BASE_API_URL + process.env.VUE_APP_BASE_API_PREFIX,
  headers: {
    "Content-Type": "application/json; charset=utf-8",
  },
});
// 请求拦截
http.interceptors.request.use(
  (config) => {
    console.log("管理员端请求的完整 URL:", config.baseURL + config.url);
    config.headers["Token"] = toolUtil.storageGet("Token"); // 请求头带上token
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
      // 不显示错误信息，直接跳转到登录页面
      router.push("/login");
      return Promise.resolve(response); // 返回resolve而不是reject，避免未捕获的Promise错误
    } else if (response.data && response.data.code == 0) {
      return response;
    } else {
      ElMessage.error(response.data.msg);
      return Promise.reject(response);
    }
  },
  (error) => {
    // 捕获所有错误，避免未捕获的Promise错误
    console.error("请求错误:", error);
    // 不显示404错误信息，避免打扰用户
    if (error.response && error.response.status !== 404) {
      ElMessage.error("请求失败，请稍后重试");
    }
    return Promise.reject(error);
  },
);
export default http;
