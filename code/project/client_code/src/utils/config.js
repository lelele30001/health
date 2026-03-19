const config = {
  get() {
    return {
      url:
        process.env.VUE_APP_BASE_API_URL + process.env.VUE_APP_BASE_API + "/",
      name: process.env.VUE_APP_BASE_API,
      menuList: [
        {
          name: "论坛交流",
          icon: "icon-common46",
          child: [
            {
              name: "膳食分享",
              url: "/index/forumList",
            },
          ],
        },
      ],
    };
  },
  getProjectName() {
    return {
      projectName: "美食推荐分享系统",
    };
  },
};
export default config;
