const config = {
    get() {
        return {
            url : process.env.VUE_APP_BASE_API_URL + process.env.VUE_APP_BASE_API + '/',
            name: process.env.VUE_APP_BASE_API,
        }
    },
    getProjectName(){
        return {
            projectName: "美食推荐分享系统"
        } 
    }
}
export default config
