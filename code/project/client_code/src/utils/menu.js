import toolUtil from "@/utils/toolUtil";
const menu = {
  list() {
    try {
      if (toolUtil.storageGet("menus")) {
        return JSON.parse(toolUtil.storageGet("menus"));
      } else {
        return null;
      }
    } catch (error) {
      console.error("解析菜单数据失败:", error);
      return null;
    }
  },
};
export default menu;
