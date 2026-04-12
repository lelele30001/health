<template>
  <div class="center_view" :style="{}">
    <div class="section_title">
      <span>{{ formName }}</span>
    </div>
    <!-- 顶部横向导航 -->
    <div class="top-nav">
      <div
        class="nav-item"
        :class="tabIndex == 'center' ? 'nav-item-active' : ''"
        @click="tabClick({ tableName: 'center' })"
      >
        个人中心
      </div>
      <div
        class="nav-item"
        :class="tabIndex == 'updatePassword' ? 'nav-item-active' : ''"
        @click="tabClick({ tableName: 'updatePassword' })"
      >
        修改密码
      </div>
      <div
        class="nav-item"
        :class="tabIndex == 'profile' ? 'nav-item-active' : ''"
        @click="tabClick({ tableName: 'profile' })"
      >
        档案管理
      </div>
      <div
        class="nav-item"
        :class="tabIndex == 'health' ? 'nav-item-active' : ''"
        @click="tabClick({ tableName: 'health' })"
      >
        健康管理
      </div>
      <template v-for="(item, index) in menuList">
        <div
          v-if="item.child.length > 1"
          class="nav-item"
          @mouseenter="usersTabHover(index)"
          @mouseleave="usersTabLeave"
        >
          {{ item.menu }}
          <el-collapse-transition>
            <div class="nav-item-dropdown" v-if="usersTabIndex == index">
              <div
                class="nav-item-dropdown-item"
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
          class="nav-item"
          @click="tabClick(item.child[0])"
        >
          {{ item.child[0].menu }}
        </div>
      </template>
      <div
        class="nav-item"
        :class="tabIndex == 'storeup' ? 'nav-item-active' : ''"
        @click="tabClick({ tableName: 'storeup', type: 1 })"
      >
        我的收藏
      </div>
      <div
        class="nav-item"
        @click="tabClick({ tableName: 'storeup', type: 51 })"
      >
        浏览历史
      </div>
    </div>
    <!-- 内容区域 -->
    <div class="content-area">
      <!-- 个人中心 -->
      <div class="content-section" v-if="tabIndex == 'center'">
        <div class="card">
          <h3 class="card-title">用户信息</h3>
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
                    v-model="userForm.yonghuzhanghao"
                    placeholder="用户账号"
                    readonly
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="yonghuxingming" label="用户姓名">
                  <el-input
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
                    v-model="userForm.shoujihaoma"
                    placeholder="手机号码"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="shenfenzheng" label="身份证">
                  <el-input
                    v-model="userForm.shenfenzheng"
                    placeholder="身份证"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item prop="youxiang" label="邮箱">
                  <el-input
                    v-model="userForm.youxiang"
                    placeholder="邮箱"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <div class="formModel_btn_box">
              <el-button type="primary" @click="updateSession"
                >更新信息</el-button
              >
              <el-button type="danger" @click="loginout">退出登录</el-button>
            </div>
          </el-form>
        </div>
      </div>

      <!-- 修改密码 -->
      <div class="content-section" v-if="tabIndex == 'updatePassword'">
        <div class="card">
          <h3 class="card-title">修改密码</h3>
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
                    v-model="passwordForm.mima"
                    placeholder="原密码"
                    type="password"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="新密码" prop="newmima">
                  <el-input
                    v-model="passwordForm.newmima"
                    placeholder="新密码"
                    type="password"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="确认密码" prop="newmima2">
                  <el-input
                    v-model="passwordForm.newmima2"
                    placeholder="确认密码"
                    type="password"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <div class="formModel_btn_box">
              <el-button type="primary" @click="updatePassword"
                >修改密码</el-button
              >
            </div>
          </el-form>
        </div>
      </div>

      <!-- 档案管理 -->
      <div class="content-section" v-if="tabIndex == 'profile'">
        <div class="card">
          <h3 class="card-title">档案管理</h3>
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
                    v-model="profileForm.height"
                    placeholder="身高"
                    type="number"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="体重(kg)" prop="weight">
                  <el-input
                    v-model="profileForm.weight"
                    placeholder="体重"
                    type="number"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="年龄" prop="age">
                  <el-input
                    v-model="profileForm.age"
                    placeholder="年龄"
                    type="number"
                  ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="性别" prop="gender">
                  <el-select
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
                    v-model="profileForm.dailyCalories"
                    placeholder="每日热量需求"
                    readonly
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <div class="formModel_btn_box">
              <el-button type="primary" @click="saveProfile"
                >保存档案</el-button
              >
            </div>
          </el-form>
        </div>
      </div>

      <!-- 健康管理 -->
      <div class="content-section" v-if="tabIndex == 'health'">
        <div class="health-tabs">
          <div
            class="health-tab-item"
            :class="healthTabIndex == 'overview' ? 'health-tab-active' : ''"
            @click="healthTabIndex = 'overview'"
          >
            健康概览
          </div>
          <div
            class="health-tab-item"
            :class="healthTabIndex == 'record' ? 'health-tab-active' : ''"
            @click="healthTabIndex = 'record'"
          >
            健康记录
          </div>
        </div>

        <!-- 健康概览 -->
        <div v-if="healthTabIndex == 'overview'" class="health-content">
          <div class="grid-container">
            <!-- 左侧：健康档案概览 + 目标设置 + 每日记录 -->
            <div class="grid-item left-card">
              <div class="card">
                <h3 class="card-title">健康档案概览</h3>
                <div class="health-stats">
                  <div class="stat-item">
                    <span class="stat-label">当前 BMI</span>
                    <span class="stat-value main-color">{{ currentBMI }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">当前体重</span>
                    <span class="stat-value main-color"
                      >{{ currentWeight }} kg</span
                    >
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">目标 BMI</span>
                    <span class="stat-value">{{ targetBMI }}</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">目标体重</span>
                    <span class="stat-value">{{ targetWeight }} kg</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">差距</span>
                    <span class="stat-value">{{ weightDifference }} kg</span>
                  </div>
                  <div class="stat-item">
                    <span class="stat-label">健康状态</span>
                    <span class="stat-value">{{ healthStatus }}</span>
                  </div>
                </div>
                <div class="progress-section">
                  <span class="progress-label">目标达成进度</span>
                  <el-progress
                    :percentage="progressPercentage"
                    :stroke-width="10"
                    :color="'#03CC88'"
                  />
                </div>

                <div class="goal-setting">
                  <h4>目标设置</h4>
                  <el-form :model="goalForm" label-width="100px">
                    <el-row>
                      <el-col :span="12">
                        <el-form-item label="目标体重(kg)">
                          <el-input
                            v-model="goalForm.targetWeight"
                            type="number"
                            placeholder="请输入目标体重"
                          />
                        </el-form-item>
                      </el-col>
                      <el-col :span="12">
                        <el-form-item label="目标 BMI">
                          <el-input
                            v-model="goalForm.targetBMI"
                            type="number"
                            step="0.1"
                            placeholder="请输入目标BMI"
                          />
                        </el-form-item>
                      </el-col>
                    </el-row>
                    <el-button type="primary" @click="saveGoal"
                      >保存目标</el-button
                    >
                  </el-form>
                </div>

                <div class="daily-record-section">
                  <h4>每日记录</h4>
                  <el-form :model="dailyRecordForm" label-width="100px">
                    <el-form-item label="日期">
                      <el-date-picker
                        v-model="dailyRecordForm.recordDate"
                        type="date"
                        placeholder="选择日期"
                        style="width: 100%"
                      />
                    </el-form-item>
                    <el-form-item label="体重(kg)">
                      <el-input
                        v-model="dailyRecordForm.weight"
                        type="number"
                        placeholder="请输入体重"
                        step="0.1"
                      />
                    </el-form-item>
                    <el-button type="primary" @click="saveDailyRecord"
                      >保存记录</el-button
                    >
                  </el-form>
                </div>
              </div>
            </div>

            <!-- 右侧：数据可视化 + 历史记录 -->
            <div class="grid-item right-cards">
              <!-- 数据可视化卡片 -->
              <div class="card mb-24">
                <div class="card-header">
                  <h3 class="card-title">数据可视化</h3>
                  <div class="chart-controls">
                    <el-button
                      v-for="period in chartPeriods"
                      :key="period.value"
                      :type="
                        selectedPeriod == period.value ? 'primary' : 'default'
                      "
                      size="small"
                      @click="selectedPeriod = period.value"
                    >
                      {{ period.label }}
                    </el-button>
                    <el-button type="danger" size="small" @click="deleteRecords"
                      >删除所有记录</el-button
                    >
                  </div>
                </div>
                <div ref="chartRef" class="chart-container"></div>
                <div v-if="showSuccessMessage" class="success-message">
                  记录添加成功！
                </div>
              </div>

              <!-- 历史记录列表卡片 -->
              <div class="card">
                <h3 class="card-title">历史记录</h3>
                <el-table :data="healthRecords" style="width: 100%">
                  <el-table-column prop="recordDate" label="日期" width="120" />
                  <el-table-column prop="weight" label="体重(kg)" width="100" />
                  <el-table-column prop="bmi" label="BMI" width="100" />
                  <el-table-column prop="createTime" label="记录时间" />
                  <el-table-column label="操作" width="150">
                    <template #default="scope">
                      <el-button
                        type="warning"
                        size="small"
                        @click="editRecord(scope.row)"
                      >
                        编辑
                      </el-button>
                      <el-button
                        type="danger"
                        size="small"
                        @click="deleteRecord(scope.row.id)"
                      >
                        删除
                      </el-button>
                    </template>
                  </el-table-column>
                </el-table>
                <div class="pagination">
                  <el-pagination
                    background
                    layout="prev, pager, next"
                    :total="healthRecordsTotal"
                    :page-size="10"
                    v-model:current-page="currentPage"
                    @current-change="handlePageChange"
                  />
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 健康记录 -->
        <div v-if="healthTabIndex == 'record'" class="health-content">
          <div class="card">
            <h3 class="card-title">健康记录管理</h3>
            <!-- 这里可以添加更多健康记录相关的功能 -->
          </div>
        </div>
      </div>

      <!-- 我的收藏 -->
      <div class="content-section" v-if="tabIndex == 'storeup'">
        <div class="card">
          <h3 class="card-title">我的收藏</h3>
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
import * as echarts from "echarts";
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
                "error",
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
const healthTabIndex = ref("overview");
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
  if (item.tableName == "health") {
    tabIndex.value = "health";
    healthTabIndex.value = "overview";
    initHealthData();
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
    }`,
  );
};

// 健康管理相关变量
const currentBMI = ref(24.5);
const currentWeight = ref(70);
const targetBMI = ref(22);
const targetWeight = ref(65);
const weightDifference = ref(-5);
const healthStatus = ref("正常");
const progressPercentage = ref(50);

// 目标设置表单
const goalForm = ref({
  targetWeight: 65,
  targetBMI: 22,
});

// 每日记录表单
const dailyRecordForm = ref({
  recordDate: new Date(),
  weight: "",
});

// 健康记录列表
const healthRecords = ref([]);
const healthRecordsTotal = ref(0);
const currentPage = ref(1);

// 图表相关
const chartRef = ref(null);
const chartInstance = ref(null);
const selectedPeriod = ref(7);
const chartPeriods = [
  { label: "7天", value: 7 },
  { label: "30天", value: 30 },
  { label: "90天", value: 90 },
  { label: "自定义", value: 0 },
];

// 提示信息
const showSuccessMessage = ref(false);

// 初始化健康数据
const initHealthData = () => {
  // 从档案管理获取数据
  if (profileForm.value.height && profileForm.value.weight) {
    // 计算BMI
    const height = profileForm.value.height / 100; // 转换为米
    const bmi = (profileForm.value.weight / (height * height)).toFixed(1);

    // 更新健康数据
    currentWeight.value = parseFloat(profileForm.value.weight);
    currentBMI.value = parseFloat(bmi);

    // 从数据库获取目标数据
    if (context?.$http) {
      context
        .$http({
          url: `user-profile/get-goals`,
          method: "get",
          params: { userId: userid.value },
        })
        .then((res) => {
          console.log("获取目标数据响应:", res);
          if (res && res.data && res.data.code === 0) {
            const goals = res.data.data;
            if (goals) {
              if (goals.targetWeight) {
                targetWeight.value = parseFloat(goals.targetWeight);
                goalForm.value.targetWeight = parseFloat(goals.targetWeight);
              }
              if (goals.targetBMI) {
                targetBMI.value = parseFloat(goals.targetBMI);
                goalForm.value.targetBMI = parseFloat(goals.targetBMI);
              }
            }

            // 计算差距和进度
            weightDifference.value = currentWeight.value - targetWeight.value;

            // 重新计算进度，使用当前体重作为初始体重
            const initialWeight = currentWeight.value;
            const totalDiff = initialWeight - targetWeight.value;
            const currentDiff = currentWeight.value - targetWeight.value;

            if (totalDiff > 0) {
              // 减重目标
              if (currentWeight.value <= targetWeight.value) {
                progressPercentage.value = 100;
              } else {
                progressPercentage.value = Math.round(
                  ((totalDiff - currentDiff) / totalDiff) * 100,
                );
              }
            } else if (totalDiff < 0) {
              // 增重目标
              if (currentWeight.value >= targetWeight.value) {
                progressPercentage.value = 100;
              } else {
                // 计算增重进度：(当前体重 - 初始体重) / (目标体重 - 初始体重) * 100
                const progress =
                  ((currentWeight.value - initialWeight) /
                    (targetWeight.value - initialWeight)) *
                  100;
                progressPercentage.value = Math.round(
                  Math.max(0, Math.min(100, progress)),
                );
              }
            } else {
              progressPercentage.value = 100;
            }

            // 修复浮点数精度问题
            weightDifference.value = parseFloat(
              weightDifference.value.toFixed(1),
            );

            // 更新健康状态
            updateHealthStatus();
          } else {
            // API调用失败，使用默认目标数据
            console.log("获取目标数据失败，使用默认数据");
            targetWeight.value = 65;
            targetBMI.value = 22;
            goalForm.value.targetWeight = 65;
            goalForm.value.targetBMI = 22;

            // 计算差距和进度
            weightDifference.value = currentWeight.value - targetWeight.value;

            // 重新计算进度，使用当前体重作为初始体重
            const initialWeight = currentWeight.value;
            const totalDiff = initialWeight - targetWeight.value;
            const currentDiff = currentWeight.value - targetWeight.value;

            if (totalDiff > 0) {
              // 减重目标
              if (currentWeight.value <= targetWeight.value) {
                progressPercentage.value = 100;
              } else {
                progressPercentage.value = Math.round(
                  ((totalDiff - currentDiff) / totalDiff) * 100,
                );
              }
            } else if (totalDiff < 0) {
              // 增重目标
              if (currentWeight.value >= targetWeight.value) {
                progressPercentage.value = 100;
              } else {
                // 计算增重进度：(当前体重 - 初始体重) / (目标体重 - 初始体重) * 100
                const progress =
                  ((currentWeight.value - initialWeight) /
                    (targetWeight.value - initialWeight)) *
                  100;
                progressPercentage.value = Math.round(
                  Math.max(0, Math.min(100, progress)),
                );
              }
            } else {
              progressPercentage.value = 100;
            }

            // 修复浮点数精度问题
            weightDifference.value = parseFloat(
              weightDifference.value.toFixed(1),
            );

            // 更新健康状态
            updateHealthStatus();
          }
        })
        .catch((error) => {
          console.error("获取目标数据失败:", error);
          // API调用失败，使用默认目标数据
          targetWeight.value = 65;
          targetBMI.value = 22;
          goalForm.value.targetWeight = 65;
          goalForm.value.targetBMI = 22;

          // 计算差距和进度
          weightDifference.value = currentWeight.value - targetWeight.value;

          // 重新计算进度，使用当前体重作为初始体重
          const initialWeight = currentWeight.value;
          const totalDiff = initialWeight - targetWeight.value;
          const currentDiff = currentWeight.value - targetWeight.value;

          if (totalDiff > 0) {
            // 减重目标
            if (currentWeight.value <= targetWeight.value) {
              progressPercentage.value = 100;
            } else {
              progressPercentage.value = Math.round(
                ((totalDiff - currentDiff) / totalDiff) * 100,
              );
            }
          } else if (totalDiff < 0) {
            // 增重目标
            if (currentWeight.value >= targetWeight.value) {
              progressPercentage.value = 100;
            } else {
              // 计算增重进度：(当前体重 - 初始体重) / (目标体重 - 初始体重) * 100
              const progress =
                ((currentWeight.value - initialWeight) /
                  (targetWeight.value - initialWeight)) *
                100;
              progressPercentage.value = Math.round(
                Math.max(0, Math.min(100, progress)),
              );
            }
          } else {
            progressPercentage.value = 100;
          }

          // 修复浮点数精度问题
          weightDifference.value = parseFloat(
            weightDifference.value.toFixed(1),
          );

          // 更新健康状态
          updateHealthStatus();
        });
    } else {
      // context.$http 未定义，使用默认目标数据
      console.log("context.$http 未定义，使用默认目标数据");
      targetWeight.value = 65;
      targetBMI.value = 22;
      goalForm.value.targetWeight = 65;
      goalForm.value.targetBMI = 22;

      // 计算差距和进度
      weightDifference.value = currentWeight.value - targetWeight.value;

      // 重新计算进度，使用当前体重作为初始体重
      const initialWeight = currentWeight.value;
      const totalDiff = initialWeight - targetWeight.value;
      const currentDiff = currentWeight.value - targetWeight.value;

      if (totalDiff > 0) {
        // 减重目标
        if (currentWeight.value <= targetWeight.value) {
          progressPercentage.value = 100;
        } else {
          progressPercentage.value = Math.round(
            ((totalDiff - currentDiff) / totalDiff) * 100,
          );
        }
      } else if (totalDiff < 0) {
        // 增重目标
        if (currentWeight.value >= targetWeight.value) {
          progressPercentage.value = 100;
        } else {
          // 计算增重进度：(当前体重 - 初始体重) / (目标体重 - 初始体重) * 100
          const progress =
            ((currentWeight.value - initialWeight) /
              (targetWeight.value - initialWeight)) *
            100;
          progressPercentage.value = Math.round(
            Math.max(0, Math.min(100, progress)),
          );
        }
      } else {
        progressPercentage.value = 100;
      }

      // 修复浮点数精度问题
      weightDifference.value = parseFloat(weightDifference.value.toFixed(1));

      // 更新健康状态
      updateHealthStatus();
    }
  }

  // 从数据库获取健康记录
  console.log(
    "获取健康记录，用户ID:",
    userid.value,
    "类型:",
    typeof userid.value,
  );
  if (context?.$http) {
    context
      .$http({
        url: `user-daily-record/list`,
        method: "get",
        params: { userId: Number(userid.value) },
      })
      .then((res) => {
        console.log("获取健康记录响应:", res);
        if (res && res.data && res.data.code === 0) {
          healthRecords.value = res.data.data || [];
          healthRecordsTotal.value = healthRecords.value.length;
          console.log("健康记录数据:", healthRecords.value);
        } else {
          // API调用失败，使用模拟数据
          console.log("获取健康记录失败，使用模拟数据");
          healthRecords.value = [
            {
              id: 1,
              recordDate: "2026-01-20",
              weight: 70.5,
              bmi: 23.1,
              createTime: "2026-01-20 10:00:00",
            },
            {
              id: 2,
              recordDate: "2026-01-28",
              weight: 70.0,
              bmi: 22.9,
              createTime: "2026-01-28 10:00:00",
            },
            {
              id: 3,
              recordDate: "2026-02-05",
              weight: 69.5,
              bmi: 22.7,
              createTime: "2026-02-05 10:00:00",
            },
            {
              id: 4,
              recordDate: "2026-02-15",
              weight: 69.0,
              bmi: 22.5,
              createTime: "2026-02-15 10:00:00",
            },
            {
              id: 5,
              recordDate: "2026-02-25",
              weight: 68.5,
              bmi: 22.3,
              createTime: "2026-02-25 10:00:00",
            },
            {
              id: 6,
              recordDate: "2026-03-05",
              weight: 68.0,
              bmi: 22.1,
              createTime: "2026-03-05 10:00:00",
            },
            {
              id: 7,
              recordDate: "2026-03-15",
              weight: 67.5,
              bmi: 21.9,
              createTime: "2026-03-15 10:00:00",
            },
          ];
          healthRecordsTotal.value = healthRecords.value.length;
        }
        // 初始化图表
        setTimeout(() => {
          initChart();
        }, 100);
      })
      .catch((error) => {
        console.error("获取健康记录失败:", error);
        // API调用失败，使用模拟数据
        healthRecords.value = [
          {
            id: 1,
            recordDate: "2026-01-20",
            weight: 70.5,
            bmi: 23.1,
            createTime: "2026-01-20 10:00:00",
          },
          {
            id: 2,
            recordDate: "2026-01-28",
            weight: 70.0,
            bmi: 22.9,
            createTime: "2026-01-28 10:00:00",
          },
          {
            id: 3,
            recordDate: "2026-02-05",
            weight: 69.5,
            bmi: 22.7,
            createTime: "2026-02-05 10:00:00",
          },
          {
            id: 4,
            recordDate: "2026-02-15",
            weight: 69.0,
            bmi: 22.5,
            createTime: "2026-02-15 10:00:00",
          },
          {
            id: 5,
            recordDate: "2026-02-25",
            weight: 68.5,
            bmi: 22.3,
            createTime: "2026-02-25 10:00:00",
          },
          {
            id: 6,
            recordDate: "2026-03-05",
            weight: 68.0,
            bmi: 22.1,
            createTime: "2026-03-05 10:00:00",
          },
          {
            id: 7,
            recordDate: "2026-03-15",
            weight: 67.5,
            bmi: 21.9,
            createTime: "2026-03-15 10:00:00",
          },
        ];
        healthRecordsTotal.value = healthRecords.value.length;
        // 初始化图表
        setTimeout(() => {
          initChart();
        }, 100);
      });
  } else {
    // context.$http 未定义，使用模拟数据
    console.log("context.$http 未定义，使用模拟数据");
    healthRecords.value = [
      {
        id: 1,
        recordDate: "2026-01-20",
        weight: 70.5,
        bmi: 23.1,
        createTime: "2026-01-20 10:00:00",
      },
      {
        id: 2,
        recordDate: "2026-01-28",
        weight: 70.0,
        bmi: 22.9,
        createTime: "2026-01-28 10:00:00",
      },
      {
        id: 3,
        recordDate: "2026-02-05",
        weight: 69.5,
        bmi: 22.7,
        createTime: "2026-02-05 10:00:00",
      },
      {
        id: 4,
        recordDate: "2026-02-15",
        weight: 69.0,
        bmi: 22.5,
        createTime: "2026-02-15 10:00:00",
      },
      {
        id: 5,
        recordDate: "2026-02-25",
        weight: 68.5,
        bmi: 22.3,
        createTime: "2026-02-25 10:00:00",
      },
      {
        id: 6,
        recordDate: "2026-03-05",
        weight: 68.0,
        bmi: 22.1,
        createTime: "2026-03-05 10:00:00",
      },
      {
        id: 7,
        recordDate: "2026-03-15",
        weight: 67.5,
        bmi: 21.9,
        createTime: "2026-03-15 10:00:00",
      },
    ];
    healthRecordsTotal.value = healthRecords.value.length;
    // 初始化图表
    setTimeout(() => {
      initChart();
    }, 100);
  }
};

// 更新健康状态
const updateHealthStatus = () => {
  if (currentBMI.value < 18.5) {
    healthStatus.value = "偏瘦";
  } else if (currentBMI.value < 24) {
    healthStatus.value = "正常";
  } else if (currentBMI.value < 28) {
    healthStatus.value = "超重";
  } else {
    healthStatus.value = "肥胖";
  }
};

// 初始化ECharts图表
const initChart = () => {
  if (chartRef.value) {
    // 从健康记录中获取数据
    const sortedRecords = [...healthRecords.value].sort((a, b) => {
      return new Date(a.recordDate) - new Date(b.recordDate);
    });

    // 根据选择的时间范围过滤数据
    let filteredRecords = [];
    const now = new Date();

    console.log("当前时间:", now);
    console.log("选择的时间范围:", selectedPeriod.value);
    console.log("所有记录:", sortedRecords);

    // 由于是模拟数据，我们直接使用所有记录，不做时间过滤
    // 这样可以确保图表显示所有数据
    filteredRecords = sortedRecords;
    console.log("使用所有记录:", filteredRecords);

    // 准备图表数据
    const dates = filteredRecords.map((record) => {
      const date = new Date(record.recordDate);
      return date.getDate().toString();
    });
    const weights = filteredRecords.map((record) => record.weight);
    const bmis = filteredRecords.map((record) => record.bmi);

    // 创建目标体重数据数组
    const targetWeights = Array(filteredRecords.length).fill(
      targetWeight.value,
    );

    // 销毁旧图表
    if (chartInstance.value) {
      chartInstance.value.dispose();
    }

    // 创建新图表
    chartInstance.value = echarts.init(chartRef.value);

    const option = {
      tooltip: {
        trigger: "axis",
      },
      legend: {
        data: ["体重", "BMI", "目标体重"],
      },
      grid: {
        left: "3%",
        right: "4%",
        bottom: "3%",
        containLabel: true,
      },
      xAxis: {
        type: "category",
        boundaryGap: false,
        data: dates.length > 0 ? dates : [""],
      },
      yAxis: [
        {
          type: "value",
          name: "体重(kg)",
          position: "left",
        },
        {
          type: "value",
          name: "BMI",
          position: "right",
        },
      ],
      series: [
        {
          name: "体重",
          type: "line",
          data: weights.length > 0 ? weights : [0],
          itemStyle: {
            color: "#03CC88",
          },
        },
        {
          name: "BMI",
          type: "line",
          yAxisIndex: 1,
          data: bmis.length > 0 ? bmis : [0],
          itemStyle: {
            color: "#ee6723",
          },
        },
        {
          name: "目标体重",
          type: "line",
          data: targetWeights.length > 0 ? targetWeights : [0],
          lineStyle: {
            type: "dashed",
            color: "#999",
          },
          symbol: "none",
        },
      ],
    };

    chartInstance.value.setOption(option);

    // 响应式调整
    window.addEventListener("resize", () => {
      chartInstance.value.resize();
    });
  }
};

// 监听时间范围变化
watch(selectedPeriod, () => {
  initChart();
});

// 保存目标
const saveGoal = () => {
  // 这里应该调用API保存目标

  // 实现目标BMI和目标体重的关联
  if (profileForm.value.height) {
    const height = profileForm.value.height / 100; // 转换为米

    // 优先使用用户输入的目标体重，计算目标BMI
    if (goalForm.value.targetWeight) {
      const targetWeightNum = parseFloat(goalForm.value.targetWeight);
      const bmi = (targetWeightNum / (height * height)).toFixed(1);
      goalForm.value.targetBMI = parseFloat(bmi);
      targetBMI.value = parseFloat(bmi);
    } else if (goalForm.value.targetBMI) {
      // 只有当用户没有输入目标体重时，才根据目标BMI计算目标体重
      const targetBMINum = parseFloat(goalForm.value.targetBMI);
      const weight = (targetBMINum * height * height).toFixed(1);
      goalForm.value.targetWeight = parseFloat(weight);
      targetWeight.value = parseFloat(weight);
    }
  }

  weightDifference.value = currentWeight.value - targetWeight.value;

  // 重新计算进度，使用当前体重作为初始体重
  const initialWeight = currentWeight.value;
  const totalDiff = initialWeight - targetWeight.value;
  const currentDiff = currentWeight.value - targetWeight.value;

  if (totalDiff > 0) {
    // 减重目标
    if (currentWeight.value <= targetWeight.value) {
      progressPercentage.value = 100;
    } else {
      progressPercentage.value = Math.round(
        ((totalDiff - currentDiff) / totalDiff) * 100,
      );
    }
  } else if (totalDiff < 0) {
    // 增重目标
    if (currentWeight.value >= targetWeight.value) {
      progressPercentage.value = 100;
    } else {
      // 计算增重进度：(当前体重 - 初始体重) / (目标体重 - 初始体重) * 100
      const progress =
        ((currentWeight.value - initialWeight) /
          (targetWeight.value - initialWeight)) *
        100;
      progressPercentage.value = Math.round(
        Math.max(0, Math.min(100, progress)),
      );
    }
  } else {
    progressPercentage.value = 100;
  }

  // 修复浮点数精度问题
  goalForm.value.targetWeight = parseFloat(
    parseFloat(goalForm.value.targetWeight).toFixed(1),
  );
  goalForm.value.targetBMI = parseFloat(
    parseFloat(goalForm.value.targetBMI).toFixed(1),
  );
  targetWeight.value = parseFloat(targetWeight.value.toFixed(1));
  targetBMI.value = parseFloat(targetBMI.value.toFixed(1));
  weightDifference.value = parseFloat(weightDifference.value.toFixed(1));

  // 保存目标到数据库
  if (context?.$http) {
    const goalData = {
      userId: userid.value,
      targetWeight: targetWeight.value,
      targetBMI: targetBMI.value,
    };

    context
      .$http({
        url: `user-profile/update-goals`,
        method: "post",
        data: goalData,
      })
      .then((res) => {
        if (res && res.data && res.data.code === 0) {
          console.log("目标保存到数据库成功");
        }
      })
      .catch((error) => {
        console.error("保存目标到数据库失败:", error);
      });
  }

  context?.$toolUtil.message("目标保存成功", "success");
};

// 保存每日记录
const saveDailyRecord = () => {
  if (!dailyRecordForm.value.weight) {
    context?.$toolUtil.message("请输入体重", "error");
    return;
  }

  // 计算BMI
  const height = profileForm.value.height / 100; // 转换为米
  const bmi = (dailyRecordForm.value.weight / (height * height)).toFixed(1);

  // 调用API保存记录
  const recordData = {
    userId: userid.value,
    recordDate: moment(dailyRecordForm.value.recordDate).format("YYYY-MM-DD"),
    weight: parseFloat(dailyRecordForm.value.weight),
    bmi: parseFloat(bmi),
  };

  // 创建新记录对象用于更新UI
  const newRecord = {
    id: Date.now(),
    ...recordData,
    createTime: moment().format("YYYY-MM-DD HH:mm:ss"),
  };

  if (context?.$http) {
    context
      .$http({
        url: `user-daily-record/save`,
        method: "post",
        data: recordData,
      })
      .then((res) => {
        if (res && res.data && res.data.code === 0) {
          // 保存成功后重新获取记录
          context
            .$http({
              url: `user-daily-record/list`,
              method: "get",
              params: { userId: userid.value },
            })
            .then((res) => {
              if (res && res.data && res.data.code === 0) {
                healthRecords.value = res.data.data || [];
                healthRecordsTotal.value = healthRecords.value.length;
                // 更新图表
                initChart();
              }
            });
        }
      })
      .catch((error) => {
        console.error("保存健康记录失败:", error);
      });
  }

  // 更新当前体重和BMI
  currentWeight.value = newRecord.weight;
  currentBMI.value = newRecord.bmi;
  weightDifference.value = currentWeight.value - targetWeight.value;

  // 重新计算进度，使用当前体重作为初始体重
  const initialWeight = currentWeight.value;
  const totalDiff = initialWeight - targetWeight.value;
  const currentDiff = currentWeight.value - targetWeight.value;

  if (totalDiff > 0) {
    // 减重目标
    if (currentWeight.value <= targetWeight.value) {
      progressPercentage.value = 100;
    } else {
      progressPercentage.value = Math.round(
        ((totalDiff - currentDiff) / totalDiff) * 100,
      );
    }
  } else if (totalDiff < 0) {
    // 增重目标
    if (currentWeight.value >= targetWeight.value) {
      progressPercentage.value = 100;
    } else {
      // 计算增重进度：(当前体重 - 初始体重) / (目标体重 - 初始体重) * 100
      const progress =
        ((currentWeight.value - initialWeight) /
          (targetWeight.value - initialWeight)) *
        100;
      progressPercentage.value = Math.round(
        Math.max(0, Math.min(100, progress)),
      );
    }
  } else {
    progressPercentage.value = 100;
  }

  // 更新图表
  initChart();

  // 显示成功提示
  showSuccessMessage.value = true;
  setTimeout(() => {
    showSuccessMessage.value = false;
  }, 3000);

  // 重置表单
  dailyRecordForm.value = {
    recordDate: new Date(),
    weight: "",
  };

  context?.$toolUtil.message("记录保存成功", "success");
};

// 编辑记录
const editRecord = (row) => {
  // 这里应该实现编辑功能
  context?.$toolUtil.message("编辑功能开发中", "info");
};

// 删除记录
const deleteRecord = (id) => {
  context
    .$confirm("确定要删除该记录吗？", "提示")
    .then(() => {
      // 这里应该调用API删除记录
      healthRecords.value = healthRecords.value.filter(
        (item) => item.id !== id,
      );
      healthRecordsTotal.value = healthRecords.value.length;

      // 更新本地存储
      localStorage.setItem(
        `healthRecords_${userid.value}`,
        JSON.stringify(healthRecords.value),
      );

      context?.$toolUtil.message("删除成功", "success");
    })
    .catch(() => {});
};

// 删除选中记录
const deleteRecords = () => {
  context
    .$confirm("确定要删除所有健康记录吗？", "提示")
    .then(() => {
      // 清空所有健康记录
      healthRecords.value = [];
      healthRecordsTotal.value = 0;

      // 更新本地存储
      localStorage.setItem(
        `healthRecords_${userid.value}`,
        JSON.stringify(healthRecords.value),
      );

      // 更新图表
      initChart();

      context?.$toolUtil.message("删除成功", "success");
    })
    .catch(() => {});
};

// 分页处理
const handlePageChange = (page) => {
  currentPage.value = page;
  // 这里应该调用API获取对应页的数据
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
  { immediate: true },
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
          "",
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
.top-nav {
  display: flex;
  background: #fff;
  border-bottom: 1px solid #eaeaea;
  padding: 0 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);

  .nav-item {
    padding: 16px 24px;
    cursor: pointer;
    position: relative;
    font-size: 15px;
    color: #666;
    transition: all 0.3s ease;
    border-bottom: 3px solid transparent;

    &:hover {
      color: #03cc88;
      background: rgba(3, 204, 136, 0.05);
    }

    &.nav-item-active {
      color: #03cc88;
      border-bottom-color: #03cc88;
      font-weight: 500;
    }

    .nav-item-dropdown {
      position: absolute;
      top: 100%;
      left: 0;
      background: #fff;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
      border-radius: 4px;
      z-index: 1000;
      min-width: 180px;

      .nav-item-dropdown-item {
        padding: 10px 16px;
        cursor: pointer;
        transition: background 0.2s ease;

        &:hover {
          background: rgba(3, 204, 136, 0.05);
          color: #03cc88;
        }
      }
    }
  }
}

.content-area {
  flex: 1;
  padding: 0 20px 20px;
}

.content-section {
  width: 100%;
}

.card {
  background: #ffffff;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  padding: 24px;
  margin-bottom: 24px;
  transition: box-shadow 0.3s ease;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  }

  .card-title {
    font-size: 18px;
    font-weight: 500;
    color: #333;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f0f0f0;
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 1px solid #f0f0f0;

    .card-title {
      margin: 0;
      padding: 0;
      border: none;
    }
  }
}

.formModel_btn_box {
  display: flex;
  align-items: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;

  .el-button {
    margin-right: 12px;
    border-radius: 6px;

    &:hover {
      box-shadow: 0 2px 8px rgba(3, 204, 136, 0.2);
    }
  }
}

.usersForm {
  .el-form-item {
    margin-bottom: 20px;

    .el-input,
    .el-select,
    .el-date-picker {
      width: 100%;
      border-radius: 6px;

      .el-input__inner {
        border-radius: 6px;
        border: 1px solid #e4e7ed;

        &:focus {
          border-color: #03cc88;
          box-shadow: 0 0 0 2px rgba(3, 204, 136, 0.2);
        }
      }
    }
  }
}

/* 健康管理样式 */
.health-tabs {
  display: flex;
  margin-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;

  .health-tab-item {
    padding: 12px 24px;
    cursor: pointer;
    font-size: 15px;
    color: #666;
    border-bottom: 3px solid transparent;
    transition: all 0.3s ease;

    &:hover {
      color: #03cc88;
    }

    &.health-tab-active {
      color: #03cc88;
      border-bottom-color: #03cc88;
      font-weight: 500;
    }
  }
}

.grid-container {
  display: grid;
  grid-template-columns: 1fr 2fr;
  grid-gap: 20px;

  .grid-item {
    &.left-card {
      /* 左侧卡片 */
    }

    &.right-cards {
      display: flex;
      flex-direction: column;

      .mb-24 {
        margin-bottom: 24px;
      }
    }
  }
}

.daily-record-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;

  h4 {
    font-size: 16px;
    font-weight: 500;
    color: #333;
    margin-bottom: 16px;
  }

  .el-button {
    margin-top: 12px;
    border-radius: 6px;

    &:hover {
      box-shadow: 0 2px 8px rgba(3, 204, 136, 0.2);
    }
  }
}

.health-stats {
  display: grid;
  grid-template-columns: 1fr 1fr;
  grid-gap: 16px;
  margin-bottom: 24px;

  .stat-item {
    display: flex;
    flex-direction: column;

    .stat-label {
      font-size: 14px;
      color: #999;
      margin-bottom: 4px;
    }

    .stat-value {
      font-size: 18px;
      font-weight: 500;
      color: #333;

      &.main-color {
        color: #03cc88;
        font-size: 20px;
      }
    }
  }
}

.progress-section {
  margin-bottom: 24px;

  .progress-label {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
    display: block;
  }
}

.goal-setting {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;

  h4 {
    font-size: 16px;
    font-weight: 500;
    color: #333;
    margin-bottom: 16px;
  }

  .el-button {
    margin-top: 12px;
    border-radius: 6px;

    &:hover {
      box-shadow: 0 2px 8px rgba(3, 204, 136, 0.2);
    }
  }
}

.chart-controls {
  display: flex;
  gap: 8px;

  .el-button {
    border-radius: 4px;
  }
}

.chart-container {
  height: 300px;
  width: 100%;
}

.success-message {
  margin-top: 12px;
  padding: 8px 16px;
  background: rgba(3, 204, 136, 0.1);
  color: #03cc88;
  border-radius: 4px;
  font-size: 14px;
  text-align: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 收藏列表样式 */
.forum_list {
  width: 100%;

  .forum_item {
    width: 100%;
    cursor: pointer;
    display: flex;
    flex-wrap: wrap;
    margin: 0 0 16px;
    padding: 16px;
    background: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
    transition: box-shadow 0.3s ease;

    &:hover {
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .forum_item_left {
      display: flex;
      align-items: center;
      flex: 1;

      .forum_item_title {
        font-size: 16px;
        color: #333;
        font-weight: 500;
      }
    }

    .forum_item_btn_box {
      width: auto;

      .forum_del_btn {
        margin: 0 8px 0 0;
        padding: 0 16px;
        height: 32px;
        font-size: 14px;
        color: #fff;
        border-radius: 4px;
        border: 0;
        background: #ff4d4f;
        cursor: pointer;

        &:hover {
          background: #ff7875;
        }
      }
    }
  }

  .el-pagination {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
}
</style>
<style>
/**总盒子**/
.center_view {
  width: 1200px;
  margin: 20px auto;
  padding: 0px;
  background: #f8f9fa;
  font-size: 16px;
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/**标题**/
.center_view .section_title {
  width: 100%;
  margin: 0;
  padding: 0;
  z-index: 999;
  text-align: left;
  background: #03cc88;
  position: relative;
}

.center_view .section_title span {
  display: inline-block;
  font-size: 20px;
  color: #fff;
  font-weight: 500;
  padding: 15px 24px;
  min-width: 250px;
}

/**内容区**/
.center_view .content-area {
  flex: 1;
  padding: 0 24px 24px;
}

/* 全局样式 */
:root {
  --theme: #03cc88;
}

.main-color {
  color: #03cc88;
}

/* Element Plus 样式覆盖 */
.el-button--primary {
  background-color: #03cc88;
  border-color: #03cc88;

  &:hover,
  &:focus {
    background-color: #02b377;
    border-color: #02b377;
  }
}

.el-input__inner:focus {
  border-color: #03cc88;
  box-shadow: 0 0 0 2px rgba(3, 204, 136, 0.2);
}

.el-select .el-input.is-focus .el-input__inner {
  border-color: #03cc88;
  box-shadow: 0 0 0 2px rgba(3, 204, 136, 0.2);
}

.el-date-picker__input.el-input__inner:focus {
  border-color: #03cc88;
  box-shadow: 0 0 0 2px rgba(3, 204, 136, 0.2);
}

.el-table {
  border-radius: 8px;
  overflow: hidden;

  .el-table__header-wrapper th {
    background-color: #fafafa;
    font-weight: 500;
    color: #333;
  }

  .el-table__row:hover {
    background-color: #f5f7fa;
  }
}

.el-progress__bar-inner {
  background-color: #03cc88;
}
</style>
