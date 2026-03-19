<template>
  <div class="forum_view" :style="{}">
    <div class="back_view" v-if="centerType">
      <el-button class="back_btn" @click="backClick" type="primary"
        >返回</el-button
      >
    </div>
    <div class="section_title">
      <span>{{ myType ? "我的发布" : formName }}</span>
    </div>
    <el-form :model="searchQuery" class="list_search">
      <div class="search_view">
        <div class="search_label">标题：</div>
        <div class="search_box">
          <el-input
            class="search_inp"
            v-model="searchQuery.title"
            placeholder="标题"
            size="small"
            clearable
          >
          </el-input>
        </div>
      </div>
      <div class="search_btn_view">
        <el-button class="search_btn" type="primary" @click="searchClick"
          >搜索</el-button
        >
        <el-button
          class="add_btn"
          @click="addClick"
          v-if="!myType && btnAuth('forum', '新增')"
          type="success"
          >发布食谱</el-button
        >
      </div>
    </el-form>
    <div class="forum_list_container">
      <forum-item
        v-for="(item, index) in list"
        :key="index"
        :item="item"
        :index="index"
        :userid="Number(userid)"
        :forum-show-index="forumShowIndex"
        :btn-auth="btnAuth"
        @mouseenter="(event) => forumEnter(index)"
        @mouseleave="forumLeave"
        @click="(id) => detailClick(id)"
        @edit="(id) => editClick(id)"
        @del="(id) => delClick(id)"
      />
    </div>
    <div class="pagination_container">
      <el-pagination
        background
        :layout="layouts.join(',')"
        :total="total"
        :page-size="listQuery.limit"
        v-model:current-page="listQuery.page"
        prev-text="上一页"
        next-text="下一页"
        :hide-on-single-page="false"
        :style="{}"
        @size-change="sizeChange"
        @current-change="currentChange"
      />
    </div>

    <el-dialog
      v-model="formVisible"
      :title="formTitle"
      width="80%"
      custom-class="edit_view"
      destroy-on-close
    >
      <el-form
        class="add_form"
        :model="form"
        label-width="120px"
        :rules="rules"
        ref="formRef"
      >
        <!-- 基础信息区 -->
        <el-row>
          <el-col :span="12">
            <el-form-item label="食谱标题" prop="title">
              <el-input
                class="list_inp"
                v-model="form.title"
                placeholder="请输入食谱标题"
              ></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="封面图片">
              <upload
                :limit="1"
                @change="uploadChange"
                type="image"
                :value="form.coverImage"
              ></upload>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="食谱描述" prop="description">
              <el-input
                type="textarea"
                class="list_inp"
                v-model="form.description"
                placeholder="请输入食谱描述"
                :rows="3"
              ></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 核心匹配区 -->
        <el-row>
          <el-col :span="8">
            <el-form-item label="健身目标" prop="fitnessGoal">
              <el-select
                v-model="form.fitnessGoal"
                placeholder="请选择健身目标"
              >
                <el-option label="增肌" value="增肌"></el-option>
                <el-option label="减脂" value="减脂"></el-option>
                <el-option label="维持" value="维持"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="用餐场景" prop="mealScene">
              <el-select v-model="form.mealScene" placeholder="请选择用餐场景">
                <el-option label="早餐" value="早餐"></el-option>
                <el-option label="午餐" value="午餐"></el-option>
                <el-option label="晚餐" value="晚餐"></el-option>
                <el-option label="加餐" value="加餐"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="饮食禁忌">
              <el-select
                v-model="form.dietaryRestrictions"
                placeholder="请选择饮食禁忌"
                multiple
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
                <el-option label="gluten-free" value="gluten-free"></el-option>
                <el-option label="乳糖不耐" value="乳糖不耐"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 食材明细区 -->
        <el-row>
          <el-col :span="24">
            <el-form-item label="食材明细">
              <el-table :data="form.ingredients" style="width: 100%">
                <el-table-column
                  prop="ingredientName"
                  label="食材名称"
                  width="180"
                >
                  <template #default="scope">
                    <el-input
                      v-model="scope.row.ingredientName"
                      placeholder="请输入食材名称"
                      @input="
                        searchFoodsByInput(
                          scope.row.ingredientName,
                          scope.$index
                        )
                      "
                      @focus="showFoodSearch(scope.$index)"
                    ></el-input>
                    <el-popover
                      v-model:visible="foodSearchPopover[scope.$index]"
                      placement="bottom-start"
                      :width="300"
                    >
                      <template #reference>
                        <!-- 触发元素 -->
                      </template>
                      <div v-if="filteredFoods.length > 0">
                        <div
                          v-for="food in filteredFoods"
                          :key="food.name"
                          class="food-item"
                          @click="selectFoodForInput(food, scope.$index)"
                        >
                          {{ food.name }}
                        </div>
                      </div>
                      <div v-else>无匹配食材</div>
                    </el-popover>
                  </template>
                </el-table-column>
                <el-table-column prop="weight" label="重量" width="120">
                  <template #default="scope">
                    <el-input-number
                      v-model="scope.row.weight"
                      :min="0.1"
                      :step="0.1"
                      @change="calculateCalories"
                    ></el-input-number>
                  </template>
                </el-table-column>
                <el-table-column prop="unit" label="单位" width="100">
                  <template #default="scope">
                    <el-select v-model="scope.row.unit" placeholder="单位">
                      <el-option label="g" value="g"></el-option>
                      <el-option label="kg" value="kg"></el-option>
                      <el-option label="ml" value="ml"></el-option>
                      <el-option label="l" value="l"></el-option>
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="baseCalories"
                  label="基础热量(kcal/100g)"
                  width="180"
                >
                  <template #default="scope">
                    <el-input
                      v-model="scope.row.baseCalories"
                      type="number"
                      @change="calculateCalories"
                    ></el-input>
                  </template>
                </el-table-column>
                <el-table-column
                  prop="calories"
                  label="实际热量(kcal)"
                  width="150"
                >
                  <template #default="scope">
                    <span>{{ scope.row.calories.toFixed(2) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="100">
                  <template #default="scope">
                    <el-button
                      type="danger"
                      size="small"
                      @click="removeIngredient(scope.$index)"
                      >删除</el-button
                    >
                  </template>
                </el-table-column>
              </el-table>
              <el-button type="primary" @click="addIngredient"
                >添加食材</el-button
              >
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 总热量区 -->
        <el-row>
          <el-col :span="12">
            <el-form-item label="总热量(kcal)" prop="totalCalories">
              <el-input-number
                v-model="form.totalCalories"
                :min="0"
                :step="0.1"
              ></el-input-number>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <span class="formModel_btn_box">
          <el-button class="formModel_cancel" @click="formVisible = false"
            >关闭</el-button
          >
          <el-button class="formModel_confirm" type="primary" @click="save">{{
            form.id ? "修改保存" : "发布"
          }}</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 食材搜索弹窗 -->
    <el-dialog
      v-model="foodSearchVisible"
      :title="'搜索食材'"
      width="60%"
      destroy-on-close
    >
      <el-input
        v-model="foodSearchKeyword"
        placeholder="请输入食材名称"
        @input="searchFoods"
        class="food-search-input"
      ></el-input>
      <el-table :data="foodList" style="width: 100%; margin-top: 20px">
        <el-table-column prop="name" label="食材名称"></el-table-column>
        <el-table-column
          prop="calories"
          label="热量(kcal/100g)"
        ></el-table-column>
        <el-table-column prop="unit" label="单位"></el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button
              type="primary"
              size="small"
              @click="selectFood(scope.row)"
              >选择</el-button
            >
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

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
</template>

<script setup>
import { ref, nextTick, getCurrentInstance } from "vue";
import { ElMessageBox } from "element-plus";
import { useRoute, useRouter } from "vue-router";
import { Thumb, Star, Close } from "@element-plus/icons-vue";
import upload from "@/components/upload.vue";
import ForumItem from "@/components/ForumItem.vue";
import ForumDetail from "@/components/ForumDetail.vue";
const context = getCurrentInstance()?.appContext.config.globalProperties;
//基础信息
const tableName = "forum";
const formName = "食谱列表";
const router = useRouter();
const route = useRoute();
//基础信息
//权限验证
const btnAuth = (e, a) => {
  return context?.$toolUtil.isAuth(e, a);
};
const list = ref([]);
const listLoading = ref(false);
const listQuery = ref({
  page: 1,
  limit: 15,
  sort: "addtime",
  order: "desc",
});
const myListQuery = ref({
  page: 1,
  limit: 15,
  sort: "addtime",
  order: "desc",
});
const searchQuery = ref({});
//分页
const layouts = ref(["total", "prev", "pager", "next", "sizes", "jumper"]);
const total = ref(0);
const sizeChange = (size) => {
  listQuery.value.limit = size;
  getList();
};
const currentChange = (page) => {
  listQuery.value.page = page;
  getList();
};
//分页
const searchClick = () => {
  listQuery.value.page = 1;
  getList();
};
const getList = () => {
  listLoading.value = true;
  let url = myType.value ? `recipe/user/${userid.value}` : "recipe/public";
  // 构建搜索参数
  let params = {
    ...listQuery.value,
    ...searchQuery.value,
  };
  context
    ?.$http({
      url: url,
      method: "get",
      params: params,
    })
    .then((res) => {
      listLoading.value = false;
      // 检查响应数据结构
      if (res && res.data) {
        let recipes = [];
        if (res.data.recipes) {
          // 后端直接返回recipes
          recipes = res.data.recipes;
        } else if (res.data.data && res.data.data.recipes) {
          // 后端返回data.recipes
          recipes = res.data.data.recipes;
        } else {
          // 处理数据结构不符合预期的情况
          list.value = [];
          total.value = 0;
          console.error("响应数据结构不符合预期:", res);
          return;
        }

        // 前端搜索过滤
        if (searchQuery.value.title) {
          const searchTerm = searchQuery.value.title.toLowerCase();
          recipes = recipes.filter((recipe) =>
            recipe.title.toLowerCase().includes(searchTerm)
          );
        }

        // 前端分页处理
        const totalItems = recipes.length;
        const page = listQuery.value.page;
        const limit = listQuery.value.limit;
        const startIndex = (page - 1) * limit;
        const endIndex = startIndex + limit;
        const paginatedRecipes = recipes.slice(startIndex, endIndex);

        list.value = paginatedRecipes;
        total.value = totalItems;
      } else {
        // 处理数据结构不符合预期的情况
        list.value = [];
        total.value = 0;
        console.error("响应数据结构不符合预期:", res);
      }
    })
    .catch((error) => {
      listLoading.value = false;
      list.value = [];
      total.value = 0;
      console.error("请求失败:", error);
    });
};
const userid = ref(0);
const form = ref({
  title: "",
  description: "",
  coverImage: "",
  fitnessGoal: "",
  dietaryRestrictions: "",
  mealScene: "",
  totalCalories: 0,
  userId: userid.value,
  ingredients: [],
});
const rules = ref({
  title: [
    {
      required: true,
      message: "请输入食谱标题",
      trigger: "blur",
    },
  ],
  description: [
    {
      required: true,
      message: "请输入食谱描述",
      trigger: "blur",
    },
  ],
  fitnessGoal: [
    {
      required: true,
      message: "请选择健身目标",
      trigger: "blur",
    },
  ],
  mealScene: [
    {
      required: true,
      message: "请选择用餐场景",
      trigger: "blur",
    },
  ],
  totalCalories: [
    {
      required: true,
      message: "请输入总热量",
      trigger: "blur",
    },
  ],
});
const formRef = ref(null);
const formVisible = ref(false);
const formTitle = ref("");
const forumShowIndex = ref(-1);
//判断按钮显示
const forumEnter = (index) => {
  forumShowIndex.value = index;
};
const forumLeave = () => {
  forumShowIndex.value = -1;
};
const resetForm = () => {
  form.value = {
    title: "",
    description: "",
    coverImage: "",
    fitnessGoal: "",
    dietaryRestrictions: "",
    mealScene: "",
    totalCalories: 0,
    userId: userid.value,
    ingredients: [],
  };
};
//添加食材
const addIngredient = () => {
  form.value.ingredients.push({
    ingredientCode: "",
    ingredientName: "",
    weight: 100,
    unit: "g",
    baseCalories: 0,
    calories: 0,
  });
  // 清空搜索关键词
  foodSearchKeyword.value = "";
  // 重置食材列表为完整列表
  foodList.value = mockFoods;
  // 触发食材选择弹窗
  currentIngredientIndex.value = form.value.ingredients.length - 1;
  foodSearchVisible.value = true;
  // 下一帧自动聚焦到搜索框
  setTimeout(() => {
    const searchInput = document.querySelector(".food-search-input input");
    if (searchInput) {
      searchInput.focus();
    }
  }, 100);
};
//删除食材
const removeIngredient = (index) => {
  form.value.ingredients.splice(index, 1);
  calculateCalories();
};
//计算热量
const calculateCalories = () => {
  let total = 0;
  form.value.ingredients.forEach((ingredient) => {
    if (ingredient.weight && ingredient.baseCalories) {
      // 计算实际热量：(基础热量 / 100) * 重量
      ingredient.calories = parseFloat(
        ((ingredient.baseCalories / 100) * ingredient.weight).toFixed(2)
      );
      total += ingredient.calories;
    }
  });
  form.value.totalCalories = parseFloat(total.toFixed(2));
};
//食材搜索
const foodSearchVisible = ref(false);
const foodSearchKeyword = ref("");
const foodList = ref([]);
const currentIngredientIndex = ref(-1);
const foodSearchPopover = ref({});
const filteredFoods = ref([]);

// 模拟食材数据
const mockFoods = [
  { name: "红烧肉(瘦肉)", calories: 164, unit: "g" },
  { name: "鸭肉(家养，含皮和肉)", calories: 404, unit: "g" },
  { name: "猪肉(瘦)", calories: 143, unit: "g" },
  { name: "肉包", calories: 205, unit: "g" },
  { name: "猪肉(肋条肉)", calories: 568, unit: "g" },
  { name: "鸡肉", calories: 167, unit: "g" },
  { name: "牛肉(精瘦)", calories: 113, unit: "g" },
  { name: "猪肉(肥瘦)", calories: 395, unit: "g" },
  { name: "鸡胸肉", calories: 118, unit: "g" },
  { name: "卤肉", calories: 386, unit: "g" },
  { name: "粉蒸肉(瘦肉)", calories: 178, unit: "g" },
  { name: "咸肉", calories: 390, unit: "g" },
];

//显示食材搜索
const showFoodSearch = (index) => {
  currentIngredientIndex.value = index;
  foodSearchVisible.value = true;
};
//搜索食材
const searchFoods = () => {
  if (foodSearchKeyword.value) {
    const filteredFoods = mockFoods.filter((item) =>
      item.name.includes(foodSearchKeyword.value)
    );
    foodList.value = filteredFoods;
    // 如果搜索结果为空，给出提示
    if (filteredFoods.length === 0) {
      context?.$toolUtil.message(
        `未找到包含"${foodSearchKeyword.value}"的食材`,
        "warning"
      );
    }
  } else {
    // 搜索框为空时不展示任何食材
    foodList.value = [];
  }
};
//选择食材
const selectFood = (food) => {
  if (currentIngredientIndex.value !== -1) {
    form.value.ingredients[currentIngredientIndex.value] = {
      ingredientCode: food.name,
      ingredientName: food.name,
      weight: 100,
      unit: food.unit,
      baseCalories: food.calories,
      calories: food.calories,
    };
    calculateCalories();
  }
  foodSearchVisible.value = false;
};

// 实时搜索食材
const searchFoodsByInput = (keyword, index) => {
  if (keyword) {
    filteredFoods.value = mockFoods.filter((item) =>
      item.name.includes(keyword)
    );
    // 显示搜索结果弹窗
    foodSearchPopover.value[index] = true;
  } else {
    filteredFoods.value = [];
    foodSearchPopover.value[index] = false;
  }
};

// 选择食材（输入框版）
const selectFoodForInput = (food, index) => {
  form.value.ingredients[index] = {
    ingredientCode: food.name,
    ingredientName: food.name,
    weight: 100,
    unit: food.unit,
    baseCalories: food.calories,
    calories: food.calories,
  };
  calculateCalories();
  foodSearchPopover.value[index] = false;
};
//上传回调
const uploadChange = (value) => {
  form.value.coverImage = value;
};
//发布食谱
const addClick = () => {
  resetForm();
  formTitle.value = "发布食谱";
  formVisible.value = true;
};
//修改食谱
const editClick = (id = null) => {
  if (id) {
    context
      ?.$http({
        url: `recipe/info/${id}`,
        method: "get",
      })
      .then((res) => {
        // 检查响应数据结构
        if (res && res.data) {
          if (res.data.recipe) {
            // 后端直接返回recipe
            form.value = res.data.recipe;
            formTitle.value = "修改食谱";
            formVisible.value = true;
          } else if (res.data.data && res.data.data.recipe) {
            // 后端返回data.recipe
            form.value = res.data.data.recipe;
            formTitle.value = "修改食谱";
            formVisible.value = true;
          } else {
            console.error("响应数据结构不符合预期:", res);
            context?.$toolUtil.message("获取食谱信息失败", "error");
          }
        } else {
          console.error("响应数据结构不符合预期:", res);
          context?.$toolUtil.message("获取食谱信息失败", "error");
        }
      })
      .catch((error) => {
        console.error("请求失败:", error);
        context?.$toolUtil.message("获取食谱信息失败", "error");
      });
  }
};
//判断是否调我的发布列表
const myType = ref(false);
//判断是否从个人中心跳转
const centerType = ref(false);
//返回
const backClick = () => {
  router.push(
    `/index/${context?.$toolUtil.storageGet("frontSessionTable")}Center`
  );
};
//初始化
const init = () => {
  if (route.query.centerType) {
    centerType.value = true;
  }
  if (route.query.myType) {
    myType.value = true;
  }
  userid.value = Number(context?.$toolUtil.storageGet("userid")) || 0;
  getList();

  // 检查URL中是否有id参数，如果有，自动打开详情
  if (route.query.id) {
    detailClick(route.query.id);
  }
};
const detailVisible = ref(false);
const detailForm = ref({});
// 行为相关变量
const isLiked = ref(false);
const isCollected = ref(false);
const isDisliked = ref(false);
const likeCount = ref(0);
const collectCount = ref(0);
const dislikeCount = ref(0);

//详情
const detailClick = (id = null) => {
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
        } else if (res.data.data && res.data.data.recipe) {
          // 后端返回data.recipe
          detailForm.value = res.data.data.recipe;
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

// 获取行为数量
const getBehaviorCounts = (recipeId) => {
  context
    ?.$http({
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
    ?.$http({
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
    ?.$http({
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
    ?.$http({
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
      ?.$http({
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
      ?.$http({
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
//删除
const delClick = (id = null) => {
  if (id) {
    ElMessageBox.confirm(`是否删除选中食谱`, "提示", {
      confirmButtonText: "是",
      cancelButtonText: "否",
      type: "warning",
    }).then(() => {
      // 调用删除食谱的API
      context
        ?.$http({
          url: `recipe/delete/${id}`,
          method: "delete",
        })
        .then((res) => {
          // 检查响应数据结构
          if (res && res.data && res.data.code === 0) {
            context?.$toolUtil.message("删除成功", "success", () => {
              getList();
              // 如果当前在详情页，关闭详情页
              detailVisible.value = false;
            });
          } else {
            console.error("响应数据结构不符合预期:", res);
            context?.$toolUtil.message("删除失败", "error");
          }
        })
        .catch((error) => {
          console.error("请求失败:", error);
          context?.$toolUtil.message("删除失败", "error");
        });
    });
  }
};
//发布食谱
const save = () => {
  formRef.value.validate((valid) => {
    if (valid) {
      // 复制表单数据
      let formData = JSON.parse(JSON.stringify(form.value));
      // 将dietaryRestrictions数组转换为逗号分隔的字符串
      if (Array.isArray(formData.dietaryRestrictions)) {
        formData.dietaryRestrictions = formData.dietaryRestrictions.join(",");
      }
      context
        ?.$http({
          url: "recipe/publish",
          method: "post",
          data: formData,
        })
        .then((res) => {
          // 检查响应数据结构
          if (res && res.data && res.data.code === 0) {
            context?.$toolUtil.message(
              `${form.value.id ? "修改" : "发布"}成功`,
              "success",
              () => {
                getList();
                formVisible.value = false;
              }
            );
          } else {
            console.error("响应数据结构不符合预期:", res);
            context?.$toolUtil.message(
              `${form.value.id ? "修改" : "发布"}失败`,
              "error"
            );
          }
        })
        .catch((error) => {
          console.error("请求失败:", error);
          context?.$toolUtil.message(
            `${form.value.id ? "修改" : "发布"}失败`,
            "error"
          );
        });
    }
  });
};
init();
</script>

<style lang="scss" scoped>
// 返回盒子
.back_view {
  border-radius: 4px;
  padding: 10px 0px;
  margin: 10px auto;
  background: none;
  width: 100%;
  text-align: right;
  // 返回按钮
  .back_btn {
    border: 1px solid var(--theme);
    cursor: pointer;
    border-radius: 0px;
    padding: 0 24px;
    color: #fff;
    background: var(--theme);
    width: auto;
    font-size: 14px;
    height: 34px;
  }
  // 返回按钮-悬浮
  .back_btn:hover {
  }
}
//搜索
.list_search {
  .search_view {
    .search_label {
    }
    .search_box {
      // 输入框
      :deep(.search_inp) {
      }
    }
  }
  .search_btn_view {
    // 搜索按钮
    .search_btn {
    }
    // 搜索按钮-悬浮
    .search_btn:hover {
    }
    // 新增按钮
    .add_btn {
    }
    // 新增按钮-悬浮
    .add_btn:hover {
    }
  }
}
.forum_list_container {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 一行三个 */
  gap: 20px; /* 卡片间距 */
  padding: 20px;
  min-height: 600px; /* 确保容器有足够高度 */
}

.pagination_container {
  width: 100%;
  display: flex;
  justify-content: center;
  margin: 20px 0;
  padding: 0 20px;
}

// 列表盒子
.forum_list {
  width: 100%;
  // item
  .forum_item {
    // item左部
    .forum_item_left {
      // 标题
      .forum_item_title {
      }
      // 发布人
      .forum_item_name {
      }
    }
    // 按钮盒子
    .forum_item_btn_box {
      // 修改按钮
      .forum_edit_btn {
      }
      // 修改按钮-悬浮
      .forum_edit_btn:hover {
      }
      // 删除按钮
      .forum_del_btn {
      }
      // 删除按钮-悬浮
      .forum_del_btn:hover {
      }
    }
    // 时间
    .forum_time {
    }
  }

  .forum_item:hover {
  }
}

//帖子详情
.forum_detail {
  .forum_title {
  }

  .forum_info {
    .forum_name {
    }
    .forum_time {
    }
  }

  .forum_content {
  }
}
// 评论盒子
.forum_comment_box {
  // 分割线
  :deep(.comment_divider) {
    border: none;
    background: #dcdfe6;
    // 分割线文字样式
    .el-divider__text {
      font-size: 16px;
    }
  }
  // 新增评论盒子
  .comment_add_box {
    margin: 0 0 20px;
    text-align: right;
    // 按钮
    .comment_add {
      border: 0;
      cursor: pointer;
      border-radius: 2px;
      padding: 0 24px;
      margin: 0 10px 0 0;
      color: #fff;
      background: var(--theme);
      width: auto;
      font-size: 15px;
      height: 36px;
    }
    // 按钮-悬浮
    .comment_add:hover {
    }
  }
  //帖子评论
  .forum_comment_list {
    // 评论item
    .forum_comment {
      // 评论用户盒子
      .forum_comment_user {
        // 用户头像
        .forum_comment_user_avatar {
        }
        // 用户名
        .forum_comment_username {
        }
      }
      // 评论内容
      .forum_comment_content {
      }
      // 回复按钮盒子
      .forum_comment_reply_view {
        // 删除按钮
        .forum_comment_del {
        }
        // 删除按钮-悬浮
        .forum_comment_del:hover {
        }
        // 回复按钮
        .forum_comment_reply {
        }
        // 回复按钮-悬浮
        .forum_comment_reply:hover {
        }
      }
      // 二级评论盒子
      .forum_comment_second_list {
        // 分割线
        :deep(.forum_comment_second_divider) {
          border: none;
          background: #dcdfe6;
          // 分割线文字样式
          .el-divider__text {
            font-size: 16px;
          }
        }
        // 二级评论item
        .forum_comment_second {
          // 用户盒子
          .forum_comment_second_user {
            // 用户头像
            .forum_comment_second_user_avatar {
            }
            // 用户名
            .forum_comment_second_username {
            }
          }
          // 二级内容
          .forum_comment_second_content {
          }
          // 二级按钮盒子
          .forum_comment_second_reply_view {
            // 二级删除
            .forum_comment_second_del {
            }
            // 二级删除-悬浮
            .forum_comment_second_del:hover {
            }
          }
        }
      }
    }
  }
}

// 分页器
.el-pagination {
  // 总页码
  :deep(.el-pagination__total) {
  }
  // 上一页
  :deep(.btn-prev) {
  }
  // 下一页
  :deep(.btn-next) {
  }
  // 上一页禁用
  :deep(.btn-prev:disabled) {
  }
  // 下一页禁用
  :deep(.btn-next:disabled) {
  }
  // 页码
  :deep(.el-pager) {
    // 数字
    .number {
    }
    // 数字悬浮
    .number:hover {
    }
    // 选中
    .number.is-active {
    }
  }
  // sizes
  :deep(.el-pagination__sizes) {
    .el-select {
      //去掉默认样式
      .select-trigger {
        height: 100%;
        .el-input {
          height: 100%;
        }
      }
    }
  }
  // 跳页
  :deep(.el-pagination__jump) {
    // 输入框
    .el-input {
    }
  }
}
// 表单
.add_form {
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
      // 富文本
      .list_editor {
      }
      //单选框样式
      .list_radio {
        //未选中样式
        .el-radio {
          //单选框
          .el-radio__inner {
          }
          //提示文字
          .el-radio__label {
          }
        }
        //选中样式
        .is-checked {
          //单选框
          .el-radio__inner {
          }
          //提示文字
          .el-radio__label {
          }
        }
      }
    }
  }
}
///* 按钮盒子 */
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

/* 食材搜索结果 */
.food-item {
  padding: 8px 12px;
  cursor: pointer;
  &:hover {
    background-color: #f5f7fa;
  }
}
</style>
<style>
/*总盒子*/
.forum_view {
  width: 1200px;
  margin: 20px auto;
  padding: 0px;
  background: #fff;
  min-height: calc(100vh - 200px);
  display: flex;
  flex-direction: column;
}

/*列表盒子*/
.forum_view .forum_list {
  flex: 1;
}
/**标题**/
.forum_view .section_title {
  width: 100%;
  margin: 0px 0 20px;
  z-index: 999;
  text-align: left;
  background: none;
  margin-left: -10px;
  border-bottom: 1px #e5e5e5 solid;
  position: relative;
}
.forum_view .section_title span {
  display: inline-block;
  font-size: 20px;
  background: var(--theme);
  color: #fff;
  font-weight: 500;
  padding: 15px 20px 15px 20px;
  min-width: 250px;
}
.forum_view .section_title:after {
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

/*搜索盒子*/
.forum_view .list_search {
  width: 100%;
  margin: 30px auto;
  display: flex;
  align-items: center;
  justify-content: center;
}
.forum_view .list_search .search_view {
  display: flex;
  align-items: center;
  margin-right: 10px;
}
.forum_view .list_search .search_view .search_label {
  font-size: 16px;
  white-space: nowrap;
}
.forum_view .list_search .search_view .search_box {
}
.forum_view .list_search .search_view .search_box .search_inp {
  padding: 0 10px;
  border: 1px solid #ddd;
  font-size: 15px;
  height: 38px;
}
.forum_view .list_search .search_btn_view .search_btn {
  background: var(--theme);
  color: rgb(255, 255, 255);
  border: 0;
  border-radius: 0;
  height: 38px;
  font-size: 15px;
}
.forum_view .list_search .search_btn_view .search_btn:hover {
  opacity: 0.8;
}
.forum_view .list_search .search_btn_view .add_btn {
  background: #666;
  color: rgb(255, 255, 255);
  border: 0;
  border-radius: 0;
  height: 38px;
  font-size: 15px;
}
.forum_view .list_search .search_btn_view .add_btn:hover {
  opacity: 0.8;
}

/*详情盒子*/
.forum_view .forum_detail {
  width: 100%;
  margin: 20px 0px;
}
/*标题*/
.forum_view .forum_detail .forum_title {
  width: 100%;
  text-align: center;
  font-size: 22px;
  font-weight: bold;
  color: rgb(0, 0, 0);
}
/*info盒子*/
.forum_view .forum_detail .forum_info {
  width: 100%;
  text-align: center;
  color: rgb(153, 153, 153);
  font-size: 16px;
  margin: 20px 0px 0px;
}
/*发布人*/
.forum_view .forum_detail .forum_info .forum_name {
  margin: 0px 10px 0px 0px;
}
/*时间*/
.forum_view .forum_detail .forum_info .forum_time {
  margin: 0px 10px 0px 0px;
}
/*分割线*/
.forum_view .forum_detail .el-divider--horizontal {
  display: block;
  height: 0px;
  width: 100%;
  margin: 24px 0;
}
/*内容区*/
.forum_view .forum_detail .forum_content {
  width: 100%;
  text-align: left;
  font-size: 16px;
  color: rgb(102, 102, 102);
  text-indent: 2rem;
}
/*食谱元信息*/
.forum_view .forum_detail .forum_meta {
  width: 100%;
  margin: 10px 0;
  padding: 10px;
  background: #f5f5f5;
  border-radius: 4px;
}
.forum_view .forum_detail .forum_meta span {
  margin-right: 20px;
  font-size: 14px;
}
/*食材明细*/
.forum_view .forum_detail .forum_ingredients {
  margin-top: 20px;
}
.forum_view .forum_detail .forum_ingredients h3 {
  margin-bottom: 10px;
  font-size: 16px;
  font-weight: bold;
}
/*热量显示*/
.forum_view .forum_detail .forum_info .forum_calories {
  color: rgb(255, 102, 102);
  margin-left: 10px;
}

/*行为按钮*/
.forum_view .forum_detail .forum_actions {
  margin: 20px 0;
  text-align: center;
}

.forum_view .forum_detail .forum_actions .el-button {
  margin: 0 10px;
}

.forum_view .thumbs_view {
  margin: 20px auto 0;
}

/*一级评论 盒子*/
.forum_view .forum_comment_box {
  margin: 40px 0px 0px;
  width: 100%;
  padding: 0px;
  border: 0px solid rgb(238, 238, 238);
  box-shadow: none;
  border-radius: 0px;
}
.forum_view .forum_comment_box .el-card__body {
  padding: 20px;
}
/*列表盒子*/
.forum_view .forum_comment_box .forum_comment_list {
  width: 100%;
  text-align: left;
}
/*item*/
.forum_view .forum_comment_box .forum_comment_list .forum_comment {
  width: 100%;
  padding: 0px;
  border: 1px solid rgb(228, 228, 228);
  margin: 0px 0px 20px;
}
/*用户盒子*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_user {
  display: flex;
  align-items: center;
  background: none;
  padding: 5px 10px;
}
/*头像*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_user
  .forum_comment_user_avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin: 0px 10px 0px 0px;
}
/*用户名*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_user
  .forum_comment_username {
  font-size: 16px;
  color: #333;
}
/*内容*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_content {
  width: calc(100% - 60px);
  margin: 0px 0px 0px 60px;
  padding: 10px 0px 0px;
  font-size: 16px;
}
/*按钮盒子*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_reply_view {
  width: 100%;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0px 20px 0px 0px;
  box-sizing: border-box;
}
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_reply_view
  .forum_comment_del {
  font-size: inherit;
  color: rgb(255, 255, 255);
  cursor: pointer;
  margin: 0px 0px 0px 10px;
  background: rgb(251, 137, 122);
  padding: 5px 10px;
}
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_reply_view
  .forum_comment_reply {
  font-size: inherit;
  color: rgb(255, 255, 255);
  cursor: pointer;
  margin: 0px 0px 0px 10px;
  background: var(--theme);
  padding: 5px 10px;
}

/*二级评论 盒子*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_second_list {
  width: calc(100% - 60px);
  margin: 0px 0px 0px 60px;
  padding: 20px 0px 0px;
}
/*线*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_second_list
  .forum_comment_second_divider {
  display: block;
  height: 1px;
  width: 100%;
  margin: 24px 0;
}
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_second_list
  .forum_comment_second_divider
  .el-divider__text {
  font-size: 16px;
}
/*item*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_second_list
  .forum_comment_second {
  width: 100%;
  padding: 10px 0px;
  border-width: 0px 0px 1px;
  border-style: solid;
  border-color: rgb(238, 238, 238);
  border-image: initial;
}
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_second_list
  .forum_comment_second:last-child {
  border-width: 0px 0px 0px;
}

/*用户盒子*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_second_list
  .forum_comment_second
  .forum_comment_second_user {
  display: flex;
  align-items: center;
}
/*头像*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_second_list
  .forum_comment_second
  .forum_comment_second_user
  .forum_comment_second_user_avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  margin: 0px 10px 0px 0px;
}
/*用户名*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_second_list
  .forum_comment_second
  .forum_comment_second_user
  .forum_comment_second_username {
  font-size: 16px;
}
/*内容*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_second_list
  .forum_comment_second
  .forum_comment_second_content {
  width: calc(100% - 60px);
  margin: 0px 0px 0px 60px;
  padding: 10px 0px 0px;
  color: #999;
  font-size: 16px;
}
/*按钮盒子*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_second_list
  .forum_comment_second
  .forum_comment_second_reply_view {
  width: 100%;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0px 20px 0px 0px;
  box-sizing: border-box;
}
/*删除*/
.forum_view
  .forum_comment_box
  .forum_comment_list
  .forum_comment
  .forum_comment_second_list
  .forum_comment_second
  .forum_comment_second_reply_view
  .forum_comment_second_del {
  font-size: inherit;
  color: rgb(255, 255, 255);
  cursor: pointer;
  margin: 0px 0px 0px 10px;
  background: rgb(251, 137, 122);
  padding: 5px 10px;
}
</style>
