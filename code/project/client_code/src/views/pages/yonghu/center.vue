<template>
  <div class="center_view" :style="{}">
    <div class="section_title">
      <span>{{ formName }}</span>
    </div>
    <div class="usersView">
      <div class="usersTabView">
        <div
          class="usersTab"
          :class="tabIndex == 'center' ? 'usersTabActive' : ''"
          @click="tabClick({ tableName: 'center' })"
        >
          个人中心
        </div>
        <div
          class="usersTab"
          :class="tabIndex == 'updatePassword' ? 'usersTabActive' : ''"
          @click="tabClick({ tableName: 'updatePassword' })"
        >
          修改密码
        </div>
        <div
          class="usersTab"
          :class="tabIndex == 'profile' ? 'usersTabActive' : ''"
          @click="tabClick({ tableName: 'profile' })"
        >
          档案管理
        </div>
        <template v-for="(item, index) in menuList">
          <div
            v-if="item.child.length > 1"
            class="usersTab"
            @mouseenter="usersTabHover(index)"
            @mouseleave="usersTabLeave"
          >
            {{ item.menu }}
            <el-collapse-transition>
              <div class="usersTabHoverView" v-if="usersTabIndex == index">
                <div
                  class="usersTabHoverTab"
                  v-for="(items, indexs) in item.child"
                  @click="tabClick(items)"
                >
                  {{ items.menu }}
                </div>
              </div>
            </el-collapse-transition>
          </div>
          <div
            v-else-if="hasBack(item.child[0])"
            class="usersTab"
            @click="tabClick(item.child[0])"
          >
            {{ item.child[0].menu }}
          </div>
        </template>
        <div
          class="usersTab"
          :class="tabIndex == 'storeup' ? 'usersTabActive' : ''"
          @click="tabClick({ tableName: 'storeup', type: 1 })"
        >
          我的收藏
        </div>
        <div
          class="usersTab"
          @click="tabClick({ tableName: 'storeup', type: 51 })"
        >
          浏览历史
        </div>
      </div>
      <div class="usersBox updateInfo" v-if="tabIndex == 'center'">
        <el-form
          class="usersForm"
          ref="userFormRef"
          :model="userForm"
          label-width="120px"
          :rules="rules"
        >
          <el-row>
            <el-col :span="12">
              <el-form-item prop="yonghuzhanghao" label="用户账号">
                <el-input
                  class="list_inp"
                  v-model="userForm.yonghuzhanghao"
                  placeholder="用户账号"
                  readonly
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="yonghuxingming" label="用户姓名">
                <el-input
                  class="list_inp"
                  v-model="userForm.yonghuxingming"
                  placeholder="用户姓名"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item prop="touxiang" label="头像">
                <uploads
                  action="file/upload"
                  tip="请上传头像"
                  style="width: 100%; text-align: left"
                  :fileUrls="userForm.touxiang ? userForm.touxiang : ''"
                  @change="touxiangUploadSuccess"
                >
                </uploads>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别" prop="xingbie">
                <el-select
                  class="list_sel"
                  v-model="userForm.xingbie"
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
              <el-form-item prop="shoujihaoma" label="手机号码">
                <el-input
                  class="list_inp"
                  v-model="userForm.shoujihaoma"
                  placeholder="手机号码"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="shenfenzheng" label="身份证">
                <el-input
                  class="list_inp"
                  v-model="userForm.shenfenzheng"
                  placeholder="身份证"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="youxiang" label="邮箱">
                <el-input
                  class="list_inp"
                  v-model="userForm.youxiang"
                  placeholder="邮箱"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <div class="formModel_btn_box">
            <el-button class="formModel_confirm" @click="updateSession"
              >更新信息</el-button
            >
            <el-button class="formModel_cancel" @click="loginout" type="danger"
              >退出登录</el-button
            >
          </div>
        </el-form>
      </div>
      <div class="usersBox updatePassword" v-if="tabIndex == 'updatePassword'">
        <el-form
          class="usersForm"
          ref="passwordFormRef"
          :model="passwordForm"
          label-width="120px"
          :rules="passwordRules"
        >
          <el-row>
            <el-col :span="12">
              <el-form-item label="原密码" prop="mima">
                <el-input
                  class="list_inp"
                  v-model="passwordForm.mima"
                  placeholder="原密码"
                  type="password"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="新密码" prop="newmima">
                <el-input
                  class="list_inp"
                  v-model="passwordForm.newmima"
                  placeholder="新密码"
                  type="password"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="确认密码" prop="newmima2">
                <el-input
                  class="list_inp"
                  v-model="passwordForm.newmima2"
                  placeholder="确认密码"
                  type="password"
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <div class="formModel_btn_box">
            <el-button class="formModel_confirm" @click="updatePassword"
              >修改密码</el-button
            >
          </div>
        </el-form>
      </div>
      <div class="usersBox profile" v-if="tabIndex == 'profile'">
        <el-form
          class="usersForm"
          ref="profileFormRef"
          :model="profileForm"
          label-width="120px"
          :rules="profileRules"
        >
          <el-row>
            <el-col :span="12">
              <el-form-item label="身高(cm)" prop="height">
                <el-input
                  class="list_inp"
                  v-model="profileForm.height"
                  placeholder="身高"
                  type="number"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="体重(kg)" prop="weight">
                <el-input
                  class="list_inp"
                  v-model="profileForm.weight"
                  placeholder="体重"
                  type="number"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="年龄" prop="age">
                <el-input
                  class="list_inp"
                  v-model="profileForm.age"
                  placeholder="年龄"
                  type="number"
                ></el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="性别" prop="gender">
                <el-select
                  class="list_sel"
                  v-model="profileForm.gender"
                  placeholder="请选择性别"
                  style="width: 100%"
                >
                  <el-option label="男" value="男"></el-option>
                  <el-option label="女" value="女"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="健身目标" prop="fitnessGoal">
                <el-select
                  class="list_sel"
                  v-model="profileForm.fitnessGoal"
                  placeholder="请选择健身目标"
                  style="width: 100%"
                >
                  <el-option label="增肌" value="增肌"></el-option>
                  <el-option label="减脂" value="减脂"></el-option>
                  <el-option label="维持" value="维持"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="饮食禁忌" prop="dietaryRestrictions">
                <el-select
                  class="list_sel"
                  v-model="profileForm.dietaryRestrictions"
                  placeholder="请选择饮食禁忌"
                  multiple
                  style="width: 100%"
                >
                  <el-option label="海鲜" value="海鲜"></el-option>
                  <el-option label="乳制品" value="乳制品"></el-option>
                  <el-option label="花生" value="花生"></el-option>
                  <el-option label="鸡蛋" value="鸡蛋"></el-option>
                  <el-option label="牛羊肉" value="牛羊肉"></el-option>
                  <el-option label="猪肉" value="猪肉"></el-option>
                  <el-option label="辛辣" value="辛辣"></el-option>
                  <el-option label="油腻" value="油腻"></el-option>
                  <el-option label="甜食" value="甜食"></el-option>
                  <el-option label="坚果" value="坚果"></el-option>
                  <el-option label="生冷" value="生冷"></el-option>
                  <el-option label="油炸" value="油炸"></el-option>
                  <el-option label="高糖" value="高糖"></el-option>
                  <el-option label="高盐" value="高盐"></el-option>
                  <el-option label="高脂" value="高脂"></el-option>
                  <el-option label="素食" value="素食"></el-option>
                  <el-option label="清真" value="清真"></el-option>
                  <el-option
                    label="gluten-free"
                    value="gluten-free"
                  ></el-option>
                  <el-option label="乳糖不耐" value="乳糖不耐"></el-option>
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="每日热量需求(kcal)">
                <el-input
                  class="list_inp"
                  v-model="profileForm.dailyCalories"
                  placeholder="每日热量需求"
                  readonly
                ></el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <div class="formModel_btn_box">
            <el-button class="formModel_confirm" @click="saveProfile"
              >保存档案</el-button
            >
          </div>
        </el-form>
      </div>
      <div class="usersBox storeup" v-if="tabIndex == 'storeup'">
        <div class="forum_list">
          <forum-item
            v-for="(item, index) in storeupList"
            :key="index"
            :item="item"
            :index="index"
            :userid="Number(userid)"
            :forum-show-index="forumShowIndex"
            :btn-auth="btnAuth"
            @mouseenter="(event) => forumEnter(index)"
            @mouseleave="forumLeave"
            @click="(id) => detailClick(id)"
            @del="(id) => delClick(id)"
          />
          <el-pagination
            background
            :layout="storeupLayouts.join(',')"
            :total="storeupTotal"
            :page-size="storeupListQuery.limit"
            v-model:current-page="storeupListQuery.page"
            prev-text="上一页"
            next-text="下一页"
            :hide-on-single-page="false"
            @size-change="storeupSizeChange"
            @current-change="storeupCurrentChange"
          />
        </div>
      </div>

      <forum-detail
        v-model:visible="detailVisible"
        :detail-form="detailForm"
        :userid="Number(userid)"
        :like-count="likeCount"
        :collect-count="collectCount"
        :dislike-count="dislikeCount"
        :is-liked="isLiked"
        :is-collected="isCollected"
        :is-disliked="isDisliked"
        :btn-auth="btnAuth"
        @del="delClick"
        @record-behavior="recordBehavior"
      />
    </div>
  </div>
</template>
<script setup>
import { ElMessageBox } from "element-plus";
import moment from "moment";
import { ref, getCurrentInstance, watch, onUnmounted, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useStore } from "vuex";
const store = useStore();
import menu from "@/utils/menu";
import ForumItem from "@/components/ForumItem.vue";
import ForumDetail from "@/components/ForumDetail.vue";
const context = getCurrentInstance()?.appContext.config.globalProperties;
const route = useRoute();
const router = useRouter();
//基础信息
const tableName = "yonghu";
const formName = "个人中心";
//基础信息
const uploadUrl = context?.$config?.url + "file/upload";
//个人信息
const userFormRef = ref(null);
const userForm = ref({});
//修改密码
const passwordFormRef = ref(null);
const passwordForm = ref({
  mima: "",
  newmima: "",
  newmima2: "",
});
const passwordRules = ref({
  mima: [
    {
      required: true,
      message: "请输入",
      trigger: "blur",
    },
  ],
  newmima: [
    {
      required: true,
      message: "请输入",
      trigger: "blur",
    },
  ],
  newmima2: [
    {
      required: true,
      message: "请输入",
      trigger: "blur",
    },
  ],
});
//档案管理
const profileFormRef = ref(null);
const profileForm = ref({
  height: "",
  weight: "",
  age: "",
  gender: "男",
  fitnessGoal: "维持",
  dietaryRestrictions: "无",
  dailyCalories: "",
  userId: "",
});

//收藏管理
const storeupList = ref([]);
const storeupListQuery = ref({
  page: 1,
  limit: 20,
  sort: "id",
  order: "desc",
  type: 1,
});
const storeupTotal = ref(0);
const storeupSearchQuery = ref({});
const storeupLayouts = ref([
  "total",
  "prev",
  "pager",
  "next",
  "sizes",
  "jumper",
]);

//用户ID
const userid = ref(context?.$toolUtil.storageGet("userid"));

//详情弹窗相关变量
const detailVisible = ref(false);
const detailForm = ref({});
const isLiked = ref(false);
const isCollected = ref(false);
const isDisliked = ref(false);
const likeCount = ref(0);
const collectCount = ref(0);
const dislikeCount = ref(0);

//权限验证
const btnAuth = (e, a) => {
  return context?.$toolUtil.isAuth(e, a);
};

//获取收藏列表
const getStoreupList = () => {
  let params = JSON.parse(JSON.stringify(storeupListQuery.value));
  context
    ?.$http({
      url: "storeup/page",
      method: "get",
      params: params,
    })
    .then((res) => {
      storeupList.value = res.data.data.list;
      storeupTotal.value = Number(res.data.data.total);
    });
};

//收藏分页
const storeupSizeChange = (size) => {
  storeupListQuery.value.limit = size;
  getStoreupList();
};

const storeupCurrentChange = (page) => {
  storeupListQuery.value.page = page;
  getStoreupList();
};

//收藏详情
const detailClick = (id) => {
  console.log("detailClick called with id:", id, "type:", typeof id);
  // 验证id是否有效
  if (!id || isNaN(id)) {
    console.error("Invalid id:", id);
    return;
  }
  // 确保id是数字类型
  const recipeId = Number(id);
  console.log("Converted recipeId:", recipeId, "type:", typeof recipeId);
  context
    ?.$http({
      url: `recipe/info/${recipeId}`,
      method: "get",
    })
    .then((res) => {
      console.log("API response:", res);
      // 检查响应数据结构
      if (res && res.data) {
        if (res.data.recipe) {
          // 后端直接返回recipe
          detailForm.value = res.data.recipe;
          detailVisible.value = true;
          // 获取行为数量和用户行为状态
          getBehaviorCounts(recipeId);
          checkUserBehavior(recipeId);
        } else {
          console.error("响应数据结构不符合预期:", res);
          context?.$toolUtil.message("获取食谱详情失败", "error");
        }
      } else {
        console.error("响应数据结构不符合预期:", res);
        context?.$toolUtil.message("获取食谱详情失败", "error");
      }
    })
    .catch((error) => {
      console.error("请求失败:", error);
      context?.$toolUtil.message("获取食谱详情失败", "error");
    });
};

//收藏删除
const storeupDelClick = (item) => {
  context
    .$confirm("确定要删除该收藏吗？", "提示")
    .then(() => {
      context.$http.post("storeup/delete", [item.id]).then((res) => {
        if (res.data.code == 0) {
          getStoreupList();
          context.$message.success("删除成功");
        }
      });
    })
    .catch(() => {});
};

//删除方法
const delClick = (item) => {
  if (item.id) {
    context
      .$confirm("确定要删除该收藏吗？", "提示")
      .then(() => {
        context.$http.post("storeup/delete", [item.id]).then((res) => {
          if (res.data.code == 0) {
            getStoreupList();
            context.$message.success("删除成功");
            detailVisible.value = false;
          }
        });
      })
      .catch(() => {});
  } else if (item) {
    context
      .$confirm("确定要删除该收藏吗？", "提示")
      .then(() => {
        context.$http.post("storeup/delete", [item.id]).then((res) => {
          if (res.data.code == 0) {
            getStoreupList();
            context.$message.success("删除成功");
          }
        });
      })
      .catch(() => {});
  }
};

// 获取行为数量
const getBehaviorCounts = (recipeId) => {
  context
    .$http({
      url: `user-behavior/recipe/count`,
      method: "get",
      params: { recipeId },
    })
    .then((res) => {
      if (res && res.data && res.data.code === 0) {
        likeCount.value = res.data.likeCount || 0;
        collectCount.value = res.data.collectCount || 0;
        dislikeCount.value = res.data.dislikeCount || 0;
      }
    })
    .catch((error) => {
      console.error("获取行为数量失败:", error);
    });
};

// 检查用户行为
const checkUserBehavior = (recipeId) => {
  const userId = context?.$toolUtil.storageGet("userid");
  if (!userId) return;

  // 检查点赞
  context
    .$http({
      url: `user-behavior/get`,
      method: "get",
      params: {
        recipeId: Number(recipeId),
        behaviorType: "like",
        userId: Number(userId),
      },
    })
    .then((res) => {
      if (res && res.data && res.data.code === 0 && res.data.behavior) {
        isLiked.value = true;
      } else {
        isLiked.value = false;
      }
    })
    .catch(() => {
      isLiked.value = false;
    });

  // 检查收藏
  context
    .$http({
      url: `user-behavior/get`,
      method: "get",
      params: {
        recipeId: Number(recipeId),
        behaviorType: "collect",
        userId: Number(userId),
      },
    })
    .then((res) => {
      if (res && res.data && res.data.code === 0 && res.data.behavior) {
        isCollected.value = true;
      } else {
        isCollected.value = false;
      }
    })
    .catch(() => {
      isCollected.value = false;
    });

  // 检查踩
  context
    .$http({
      url: `user-behavior/get`,
      method: "get",
      params: {
        recipeId: Number(recipeId),
        behaviorType: "dislike",
        userId: Number(userId),
      },
    })
    .then((res) => {
      if (res && res.data && res.data.code === 0 && res.data.behavior) {
        isDisliked.value = true;
      } else {
        isDisliked.value = false;
      }
    })
    .catch(() => {
      isDisliked.value = false;
    });
};

// 记录用户行为
const recordBehavior = (behaviorType) => {
  const userId = context?.$toolUtil.storageGet("userid");
  if (!userId) {
    context?.$toolUtil.message("请先登录", "error");
    return;
  }

  const recipeId = detailForm.value.id;
  if (!recipeId) return;

  // 检查当前状态，决定是记录还是删除行为
  let isCurrentlyActive = false;
  switch (behaviorType) {
    case "like":
      isCurrentlyActive = isLiked.value;
      break;
    case "collect":
      isCurrentlyActive = isCollected.value;
      break;
    case "dislike":
      isCurrentlyActive = isDisliked.value;
      break;
  }

  if (isCurrentlyActive) {
    // 取消行为
    context
      .$http({
        url: `user-behavior/delete`,
        method: "delete",
        params: {
          recipeId: Number(recipeId),
          behaviorType,
          userId: Number(userId),
        },
      })
      .then((res) => {
        if (res && res.data && res.data.code === 0) {
          // 更新本地状态
          switch (behaviorType) {
            case "like":
              isLiked.value = false;
              likeCount.value -= 1;
              break;
            case "collect":
              isCollected.value = false;
              collectCount.value -= 1;
              // 如果取消收藏，从收藏列表中移除
              getStoreupList();
              break;
            case "dislike":
              isDisliked.value = false;
              dislikeCount.value -= 1;
              break;
          }
          context?.$toolUtil.message("取消成功", "success");
        } else {
          context?.$toolUtil.message("操作失败", "error");
        }
      })
      .catch((error) => {
        console.error("取消行为失败:", error);
        context?.$toolUtil.message("操作失败", "error");
      });
  } else {
    // 记录行为
    const behaviorData = {
      userId: Number(userId),
      recipeId: Number(recipeId),
      behaviorType,
    };

    context
      .$http({
        url: `user-behavior/record`,
        method: "post",
        data: behaviorData,
      })
      .then((res) => {
        if (res && res.data && res.data.code === 0) {
          // 更新本地状态
          switch (behaviorType) {
            case "like":
              isLiked.value = true;
              likeCount.value += 1;
              break;
            case "collect":
              isCollected.value = true;
              collectCount.value += 1;
              break;
            case "dislike":
              isDisliked.value = true;
              dislikeCount.value += 1;
              break;
          }
          context?.$toolUtil.message("操作成功", "success");
        } else {
          context?.$toolUtil.message("操作失败", "error");
        }
      })
      .catch((error) => {
        console.error("记录行为失败:", error);
        context?.$toolUtil.message("操作失败", "error");
      });
  }
};

//论坛item悬浮
const forumShowIndex = ref(-1);
const forumEnter = (index) => {
  forumShowIndex.value = index;
};
const forumLeave = () => {
  forumShowIndex.value = -1;
};
const profileRules = ref({
  height: [
    { required: true, message: "请输入身高", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入身高"));
        } else if (isNaN(Number(value))) {
          callback(new Error("请输入数字"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  weight: [
    { required: true, message: "请输入体重", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入体重"));
        } else if (isNaN(Number(value))) {
          callback(new Error("请输入数字"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  age: [
    { required: true, message: "请输入年龄", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value === "") {
          callback(new Error("请输入年龄"));
        } else if (isNaN(Number(value))) {
          callback(new Error("请输入数字"));
        } else {
          callback();
        }
      },
      trigger: "blur",
    },
  ],
  gender: [{ required: true, message: "请选择性别", trigger: "blur" }],
  fitnessGoal: [{ required: true, message: "请选择健身目标", trigger: "blur" }],
});
//验证规则
const rules = ref({
  yonghuzhanghao: [{ required: true, message: "请输入", trigger: "blur" }],
  yonghumima: [{ required: true, message: "请输入", trigger: "blur" }],
  yonghuxingming: [{ required: true, message: "请输入", trigger: "blur" }],
  touxiang: [],
  xingbie: [],
  shoujihaoma: [
    { validator: context.$toolUtil.validator.mobile, trigger: "blur" },
  ],
  shenfenzheng: [
    { validator: context.$toolUtil.validator.idCard, trigger: "blur" },
  ],
  youxiang: [{ validator: context.$toolUtil.validator.email, trigger: "blur" }],
});
const getSession = () => {
  context
    ?.$http({
      url: `${context?.$toolUtil.storageGet("frontSessionTable")}/session`,
      method: "get",
    })
    .then((res) => {
      context?.$toolUtil.storageSet("userid", res.data.data.id);
      context?.$toolUtil.storageSet("frontName", res.data.data.yonghuzhanghao);
      context?.$toolUtil.storageSet("headportrait", res.data.data.touxiang);
      userForm.value = res.data.data;
      // 更新store中的session状态，确保顶部头像同步更新
      store.dispatch("user/getSession");
      // 获取用户档案
      getProfile(res.data.data.id);
    });
};

// 获取用户档案
const getProfile = (userId) => {
  if (context?.$http) {
    context
      .$http({
        url: `user/profile/get`,
        method: "get",
        params: { userId },
      })
      .then((res) => {
        if (res?.data?.profile) {
          // 确保数据类型正确
          const profileData = res.data.profile;
          profileForm.value = {
            ...profileForm.value,
            ...profileData,
            height: profileData.height || "",
            weight: profileData.weight || "",
            age: profileData.age || "",
            dailyCalories: profileData.dailyCalories || "",
            dietaryRestrictions: profileData.dietaryRestrictions
              ? profileData.dietaryRestrictions.split(",")
              : [],
            userId: userId,
          };
        } else {
          // 设置默认值
          profileForm.value = {
            height: "",
            weight: "",
            age: "",
            gender: "男",
            fitnessGoal: "维持",
            dietaryRestrictions: "无",
            dailyCalories: "",
            userId: userId,
          };
        }
      })
      .catch((error) => {
        console.error("获取档案失败:", error);
        // 发生错误时设置默认值
        profileForm.value = {
          height: "",
          weight: "",
          age: "",
          gender: "男",
          fitnessGoal: "维持",
          dietaryRestrictions: "无",
          dailyCalories: "",
          userId: userId,
        };
      });
  }
};

// 保存用户档案
const saveProfile = () => {
  if (profileFormRef.value) {
    profileFormRef.value.validate((valid) => {
      if (valid && context?.$http) {
        // 确保数据类型正确
        const formData = {
          ...profileForm.value,
          height: Number(profileForm.value.height),
          weight: Number(profileForm.value.weight),
          age: parseInt(profileForm.value.age),
        };

        // 将dietaryRestrictions数组转换为逗号分隔的字符串
        if (Array.isArray(formData.dietaryRestrictions)) {
          formData.dietaryRestrictions = formData.dietaryRestrictions.join(",");
        }

        console.log("保存档案请求数据:", formData);

        context
          .$http({
            url: `user/profile/save`,
            method: "post",
            data: formData,
          })
          .then((res) => {
            console.log("保存档案响应:", res);
            if (res?.data?.code == 0) {
              context?.$toolUtil?.message("保存成功", "success");
              // 保持表单内容不消失，只更新热量计算结果
              if (res.data?.profile?.dailyCalories) {
                profileForm.value.dailyCalories =
                  res.data.profile.dailyCalories.toString();
              }
            } else {
              context?.$toolUtil?.message(
                res?.data?.msg || "保存失败",
                "error"
              );
            }
          })
          .catch((error) => {
            console.error("保存档案失败:", error);
            context?.$toolUtil?.message("网络错误", "error");
          });
      } else {
        console.log("表单验证失败");
      }
    });
  } else {
    console.log("profileFormRef 未定义");
  }
};
//菜单跳转
const tabIndex = ref("center");
const tabClick = (item) => {
  if (item.tableName == "center") {
    tabIndex.value = "center";
    return;
  }
  if (item.tableName == "updatePassword") {
    passwordForm.value = {
      mima: "",
      newmima: "",
      newmima2: "",
    };
    tabIndex.value = "updatePassword";
    return;
  }
  if (item.tableName == "profile") {
    tabIndex.value = "profile";
    return;
  }
  if (item.tableName == "examrecord" && item.menuJump == "22") {
    router.push(`/index/examfailrecord?centerType=1`);
    return;
  }
  if (item.tableName == "forum" && item.menuJump == "14") {
    router.push(`/index/forumList?centerType=1&myType=1`);
    return;
  }
  if (item.tableName == "storeup" && item.type == 1) {
    tabIndex.value = "storeup";
    getStoreupList();
    return;
  }
  if (item.tableName == "storeup" && item.type == 51) {
    router.push(`/index/storeupList?centerType=1&&type=${item.type}`);
    return;
  }
  router.push(
    `/index/${item.classname || item.tableName}List?centerType=1${
      item.menuJump ? "&menuJump=" + item.menuJump : ""
    }`
  );
};
const hasBack = (menu) => {
  if (menu.tableName == "storeup") {
    return false;
  }
  return true;
};
// 修改密码
const updatePassword = async () => {
  passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      if (passwordForm.value.mima != userForm.value.yonghumima) {
        context?.$toolUtil.message("原密码不正确", "error");
        return false;
      }
      if (passwordForm.value.newmima != passwordForm.value.newmima2) {
        context?.$toolUtil.message("两次密码输入不正确", "error");
        return false;
      }
      userForm.value.yonghumima = passwordForm.value.newmima;
      store.dispatch("user/update", userForm.value).then((res) => {
        if (res?.data && res.data.code == 0) {
          context?.$toolUtil.message("更新成功", "success");
          passwordForm.value = {
            mima: "",
            newmima: "",
            newmima2: "",
          };
          getSession();
        }
      });
    }
  });
};
//菜单
const menuList = ref([]);
const role = ref("");
//头像上传回调
const touxiangUploadSuccess = (e) => {
  userForm.value.touxiang = e;
};
//性别列表
const xingbieLists = ref([]);
//初始化
const init = () => {
  const menus = menu.list();
  let arr = [];
  if (menus) {
    menuList.value = menus;
  }
  role.value = context?.$toolUtil.storageGet("frontRole");
  for (let i = 0; i < menuList.value.length; i++) {
    if (menuList.value[i].roleName == role.value) {
      arr = menuList.value[i].backMenu;
      break;
    }
  }
  menuList.value = arr;
  xingbieLists.value = "男,女".split(",");
  getSession();
};

// 监听路由变化，当用户切换时重新获取用户信息
watch(
  () => router.currentRoute.value,
  () => {
    getSession();
  },
  { immediate: true }
);
//菜单悬浮的显示与隐藏
const usersTabIndex = ref(-1);
const usersTabHover = (index) => {
  usersTabIndex.value = index;
};
const usersTabLeave = () => {
  usersTabIndex.value = -1;
};
//富文本
const editorChange = (e, name) => {
  userForm.value[name] = e;
};
//保存
const updateSession = () => {
  userFormRef.value.validate((valid) => {
    if (valid) {
      if (userForm.value.touxiang != null) {
        userForm.value.touxiang = userForm.value.touxiang.replace(
          new RegExp(context?.$config.url, "g"),
          ""
        );
      }
      store.dispatch("user/update", userForm.value).then((res) => {
        if (res?.data && res.data.code == 0) {
          context?.$toolUtil.message("更新成功", "success");
          getSession();
        }
      });
    }
  });
};
//退出登录
const loginout = () => {
  context?.$toolUtil.message("退出成功", "success");
  context?.$toolUtil.storageClear();
  router.replace("/index/home");
};
init();
</script>

<style lang="scss" scoped>
.usersView {
  .usersTabView {
    .usersTab {
      .usersTabHoverView {
        padding: 10px 0px;
        z-index: 999;
        top: 0px;
        background: var(--theme);
        display: block;
        width: 200px;
        position: absolute;
        right: -200px;
        .usersTabHoverTab {
          color: #fff;
          line-height: 40px;
        }
        .usersTabHoverTab:hover {
          color: #ff0;
        }
      }
    }
    .usersTab:hover {
    }
    .usersTabActive {
    }
  }

  .usersBox {
    .usersForm {
      // form item
      :deep(.el-form-item) {
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
  }
  // 按钮盒子
  .formModel_btn_box {
    .formModel_cancel {
    }
    .formModel_cancel:hover {
    }

    .formModel_confirm {
    }
    .formModel_confirm:hover {
    }
  }
}
</style>
<style>
/**总盒子**/
.center_view {
  width: 1200px;
  margin: 20px auto;
  padding: 0px;
  background: #fff;
  font-size: 16px;
  box-shadow: 0 2px 4px 0 rgba(0, 0, 0, 0.08);
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

/**内容区**/
.center_view .usersView {
  flex: 1;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin: 20px auto;
  padding: 20px;
}
/**标题**/
.center_view .section_title {
  width: 100%;
  margin: 0px 0 20px;
  z-index: 999;
  text-align: left;
  background: none;
  margin-left: -10px;
  border-bottom: 1px #e5e5e5 solid;
  position: relative;
}
.center_view .section_title span {
  display: inline-block;
  font-size: 20px;
  background: var(--theme);
  color: #fff;
  font-weight: 500;
  padding: 15px 20px 15px 20px;
  min-width: 250px;
}
.center_view .section_title:after {
  content: "";
  height: 1px;
  width: 1px;
  border-style: solid;
  border-width: 5px;
  position: absolute;
  bottom: -10px;
  left: 0;
  border-color: #222 #222 transparent transparent;
}

/**内容区**/
.center_view .usersView {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin: 20px auto;
  padding: 20px;
}

/**tab总盒子**/
.center_view .usersView .usersTabView {
  width: 200px;
  background: none;
  padding: 6px 10px;
  box-sizing: border-box;
}
/**item**/
.center_view .usersView .usersTabView .usersTab {
  width: 100%;
  font-size: 16px;
  padding: 10px 0px;
  margin: 0px 0px 10px;
  position: relative;
  cursor: pointer;
  text-align: center;
  background: #fff;
  color: rgb(51, 51, 51);
  border: 1px solid #ddd;
  border-top: 4px solid #ddd;
}
.center_view .usersView .usersTabView .usersTab:hover {
  background: var(--theme);
  color: #fff;
}
.center_view .usersView .usersTabView .usersTab.usersTabActive {
  width: 100%;
  font-size: 16px;
  padding: 10px 0px;
  margin: 0px 0px 10px;
  position: relative;
  cursor: pointer;
  text-align: center;
  background: var(--theme);
  color: #fff;
}

/**右部 总盒子**/
.center_view .usersView .usersBox {
  width: calc(100% - 220px);
  background: #fff;
  margin: 0px;
  padding: 0px;
  box-sizing: border-box;
  box-shadow: none;
  border: 1px solid #eee;
}
/**form盒子**/
.center_view .usersView .usersBox .usersForm {
  width: 100%;
  padding: 30px;
}
.center_view .usersView .usersBox .usersForm .el-form-item {
  margin: 0px 0px 20px;
  display: flex;
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__label {
  width: 150px;
  background: none;
  text-align: right;
  display: block;
  font-size: 16px;
  color: rgb(51, 51, 51);
  font-weight: 500;
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content {
  display: flex;
  justify-content: flex-start;
  align-items: center;
  flex-wrap: wrap;
  width: calc(100% - 120px);
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_inp {
  height: 36px;
  line-height: 36px;
  border: 1px solid rgb(221, 221, 221);
  padding: 0px 10px;
  width: 100%;
  box-sizing: border-box;
  background: rgb(255, 255, 255);
  font-size: 16px;
}

/**会员**/
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .vip_item {
  display: flex;
  align-items: center;
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .vip_item
  .vip_inp {
  height: 36px;
  line-height: 36px;
  border: 1px solid rgb(221, 221, 221);
  padding: 0px 10px;
  width: 80%;
  box-sizing: border-box;
  background: rgb(255, 255, 255);
  font-size: 16px;
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .vip_item
  .vip_btn {
  height: 36px;
  line-height: 36px;
  padding: 0px 20px;
  border-radius: 0px;
  margin: 0px 0px 0px 5px;
  background: var(--theme);
  border-color: var(--theme);
  color: #fff;
}
/**end**/

/**下拉选择**/
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_sel {
  line-height: 36px;
  border: 1px solid rgb(221, 221, 221);
  box-sizing: border-box;
  width: calc(100% - 120px);
  padding: 0px 10px;
  border-radius: 0px;
  background: rgb(255, 255, 255);
  font-size: 16px;
}
/**end**/

/**日期选择**/
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_date {
  line-height: 36px;
  border: 1px solid rgb(221, 221, 221);
  box-sizing: border-box;
  width: 100%;
  border-radius: 0px;
  background: rgb(255, 255, 255);
  font-size: 16px;
}
/**end**/

/**radio**/
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_radio {
  display: flex;
  width: calc(100% - 120px);
  align-items: center;
  flex-wrap: wrap;
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_radio
  .el-radio {
  width: 30%;
  margin: 0px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_radio
  .el-radio
  .el-radio__inner {
  border-color: #999;
  background: #fff;
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_radio
  .el-radio
  .el-radio__label {
  color: #999;
  font-size: 16px;
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_radio
  .el-radio.is-checked
  .el-radio__inner {
  border-color: var(--theme);
  background: var(--theme);
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_radio
  .el-radio.is-checked
  .el-radio__label {
  color: var(--theme);
  font-size: 16px;
}
/**end**/

/**checkbox**/
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_checkbox {
  display: flex;
  width: calc(100% - 120px);
  flex-wrap: wrap;
  align-items: center;
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_checkbox
  .el-checkbox {
  width: 20%;
  margin: 0px;
  display: flex;
  justify-content: center;
  align-items: center;
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_checkbox
  .el-checkbox
  .el-checkbox__inner {
  border-color: #999;
  background: #fff;
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_checkbox
  .el-checkbox
  .el-checkbox__label {
  color: #999;
  font-size: 16px;
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_checkbox
  .el-checkbox.is-checked
  .el-checkbox__inner {
  border-color: var(--theme);
  background: var(--theme);
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_checkbox
  .el-checkbox.is-checked
  .el-checkbox__label {
  color: var(--theme);
  font-size: 16px;
}
/**end**/

/**textarea**/
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_textarea {
}
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_textarea
  .el-textarea__inner {
  width: 100%;
  min-height: 150px;
  padding: 12px;
  border: 1px solid rgb(221, 221, 221);
  border-radius: 0px;
  color: #666;
  font-size: 16px;
}
/**end**/

/**图片上传**/
/* 盒子 */
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .el-upload--picture-card {
  background-color: rgb(255, 255, 255);
  width: 100px;
  height: 90px;
  line-height: 100px;
  text-align: center;
  border: 1px solid rgb(221, 221, 221);
  border-radius: 0px;
  cursor: pointer;
}
/* 图标 */
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .el-upload--picture-card
  .el-icon {
  font-size: 32px;
  color: #999;
}
/* 提示语 */
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .img-uploader
  .el-upload__tip {
  font-size: 15px;
  color: #666;
  margin: 0;
}
/**end**/

/**文件上传**/
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .el-upload--text
  .el-upload-dragger {
  background-color: rgb(255, 255, 255);
  border: 1px solid rgb(221, 221, 221);
  border-radius: 0px;
  box-sizing: border-box;
  width: 360px;
  height: 180px;
  text-align: center;
  cursor: pointer;
  overflow: hidden;
}
/* 图标 */
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .el-upload--text
  .el-upload-dragger
  .el-icon-upload {
  font-size: 67px;
  color: var(--theme);
  line-height: 50px;
}
/* 提示语 */
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .upload-demo
  .el-upload__tip {
  font-size: 15px;
  color: #666;
  margin: 0px 0 0;
  line-height: 1;
}
/* 点击上传 */
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .el-upload--text
  .el-upload-dragger
  em {
  color: var(--theme);
  font-size: 15px;
}
/**end**/

/**富文本**/
.center_view
  .usersView
  .usersBox
  .usersForm
  .el-form-item
  .el-form-item__content
  .list_editor {
  width: 100%;
  height: auto;
  margin: 0px;
  padding: 0px;
  border-radius: 0px;
  border-width: 0px;
  border-style: solid;
  border-color: rgb(204, 204, 204);
  background-color: rgb(255, 255, 255);
}
/**end**/

/**按钮**/
.center_view .formModel_btn_box {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 10px 0px 10px 150px;
  margin: 40px 0px 0px;
}
/**更新信息**/
.center_view .formModel_btn_box .formModel_confirm {
  margin: 0px 20px 0px 0px;
  padding: 0px 24px;
  width: auto;
  height: 36px;
  font-size: 16px;
  color: rgb(255, 255, 255);
  border-radius: 0px;
  border: 0px;
  background: var(--theme);
  cursor: pointer;
}
.center_view .formModel_btn_box .formModel_confirm:hover {
  background: rgba(254, 176, 66, 0.8);
}
/**退出登陆**/
.center_view .formModel_btn_box .formModel_cancel {
  margin: 0px 20px 0px 0px;
  padding: 0px 24px;
  width: auto;
  height: 36px;
  font-size: 16px;
  color: rgb(255, 255, 255);
  border-radius: 0px;
  border: 0px;
  background: rgba(66, 66, 66, 1);
  cursor: pointer;
}
.center_view .formModel_btn_box .formModel_cancel:hover {
  background: rgba(66, 66, 66, 0.8);
}

/**收藏列表样式**/
.center_view .usersView .usersBox .forum_list {
  width: 100%;
  margin: 20px auto;
  padding: 20px;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
}

.center_view .usersView .usersBox .forum_list .forum_item {
  width: 100%;
  cursor: pointer;
  display: flex;
  flex-wrap: wrap;
  margin: 0px;
  height: auto;
  background: rgb(255, 255, 255);
  justify-content: space-between;
  padding: 20px 10px;
  background: none;
  margin: 0 0 20px;
  box-shadow: 0 0 0px rgba(20, 49, 49, 0.1);
  min-height: 90px;
  border: 1px solid #eee;
}

.center_view .usersView .usersBox .forum_list .forum_item:hover {
  background: none;
  color: #333;
}

.center_view .usersView .usersBox .forum_list .forum_item .forum_item_left {
  display: flex;
  align-items: center;
}

.center_view
  .usersView
  .usersBox
  .forum_list
  .forum_item
  .forum_item_left
  .forum_item_title {
  font-size: 16px;
  color: rgb(51, 51, 51);
  font-weight: 600;
}

.center_view .usersView .usersBox .forum_list .forum_item .forum_item_btn_box {
  width: auto;
}

.center_view
  .usersView
  .usersBox
  .forum_list
  .forum_item
  .forum_item_btn_box
  .forum_del_btn {
  margin: 0px 10px 0px 0px;
  padding: 0px 24px;
  width: auto;
  height: 40px;
  font-size: 14px;
  color: rgb(255, 255, 255);
  border-radius: 4px;
  border: 0px;
  background: rgba(254, 66, 66, 0.8);
  cursor: pointer;
}

.center_view
  .usersView
  .usersBox
  .forum_list
  .forum_item
  .forum_item_btn_box
  .forum_del_btn:hover {
  background: rgba(254, 66, 66, 1);
}
</style>
