<template>
  <div class="center_view" :style="{}">
    <!-- 顶部横向导航 -->
    <div class="section_title top-nav">
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
          :class="
            item.child[0].tableName == 'forum' &&
            item.child[0].menuJump == '14' &&
            tabIndex == 'myPosts'
              ? 'nav-item-active'
              : ''
          "
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
                  <div
                    style="
                      width: 200px;
                      height: 200px;
                      border: 1px solid #d9d9d9;
                      border-radius: 4px;
                      overflow: hidden;
                    "
                  >
                    <img
                      :src="userForm.touxiang"
                      style="width: 100%; height: 100%; object-fit: cover"
                      alt="用户头像"
                    />
                  </div>
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
        <!-- 健康概览 -->
        <div class="health-content">
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

                <div class="predict-section">
                  <span class="predict-label">目标达成预测</span>
                  <div class="predict-text">{{ predictText }}</div>
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
                  <el-table-column label="操作" width="80">
                    <template #default="scope">
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

      <!-- 我的发布 -->
      <div class="content-section" v-if="tabIndex == 'myPosts'">
        <div class="card">
          <h3 class="card-title">我的发布</h3>
          <div class="forum_list">
            <forum-item
              v-for="(item, index) in myPostsList"
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
              :layout="myPostsLayouts.join(',')"
              :total="myPostsTotal"
              :page-size="myPostsListQuery.limit"
              v-model:current-page="myPostsListQuery.page"
              prev-text="上一页"
              next-text="下一页"
              :hide-on-single-page="false"
              @size-change="myPostsSizeChange"
              @current-change="myPostsCurrentChange"
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
        @update="updateRecipe"
      />
    </div>
  </div>
</template>

<style scoped>
.predict-section {
  margin-top: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
  border-left: 4px solid #03cc88;
}

.predict-label {
  display: block;
  font-weight: 600;
  margin-bottom: 8px;
  color: #333;
}

.predict-text {
  font-size: 14px;
  line-height: 1.5;
  color: #666;
}

/* 现代弹窗样式 */
.modern-alert {
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  overflow: hidden;
}

.modern-alert .el-message-box__title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  padding: 20px 24px 0;
}

.modern-alert .el-message-box__content {
  font-size: 14px;
  line-height: 1.6;
  color: #666;
  padding: 20px 24px;
}

.modern-alert .el-message-box__btns {
  padding: 0 24px 20px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.modern-confirm-btn {
  background-color: #03cc88;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.modern-confirm-btn:hover {
  background-color: #02b378;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(3, 204, 136, 0.3);
}

.modern-cancel-btn {
  background-color: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.modern-cancel-btn:hover {
  background-color: #edf2fc;
  border-color: #dcdfe6;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>

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
  height: 170, // 设置默认身高为 170 厘米
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

// 我的发布相关变量
const myPostsList = ref([]);
const myPostsTotal = ref(0);
const myPostsPage = ref(1);
const myPostsLimit = ref(20);
const myPostsLoading = ref(false);
const myPostsListQuery = ref({
  page: 1,
  limit: 20,
  sort: "id",
  order: "desc",
});
const myPostsLayouts = ref([
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

//获取我的发布列表
const getMyPostsList = () => {
  console.log("开始获取我的发布列表");
  let params = JSON.parse(JSON.stringify(myPostsListQuery.value));
  console.log("请求参数:", params);

  if (!context || !context.$http) {
    console.error("context或$http未定义");
    return;
  }

  context
    .$http({
      url: `recipe/user/${userid.value}`,
      method: "get",
    })
    .then((res) => {
      console.log("我的发布列表响应:", res);
      if (res && res.data && res.data.recipes) {
        const myPostsItems = res.data.recipes || [];
        console.log("我的发布项目:", myPostsItems);

        // 处理我的发布列表数据
        myPostsList.value = myPostsItems.map((item) => {
          return {
            ...item,
            // 确保id字段正确
            id: item.id,
            // 确保userid字段是帖子作者的ID
            userid: item.userId,
            // 确保username字段存在
            username: item.username || item.nickname || "未知用户",
            // 确保totalCalories字段存在
            totalCalories: item.totalCalories || 0,
            // 确保nutritionTag字段存在
            nutritionTag: item.nutritionTag || "",
            // 确保addtime字段存在
            addtime: item.addtime || "",
            // 确保title字段存在
            title: item.title || "无标题",
            // 确保cover_image字段存在
            cover_image: item.coverImage || item.cover_image || "",
          };
        });
        myPostsTotal.value = myPostsItems.length || 0;
        console.log("处理后的我的发布列表:", myPostsList.value);
      } else {
        console.error("我的发布列表响应结构不正确");
        myPostsList.value = [];
        myPostsTotal.value = 0;
      }
    })
    .catch((error) => {
      console.error("获取我的发布列表失败:", error);
      myPostsList.value = [];
      myPostsTotal.value = 0;
    });
};

//获取收藏列表
const getStoreupList = () => {
  console.log("开始获取收藏列表");
  let params = JSON.parse(JSON.stringify(storeupListQuery.value));
  console.log("请求参数:", params);

  if (!context || !context.$http) {
    console.error("context或$http未定义");
    return;
  }

  context
    .$http({
      url: "storeup/page",
      method: "get",
      params: params,
    })
    .then((res) => {
      console.log("收藏列表响应:", res);
      if (res && res.data && res.data.data) {
        const storeupItems = res.data.data.list || [];
        console.log("收藏项目:", storeupItems);
        // 获取收藏的帖子详情
        const refIds = storeupItems
          .map((item) => item.refid)
          .filter((id) => id);
        console.log("收藏的帖子ID:", refIds);
        if (refIds.length > 0) {
          // 对每个收藏的帖子ID单独调用接口获取详情
          const recipePromises = refIds.map((id) => {
            return context.$http({
              url: `recipe/info/${id}`,
              method: "get",
            });
          });

          Promise.all(recipePromises)
            .then((recipeResArray) => {
              console.log("帖子详情响应数组:", recipeResArray);
              const recipeMap = {};
              recipeResArray.forEach((recipeRes, index) => {
                if (recipeRes && recipeRes.data && recipeRes.data.recipe) {
                  const recipe = recipeRes.data.recipe;
                  recipeMap[refIds[index]] = recipe;
                }
              });
              console.log("帖子详情映射:", recipeMap);

              // 合并收藏数据和帖子详情
              storeupList.value = storeupItems.map((item) => {
                const recipe = recipeMap[item.refid] || {};
                return {
                  ...item,
                  ...recipe,
                  // 确保id字段正确
                  id: item.refid,
                  // 确保userid字段是帖子作者的ID
                  userid: recipe.userId,
                  // 确保username字段存在
                  username: recipe.username || recipe.nickname || "未知用户",
                  // 确保totalCalories字段存在
                  totalCalories: recipe.totalCalories || 0,
                  // 确保nutritionTag字段存在
                  nutritionTag: recipe.nutritionTag || "",
                  // 确保addtime字段存在
                  addtime: item.addtime || recipe.addtime || "",
                  // 确保title字段存在
                  title: recipe.title || "无标题",
                  // 确保cover_image字段存在
                  cover_image: recipe.coverImage || recipe.cover_image || "",
                };
              });
              storeupTotal.value = Number(res.data.data.total) || 0;
              console.log("合并后的收藏列表:", storeupList.value);
            })
            .catch((error) => {
              console.error("获取帖子详情失败:", error);
              // 即使获取帖子详情失败，也显示收藏列表
              storeupList.value = storeupItems;
              storeupTotal.value = Number(res.data.data.total) || 0;
            });
        } else {
          storeupList.value = [];
          storeupTotal.value = 0;
          console.log("没有收藏的帖子");
        }
      } else {
        storeupList.value = [];
        storeupTotal.value = 0;
        console.error("收藏列表响应结构不正确");
      }
    })
    .catch((error) => {
      console.error("获取收藏列表失败:", error);
      storeupList.value = [];
      storeupTotal.value = 0;
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

//我的发布分页
const myPostsSizeChange = (size) => {
  myPostsListQuery.value.limit = size;
  getMyPostsList();
};

const myPostsCurrentChange = (page) => {
  myPostsListQuery.value.page = page;
  getMyPostsList();
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
  // 检查是否是帖子ID（从详情弹窗传递的是帖子ID）
  if (typeof item === "number") {
    context
      .$confirm("确定要删除这条帖子吗？", "提示")
      .then(() => {
        context
          .$http({
            url: `recipe/delete/${item}`,
            method: "delete",
            params: {
              userId: userid.value,
            },
          })
          .then((res) => {
            if (res.data.code === 0) {
              context.$message({
                type: "success",
                message: "删除成功！",
              });
              // 刷新列表
              if (tabIndex.value === "myPosts") {
                getMyPostsList();
              } else if (tabIndex.value === "storeup") {
                getStoreupList();
              }
              detailVisible.value = false;
            } else {
              context.$message.error(res.data.msg || "删除失败");
            }
          })
          .catch((error) => {
            console.error("删除失败:", error);
            context.$message.error("删除失败");
          });
      })
      .catch(() => {
        // 取消删除
      });
  } else if (item.id) {
    // 处理收藏的删除
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
    // 处理收藏的删除
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

// 更新帖子
const updateRecipe = (form) => {
  console.log("updateRecipe called with form:", form);
  if (!context || !context.$http) {
    console.error("context或$http未定义");
    return;
  }

  // 计算总热量
  let totalCalories = 0;
  form.ingredients.forEach((ingredient) => {
    totalCalories += ingredient.calories || 0;
  });
  form.totalCalories = totalCalories;
  form.id = detailForm.value.id;
  form.userId = detailForm.value.userId;
  form.username = detailForm.value.username;
  form.addtime = detailForm.value.addtime;

  context
    .$http({
      url: "recipe/update",
      method: "post",
      data: form,
    })
    .then((res) => {
      if (res.data.code === 0) {
        context.$message({
          type: "success",
          message: "更新成功！",
        });
        // 刷新列表
        if (tabIndex.value === "myPosts") {
          getMyPostsList();
        }
        // 更新详情弹窗中的数据
        detailForm.value = { ...detailForm.value, ...form };
      } else {
        context.$message.error(res.data.msg || "更新失败");
      }
    })
    .catch((error) => {
      console.error("更新失败:", error);
      context.$message.error("更新失败");
    });
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
  console.log("=== 开始获取个人中心用户信息 ===");
  const tableName = context?.$toolUtil.storageGet("frontSessionTable");
  console.log("数据来源表:", tableName);
  console.log("请求URL:", `${tableName}/session`);

  context
    ?.$http({
      url: `${tableName}/session`,
      method: "get",
    })
    .then((res) => {
      if (res?.data?.data) {
        context?.$toolUtil.storageSet("userid", res.data.data.id);
        context?.$toolUtil.storageSet(
          "frontName",
          res.data.data.yonghuzhanghao,
        );
        context?.$toolUtil.storageSet("headportrait", res.data.data.touxiang);
        userForm.value = res.data.data;
        // 更新store中的session状态，确保顶部头像同步更新
        store.dispatch("user/getSession");
        // 获取用户档案
        getProfile(res.data.data.id);
      }
    })
    .catch((error) => {
      console.error("获取个人中心用户信息失败:", error);
    });
};

// 获取用户档案
const getProfile = (userId) => {
  // 直接使用个人中心获取到的用户信息，避免调用user/profile/get接口
  if (userForm.value) {
    profileForm.value = {
      ...profileForm.value,
      ...userForm.value,
      height: userForm.value.height || 170, // 默认身高为 170 厘米
      weight: userForm.value.weight || "",
      age: userForm.value.age || "",
      dailyCalories: userForm.value.dailyCalories || "",
      dietaryRestrictions: userForm.value.dietaryRestrictions
        ? userForm.value.dietaryRestrictions.split(",")
        : [],
      fitnessGoal: userForm.value.fitnessGoal || "维持", // 确保健身目标不为空
      gender: userForm.value.xingbie || userForm.value.gender || "男", // 优先使用xingbie字段，保持与个人中心一致
      userId: userId,
    };
  } else {
    // 设置默认值
    profileForm.value = {
      height: 170, // 默认身高为 170 厘米
      weight: "",
      age: "",
      gender: "男",
      fitnessGoal: "维持",
      dietaryRestrictions: "无",
      dailyCalories: "",
      userId: userId,
    };
  }
};

// 计算每日热量需求
const calculateDailyCalories = (height, weight, age, gender, fitnessGoal) => {
  console.log("=== 开始计算每日热量需求 ===");
  console.log("身高:", height);
  console.log("体重:", weight);
  console.log("年龄:", age);
  console.log("性别:", gender);
  console.log("健身目标:", fitnessGoal);

  // 使用哈里斯-本尼迪克特方程计算基础代谢率(BMR)
  let bmr;
  if (gender === "男") {
    bmr = 88.362 + 13.397 * weight + 4.799 * height - 5.677 * age;
  } else {
    bmr = 447.593 + 9.247 * weight + 3.098 * height - 4.33 * age;
  }
  console.log("基础代谢率(BMR):", bmr);

  // 根据健身目标调整活动系数
  let activityFactor;
  switch (fitnessGoal) {
    case "增肌":
      activityFactor = 1.8; // 高强度运动
      break;
    case "减脂":
      activityFactor = 1.5; // 中等强度运动
      break;
    case "维持":
      activityFactor = 1.375; // 轻度运动
      break;
    default:
      activityFactor = 1.375;
  }
  console.log("活动系数:", activityFactor);

  // 计算每日热量需求
  let dailyCalories = Math.round(bmr * activityFactor);
  console.log("每日热量需求:", dailyCalories);
  console.log("=== 计算每日热量需求结束 ===");

  return dailyCalories;
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
          fitnessGoal: profileForm.value.fitnessGoal || "维持", // 确保健身目标不为空
          xingbie: profileForm.value.gender, // 保存性别到xingbie字段，保持与个人中心一致
        };

        // 计算每日热量需求
        formData.dailyCalories = calculateDailyCalories(
          formData.height,
          formData.weight,
          formData.age,
          formData.xingbie,
          formData.fitnessGoal,
        );

        // 将dietaryRestrictions数组转换为逗号分隔的字符串
        if (Array.isArray(formData.dietaryRestrictions)) {
          formData.dietaryRestrictions = formData.dietaryRestrictions.join(",");
        }

        console.log("保存档案请求数据:", formData);

        // 使用与个人中心相同的接口保存数据
        const tableName = context?.$toolUtil.storageGet("frontSessionTable");
        console.log("保存档案使用的表:", tableName);
        console.log("保存档案请求URL:", `${tableName}/update`);

        context
          .$http({
            url: `${tableName}/update`,
            method: "post",
            data: formData,
          })
          .then((res) => {
            console.log("保存档案响应:", res);
            if (res?.data?.code == 0) {
              context?.$toolUtil?.message("保存成功", "success");
              // 更新个人中心的用户信息
              if (userForm.value) {
                userForm.value = {
                  ...userForm.value,
                  ...formData,
                };
                console.log("更新个人中心用户信息:", userForm.value);
              }
              // 刷新个人中心的session信息
              getSession();
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
    initHealthData();
    return;
  }
  if (item.tableName == "examrecord" && item.menuJump == "22") {
    router.push(`/index/examfailrecord?centerType=1`);
    return;
  }
  if (item.tableName == "forum" && item.menuJump == "14") {
    tabIndex.value = "myPosts";
    getMyPostsList();
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
const initialWeight = ref(0);

// 预测文本
const predictText = ref("");

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
const selectedPeriod = ref("7");
const chartPeriods = [
  { label: "7天", value: "7" },
  { label: "30天", value: "30" },
  { label: "90天", value: "90" },
  { label: "自定义", value: "custom" },
];

// 提示信息
const showSuccessMessage = ref(false);

// 初始化健康数据
const initHealthData = () => {
  // 从档案管理获取数据
  if (profileForm.value.height && profileForm.value.weight) {
    // 计算BMI
    const height = profileForm.value.height / 100; // 转换为米
    const bmi = (profileForm.value.weight / (height * height)).toFixed(2);

    // 更新健康数据
    currentWeight.value = parseFloat(profileForm.value.weight);
    currentBMI.value = parseFloat(bmi);

    // 从数据库获取目标数据
    if (context?.$http) {
      console.log("准备发送请求到 user-profile/get-goals");
      console.log("请求参数:", { userId: userid.value });
      console.log(
        "请求URL:",
        `${process.env.VUE_APP_BASE_API}/user-profile/get-goals?userId=${userid.value}`,
      );

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

            // 使用从后端获取的初始体重，如果没有则使用当前体重
            const initialWeight = goals.initialWeight || currentWeight.value;
            const totalDiff = Math.abs(initialWeight - targetWeight.value);
            const currentDiff = Math.abs(
              currentWeight.value - targetWeight.value,
            );

            // 计算进度：|初始体重 - 当前体重| / |初始体重 - 目标体重| * 100%
            if (totalDiff > 0) {
              const progress = (currentDiff / totalDiff) * 100;
              // 进度应该是100%减去当前差距占总差距的比例
              progressPercentage.value = Math.round(100 - progress);
              // 确保进度在0-100之间
              progressPercentage.value = Math.max(
                0,
                Math.min(100, progressPercentage.value),
              );
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

  // 初始化健康记录为空数组，等待从数据库获取
  healthRecords.value = [];
  healthRecordsTotal.value = 0;

  // 从数据库获取每日体重记录
  if (context?.$http) {
    // 每次请求前从localStorage中重新获取userid，确保是最新的
    const currentUserId = context?.$toolUtil.storageGet("userid");

    context
      .$http({
        url: `/user-daily-record/list`,
        method: "get",
        params: { userId: parseInt(currentUserId) },
      })
      .then((res) => {
        if (res && res.data && res.data.code === 0) {
          const records = res.data.records;
          if (records && records.length > 0) {
            // 更新健康记录数据
            healthRecords.value = records;
            healthRecordsTotal.value = records.length;
            // 更新最新体重
            // 按日期排序，获取最新记录
            const sortedRecords = [...records].sort((a, b) => {
              return new Date(b.recordDate) - new Date(a.recordDate);
            });
            const latestRecord = sortedRecords[0];
            if (latestRecord) {
              currentWeight.value = latestRecord.weight;
              currentBMI.value = latestRecord.bmi;
              // 更新档案管理的体重
              profileForm.value.weight = latestRecord.weight;
            }
            // 计算目标达成进度
            calculateProgress();

            // 重新渲染图表
            initChart();
          }
        }
      })
      .catch((error) => {
        // 静默处理错误
      });
  }

  // 初始化当前体重和BMI
  if (healthRecords.value.length > 0) {
    // 按日期排序，获取最新记录
    const sortedRecords = [...healthRecords.value].sort((a, b) => {
      return new Date(b.recordDate) - new Date(a.recordDate);
    });
    const latestRecord = sortedRecords[0];
    currentWeight.value = latestRecord.weight;
    currentBMI.value = latestRecord.bmi;
    // 更新档案管理的体重
    profileForm.value.weight = latestRecord.weight;
    console.log("最新健康记录:", latestRecord);
  }

  // 从本地存储获取目标数据
  const currentUserId = context?.$toolUtil.storageGet("userid");
  const goalStorageKey = `healthGoals_${currentUserId}`;
  const savedGoals = localStorage.getItem(goalStorageKey);

  if (savedGoals) {
    const goals = JSON.parse(savedGoals);
    if (goals) {
      if (goals.targetWeight) {
        targetWeight.value = parseFloat(goals.targetWeight);
        goalForm.value.targetWeight = parseFloat(goals.targetWeight);
      }
      if (goals.targetBMI) {
        targetBMI.value = parseFloat(goals.targetBMI);
        goalForm.value.targetBMI = parseFloat(goals.targetBMI);
      }
      if (goals.initialWeight) {
        initialWeight.value = parseFloat(goals.initialWeight);
      }
    }
  }

  // 如果没有初始体重，使用用户第一次记录的体重作为初始体重
  if (!initialWeight.value && healthRecords.value.length > 0) {
    // 按日期排序，获取最早记录
    const sortedRecords = [...healthRecords.value].sort((a, b) => {
      return new Date(a.recordDate) - new Date(b.recordDate);
    });
    const firstRecord = sortedRecords[0];
    initialWeight.value = firstRecord.weight;
    console.log("使用第一次记录的体重作为初始体重:", initialWeight.value);

    // 保存初始体重到本地存储
    const goalData = {
      targetWeight: targetWeight.value,
      targetBMI: targetBMI.value,
      initialWeight: initialWeight.value,
    };
    localStorage.setItem(goalStorageKey, JSON.stringify(goalData));
  }

  // 修复浮点数精度问题
  weightDifference.value = parseFloat(weightDifference.value.toFixed(1));

  // 更新健康状态
  updateHealthStatus();

  // 初始化图表
  setTimeout(() => {
    initChart();
  }, 100);

  // 计算目标达成进度
  calculateProgress();
};

// 计算目标达成进度
const calculateProgress = () => {
  // 计算差距和进度
  weightDifference.value = currentWeight.value - targetWeight.value;

  // 使用历史记录中最早的日期作为初始体重，最近的日期作为当前体重
  let initialWeightVal = 0;
  let currentWeightVal = 0;
  let currentBMIVal = 0;

  if (healthRecords.value && healthRecords.value.length > 0) {
    // 按日期排序记录（升序）
    const sortedRecords = [...healthRecords.value].sort((a, b) => {
      return new Date(a.recordDate) - new Date(b.recordDate);
    });

    // 最早的记录作为初始体重
    if (sortedRecords.length > 0) {
      initialWeightVal = sortedRecords[0].weight;
    }

    // 最新的记录作为当前体重和 BMI
    if (sortedRecords.length > 0) {
      const latestRecord = sortedRecords[sortedRecords.length - 1];
      currentWeightVal = latestRecord.weight;
      currentBMIVal = latestRecord.bmi;
      // 更新 currentWeight 和 currentBMI，确保左边上方的数据显示与进度条一致
      currentWeight.value = currentWeightVal;
      currentBMI.value = currentBMIVal;
    }
  }

  // 确保初始体重和当前体重都是有效的数字
  initialWeightVal = parseFloat(initialWeightVal) || 0;
  currentWeightVal = parseFloat(currentWeightVal) || 0;

  // 确保所有值都是数字类型
  initialWeightVal = parseFloat(initialWeightVal) || 0;
  currentWeightVal = parseFloat(currentWeightVal) || 0;
  const targetWeightVal = parseFloat(targetWeight.value) || 0;

  const totalDiff = Math.abs(initialWeightVal - targetWeightVal);
  const weightChange = Math.abs(initialWeightVal - currentWeightVal);

  // 计算进度：|初始体重 - 当前体重| / |初始体重 - 目标体重| × 100%
  if (totalDiff > 0) {
    // 检查是否已经达到或超过目标体重
    const isGoalAchieved =
      (initialWeightVal > targetWeightVal &&
        currentWeightVal <= targetWeightVal) ||
      (initialWeightVal < targetWeightVal &&
        currentWeightVal >= targetWeightVal);

    if (isGoalAchieved) {
      // 如果已经达到或超过目标体重，进度为 100%
      progressPercentage.value = 100;
    } else {
      // 否则计算进度
      const progress = (weightChange / totalDiff) * 100;
      // 确保进度在0-100之间
      progressPercentage.value = Math.max(
        0,
        Math.min(100, Math.round(progress)),
      );
    }
  } else {
    progressPercentage.value = 100;
  }

  // 输出目标达成进度信息
  console.log("最新体重:", currentWeightVal);
  console.log("最早体重:", initialWeightVal);
  console.log("进度条:", progressPercentage.value);
  console.log("健康记录数量:", healthRecords.value.length);
  if (healthRecords.value.length > 0) {
    console.log("第一条记录:", healthRecords.value[0]);
    console.log(
      "最后一条记录:",
      healthRecords.value[healthRecords.value.length - 1],
    );
  }

  // 计算预测
  calculatePredict();
};

// 计算预测
const calculatePredict = () => {
  let current = currentWeight.value;
  let goal = targetWeight.value;
  let pre = 0;
  let dayDiff = 0;

  if (healthRecords.value && healthRecords.value.length >= 2) {
    // 按日期排序记录（升序）
    const sortedRecords = [...healthRecords.value].sort((a, b) => {
      return new Date(a.recordDate) - new Date(b.recordDate);
    });

    // 最新的两条记录
    const latestRecord = sortedRecords[sortedRecords.length - 1];
    const previousRecord = sortedRecords[sortedRecords.length - 2];

    pre = previousRecord.weight;
    dayDiff =
      (new Date(latestRecord.recordDate) -
        new Date(previousRecord.recordDate)) /
      (1000 * 60 * 60 * 24);
  }

  // 没有足够数据
  if (dayDiff === 0) {
    predictText.value = "坚持记录两天以上，即可生成预测";
    return;
  }

  // 每天体重变化
  let perDay = (current - pre) / dayDiff;

  // 已经达到目标
  if (Math.abs(current - goal) < 0.1) {
    predictText.value = "🎉 恭喜！已达到目标体重，继续保持！";
    return;
  }

  // 减重用户
  if (current > goal) {
    if (perDay < 0) {
      // 正常减重
      let need = (current - goal) / Math.abs(perDay);
      predictText.value = `🚀 预测还需 ${Math.ceil(need)} 天达到目标体重`;
    } else {
      // 体重反弹
      predictText.value = "⚠️ 最近体重有所上升，坚持住就能重回正轨！";
    }
    return;
  }

  // 增重用户
  if (current < goal) {
    if (perDay > 0) {
      // 正常增重
      let need = (goal - current) / perDay;
      predictText.value = `🚀 预测还需 ${Math.ceil(need)} 天达到目标体重`;
    } else {
      // 体重下降
      predictText.value = "⚠️ 最近体重有所下降，多吃点很快就能达标！";
    }
    return;
  }

  // 维持
  predictText.value = "✅ 当前体重理想，继续保持健康生活！";
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

    // 根据选择的时间范围过滤数据
    if (selectedPeriod.value === "7") {
      // 过滤最近7天的记录
      const sevenDaysAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
      console.log("7天前日期:", sevenDaysAgo);
      filteredRecords = sortedRecords.filter((record) => {
        const recordDate = new Date(record.recordDate);
        console.log(
          "记录日期:",
          recordDate,
          "是否在范围内:",
          recordDate >= sevenDaysAgo,
        );
        return recordDate >= sevenDaysAgo;
      });
    } else if (selectedPeriod.value === "30") {
      // 过滤最近30天的记录
      const thirtyDaysAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
      filteredRecords = sortedRecords.filter((record) => {
        return new Date(record.recordDate) >= thirtyDaysAgo;
      });
    } else if (selectedPeriod.value === "90") {
      // 过滤最近90天的记录
      const ninetyDaysAgo = new Date(now.getTime() - 90 * 24 * 60 * 60 * 1000);
      filteredRecords = sortedRecords.filter((record) => {
        return new Date(record.recordDate) >= ninetyDaysAgo;
      });
    } else if (selectedPeriod.value === "custom") {
      // 自定义时间范围，这里简单处理，使用所有记录
      // 实际项目中应该添加日期选择器让用户选择自定义时间范围
      filteredRecords = sortedRecords;
    } else {
      // 默认使用所有记录
      filteredRecords = sortedRecords;
    }

    // 如果过滤后的记录为空，使用所有记录
    if (filteredRecords.length === 0 && sortedRecords.length > 0) {
      console.log("过滤后无数据，使用所有记录");
      filteredRecords = sortedRecords;
    }

    console.log("过滤后的记录:", filteredRecords);

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

    // 打印 ECharts 接收到的数据
    console.log("ECharts 接收到的数据 - 日期:", dates);
    console.log("ECharts 接收到的数据 - 体重:", weights);
    console.log("ECharts 接收到的数据 - BMI:", bmis);
    console.log("ECharts 接收到的数据 - 目标体重:", targetWeights);

    // 销毁旧图表
    if (chartInstance.value) {
      chartInstance.value.dispose();
    }

    // 创建新图表
    chartInstance.value = echarts.init(chartRef.value);

    // 检查是否有数据
    const hasData = filteredRecords.length > 0;

    const option = {
      tooltip: {
        trigger: "axis",
      },
      legend: {
        data: hasData ? ["体重", "BMI", "目标体重"] : [],
        bottom: "10%", // 向下移动图例
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
        data: hasData ? dates : [""],
      },
      yAxis: [
        {
          type: "value",
          name: "体重(kg)",
          position: "left",
          min: 0,
          max: 100,
        },
        {
          type: "value",
          name: "BMI",
          position: "right",
          min: 0,
          max: 40,
        },
      ],
      series: hasData
        ? [
            {
              name: "体重",
              type: "line",
              data: weights,
              itemStyle: {
                color: "#03CC88",
              },
              showSymbol: true,
              symbolSize: 6,
            },
            {
              name: "BMI",
              type: "line",
              yAxisIndex: 1,
              data: bmis,
              itemStyle: {
                color: "#ee6723",
              },
              showSymbol: true,
              symbolSize: 6,
            },
            {
              name: "目标体重",
              type: "line",
              data: targetWeights,
              lineStyle: {
                type: "dashed",
                color: "#999",
              },
              symbol: "none",
              showSymbol: false,
            },
          ]
        : [],
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

  // 保存目标到本地存储
  const currentUserId = context?.$toolUtil.storageGet("userid");
  const goalStorageKey = `healthGoals_${currentUserId}`;
  const goalData = {
    targetWeight: targetWeight.value,
    targetBMI: targetBMI.value,
    initialWeight: currentWeight.value, // 保存初始体重
  };

  localStorage.setItem(goalStorageKey, JSON.stringify(goalData));
  console.log("目标保存到本地存储成功:", goalData);

  context?.$toolUtil.message("目标保存成功", "success");
};

// 保存每日记录
const saveDailyRecord = () => {
  if (!dailyRecordForm.value.weight) {
    context?.$toolUtil.message("请输入体重", "error");
    return;
  }

  // 计算BMI
  let bmi = 0;
  if (profileForm.value.height && dailyRecordForm.value.weight) {
    const height = profileForm.value.height / 100; // 转换为米
    if (height > 0) {
      bmi = parseFloat(
        (dailyRecordForm.value.weight / (height * height)).toFixed(2),
      );
    } else {
      context?.$toolUtil.message("身高必须大于 0", "error");
      return;
    }
  } else {
    context?.$toolUtil.message("请输入身高和体重", "error");
    return;
  }
  // 准备记录数据
  const currentUserId = parseInt(context?.$toolUtil.storageGet("userid"));
  // 确保日期格式为 YYYY-MM-DD
  const recordDate = new Date(dailyRecordForm.value.recordDate)
    .toISOString()
    .split("T")[0];
  const recordData = {
    userId: currentUserId,
    recordDate: recordDate,
    weight: parseFloat(dailyRecordForm.value.weight),
    bmi: bmi,
  };

  // 创建新记录对象用于更新UI
  const newRecord = {
    id: Date.now(),
    ...recordData,
    createTime: moment().format("YYYY-MM-DD HH:mm:ss"),
  };

  // 检查是否有上一次记录
  let oldRecord = null;
  if (healthRecords.value && healthRecords.value.length > 0) {
    // 按日期排序记录（升序）
    const sortedRecords = [...healthRecords.value].sort((a, b) => {
      return new Date(a.recordDate) - new Date(b.recordDate);
    });
    // 找到最近的记录（不包括今天的记录）
    for (let i = sortedRecords.length - 1; i >= 0; i--) {
      if (sortedRecords[i].recordDate !== recordDate) {
        oldRecord = sortedRecords[i];
        break;
      }
    }
  }

  // 检查是否需要弹出警告提示
  if (oldRecord) {
    const oldDate = new Date(oldRecord.recordDate);
    const newDate = new Date(recordDate);
    const weightDiff = Math.abs(
      parseFloat(dailyRecordForm.value.weight) - oldRecord.weight,
    );
    const dayDiff = (newDate - oldDate) / (1000 * 60 * 60 * 24);

    // 触发提示条件：天数差 ≤7 天（一周内）且体重差绝对值 ≥3kg
    if (dayDiff <= 7 && weightDiff >= 3) {
      // 弹出警告提示
      ElMessageBox.confirm(
        `检测到您短时间内体重变化较大（7 天内变化≥3kg），请确认是否输入有误。<br>上次记录：${oldRecord.recordDate}，体重 ${oldRecord.weight} kg<br>本次记录：${recordDate}，体重 ${dailyRecordForm.value.weight} kg`,
        "体重数据异常提醒",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning",
          dangerouslyUseHTMLString: true,
          customClass: "modern-alert",
          confirmButtonClass: "modern-confirm-btn",
          cancelButtonClass: "modern-cancel-btn",
        },
      )
        .then(() => {
          // 点击确定，保存记录
          saveRecordToDatabase(recordData, newRecord, currentUserId);
        })
        .catch(() => {
          // 点击取消，清空输入框
          dailyRecordForm.value = {
            recordDate: new Date(),
            weight: "",
          };
        });
      return;
    }
  }

  // 如果不需要弹出警告提示，直接保存记录
  saveRecordToDatabase(recordData, newRecord, currentUserId);
};

// 保存记录到数据库
const saveRecordToDatabase = (recordData, newRecord, currentUserId) => {
  // 从本地存储中保存记录，实现覆盖已有记录的功能
  const storageKey = `healthRecords_${currentUserId}`;

  // 检查是否已存在相同日期的记录
  const existingIndex = healthRecords.value.findIndex(
    (item) => item.recordDate === recordData.recordDate,
  );

  if (existingIndex !== -1) {
    // 覆盖已有记录
    healthRecords.value[existingIndex] = newRecord;
  } else {
    // 添加新记录
    healthRecords.value.push(newRecord);
  }

  // 按日期排序
  healthRecords.value.sort((a, b) => {
    return new Date(b.recordDate) - new Date(a.recordDate);
  });

  healthRecordsTotal.value = healthRecords.value.length;

  // 更新本地存储
  localStorage.setItem(storageKey, JSON.stringify(healthRecords.value));

  // 调用后端API保存记录到数据库
  if (context?.$http) {
    console.log("开始保存记录到数据库");
    console.log("保存URL:", `/user-daily-record/save`);
    console.log("保存数据:", recordData);

    context
      .$http({
        url: `/user-daily-record/save`,
        method: "post",
        data: recordData,
      })
      .then((res) => {
        console.log("保存记录响应:", res);
        if (res && res.data) {
          console.log("响应代码:", res.data.code);
          console.log("响应消息:", res.data.msg);

          if (res.data.code === 0) {
            console.log("保存记录成功");
            // 保存成功后，重新从数据库获取健康记录
            console.log("开始重新获取健康记录");
            context
              .$http({
                url: `/user-daily-record/list`,
                method: "get",
                params: { userId: parseInt(currentUserId) },
              })
              .then((res) => {
                console.log("获取健康记录响应:", res);
                if (res && res.data && res.data.code === 0) {
                  const records = res.data.records;
                  console.log("从数据库获取的健康记录:", records);
                  if (records && records.length > 0) {
                    // 更新健康记录数据
                    healthRecords.value = records;
                    healthRecordsTotal.value = records.length;
                    // 更新最新体重
                    const latestRecord = records[0];
                    if (latestRecord) {
                      currentWeight.value = latestRecord.weight;
                      currentBMI.value = latestRecord.bmi;
                      // 更新档案管理的体重
                      profileForm.value.weight = latestRecord.weight;
                    }
                    // 计算目标达成进度
                    calculateProgress();

                    // 重新渲染图表
                    console.log("保存成功后重新渲染图表");
                    console.log(
                      "更新后的健康记录数量:",
                      healthRecords.value.length,
                    );
                    if (healthRecords.value.length > 0) {
                      console.log(
                        "更新后的第一条记录:",
                        healthRecords.value[0],
                      );
                    }
                    initChart();
                  } else {
                    console.log("数据库中没有健康记录");
                  }
                } else {
                  console.error("获取健康记录失败，响应代码:", res?.data?.code);
                }
              })
              .catch((error) => {
                console.error("获取健康记录失败:", error);
              });
          } else {
            console.error("保存记录失败，响应代码:", res.data.code);
            console.error("保存记录失败，响应消息:", res.data.msg);
          }
        } else {
          console.error("保存记录失败，响应格式不正确:", res);
        }
      })
      .catch((error) => {
        console.error("保存记录失败，网络错误:", error);
        console.error("错误详情:", error.message);
        console.error("错误堆栈:", error.stack);
      });
  } else {
    // 如果没有HTTP请求，至少更新图表
    console.log("没有HTTP请求，更新图表");
    initChart();
  }

  // 更新当前体重和BMI
  currentWeight.value = newRecord.weight;
  currentBMI.value = newRecord.bmi;
  weightDifference.value = currentWeight.value - targetWeight.value;

  // 从本地存储获取目标数据，包括初始体重
  const goalStorageKey = `healthGoals_${currentUserId}`;
  const savedGoals = localStorage.getItem(goalStorageKey);

  if (savedGoals) {
    const goals = JSON.parse(savedGoals);
    // 使用从本地存储获取的初始体重，如果没有则使用当前体重
    const initialWeightVal = goals?.initialWeight || currentWeight.value;
    const totalDiff = Math.abs(initialWeightVal - targetWeight.value);
    const currentDiff = Math.abs(currentWeight.value - targetWeight.value);

    // 计算进度：|初始体重 - 当前体重| / |初始体重 - 目标体重| * 100%
    if (totalDiff > 0) {
      const progress = (currentDiff / totalDiff) * 100;
      // 进度应该是100%减去当前差距占总差距的比例
      progressPercentage.value = Math.round(100 - progress);
      // 确保进度在0-100之间
      progressPercentage.value = Math.max(
        0,
        Math.min(100, progressPercentage.value),
      );
    } else {
      progressPercentage.value = 100;
    }
  } else {
    // 如果没有保存的目标数据，使用当前体重作为初始体重
    const initialWeightVal = currentWeight.value;
    const totalDiff = Math.abs(initialWeightVal - targetWeight.value);
    const currentDiff = Math.abs(currentWeight.value - targetWeight.value);

    // 计算进度：|初始体重 - 当前体重| / |初始体重 - 目标体重| * 100%
    if (totalDiff > 0) {
      const progress = (currentDiff / totalDiff) * 100;
      // 进度应该是100%减去当前差距占总差距的比例
      progressPercentage.value = Math.round(100 - progress);
      // 确保进度在0-100之间
      progressPercentage.value = Math.max(
        0,
        Math.min(100, progressPercentage.value),
      );
    } else {
      progressPercentage.value = 100;
    }
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
      // 从本地存储中删除记录
      const currentUserId = context?.$toolUtil.storageGet("userid");
      const storageKey = `healthRecords_${currentUserId}`;
      healthRecords.value = healthRecords.value.filter(
        (item) => item.id !== id,
      );
      healthRecordsTotal.value = healthRecords.value.length;

      // 更新本地存储
      localStorage.setItem(storageKey, JSON.stringify(healthRecords.value));

      // 调用后端API删除数据库中的记录
      if (context?.$http) {
        context
          .$http({
            url: `/user-daily-record/delete`,
            method: "delete",
            params: { id: id },
          })
          .then((res) => {
            console.log("删除记录响应:", res);
            if (res && res.data && res.data.code === 0) {
              console.log("删除记录成功");
            }
          })
          .catch((error) => {
            console.error("删除记录失败:", error);
          });
      }

      // 更新图表
      initChart();

      // 更新当前体重和BMI
      if (healthRecords.value.length > 0) {
        // 按日期排序，获取最新记录
        const sortedRecords = [...healthRecords.value].sort((a, b) => {
          return new Date(b.recordDate) - new Date(a.recordDate);
        });
        const latestRecord = sortedRecords[0];
        currentWeight.value = latestRecord.weight;
        currentBMI.value = latestRecord.bmi;
      }

      context?.$toolUtil.message("删除成功", "success");
    })
    .catch(() => {});
};

// 删除选中记录
const deleteRecords = () => {
  context
    .$confirm("确定要删除所有健康记录吗？", "提示")
    .then(() => {
      // 从本地存储中删除所有记录
      const currentUserId = context?.$toolUtil.storageGet("userid");
      const storageKey = `healthRecords_${currentUserId}`;
      healthRecords.value = [];
      healthRecordsTotal.value = 0;

      // 更新本地存储
      localStorage.setItem(storageKey, JSON.stringify(healthRecords.value));

      // 调用后端API删除数据库中的所有记录
      if (context?.$http) {
        context
          .$http({
            url: `/user-daily-record/delete-all`,
            method: "delete",
            params: { userId: currentUserId },
          })
          .then((res) => {
            console.log("删除所有记录响应:", res);
            if (res && res.data && res.data.code === 0) {
              console.log("删除所有记录成功");
            }
          })
          .catch((error) => {
            console.error("删除所有记录失败:", error);
          });
      }

      // 更新图表
      initChart();

      // 重置当前体重和BMI
      currentWeight.value = 0;
      currentBMI.value = 0;

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
  background: #03cc88;
  padding: 0 24px;
  position: relative;
  z-index: 999;

  .nav-item {
    padding: 15px 24px;
    cursor: pointer;
    position: relative;
    font-size: 15px;
    color: #fff;
    transition: all 0.3s ease;
    border-bottom: 3px solid transparent;

    &:hover {
      color: #fff;
      background: rgba(255, 255, 255, 0.1);
    }

    &.nav-item-active {
      color: #fff;
      border-bottom-color: #fff;
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
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;

  .forum_item {
    cursor: pointer;
    transition: box-shadow 0.3s ease;
  }

  .el-pagination {
    grid-column: 1 / -1;
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
