<template>
  <div class="container">
    <!-- 用户发布的食谱帖子 -->
    <section class="section">
      <h2 class="section-title">我的食谱帖子</h2>
      <div class="grid">
        <div
          v-for="(item, index) in userRecipes"
          :key="index"
          class="card recipe-card"
          @click="openRecipeDetail(item.id)"
        >
          <div class="card-image">
            <img
              v-if="item.coverImage && isHttp(item.coverImage)"
              :src="item.coverImage"
              alt="{{ item.title }}"
              class="card-img"
            />
            <img
              v-else-if="item.coverImage"
              :src="context.$config.url + item.coverImage"
              alt="{{ item.title }}"
              class="card-img"
            />
            <div v-else class="card-image-placeholder">
              <span class="placeholder-text">{{ item.title }}</span>
            </div>
          </div>
          <div class="card-content">
            <h3 class="card-title">{{ item.title }}</h3>
            <div class="card-stats">
              <div class="stat-item">
                <span class="stat-icon">❤️</span>
                <span class="stat-value">{{ item.likeCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <span class="stat-icon">⭐</span>
                <span class="stat-value">{{ item.storeupNumber || 0 }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div class="section-more" @click="moreClick('forum')">
        <span>查看更多帖子 →</span>
      </div>
    </section>

    <!-- 食谱详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      :title="detailForm.title || '食谱详情'"
      width="80%"
      custom-class="recipe-detail-dialog"
      destroy-on-close
    >
      <div class="recipe-detail-content">
        <div v-if="detailForm.coverImage" class="recipe-detail-image">
          <img
            v-if="isHttp(detailForm.coverImage)"
            :src="detailForm.coverImage"
            alt="{{ detailForm.title }}"
            class="detail-img"
          />
          <img
            v-else
            :src="context.$config.url + detailForm.coverImage"
            alt="{{ detailForm.title }}"
            class="detail-img"
          />
        </div>
        <div class="recipe-detail-meta">
          <div class="meta-item">
            <span class="meta-label">热量：</span>
            <span class="meta-value"
              >{{ detailForm.totalCalories || 0 }}kcal</span
            >
          </div>
          <div class="meta-item">
            <span class="meta-label">健身目标：</span>
            <span class="meta-value">{{
              detailForm.fitnessGoal || "未设置"
            }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">用餐场景：</span>
            <span class="meta-value">{{
              detailForm.mealScene || "未设置"
            }}</span>
          </div>
          <div class="meta-item">
            <span class="meta-label">饮食禁忌：</span>
            <span class="meta-value">{{
              detailForm.dietaryRestrictions || "无"
            }}</span>
          </div>
        </div>
        <el-divider />
        <div class="recipe-detail-description">
          <h3>食谱描述</h3>
          <p>{{ detailForm.description || "暂无描述" }}</p>
        </div>
        <el-divider />
        <div class="recipe-detail-ingredients">
          <h3>食材明细</h3>
          <el-table :data="detailForm.ingredients || []" style="width: 100%">
            <el-table-column
              prop="ingredientName"
              label="食材名称"
            ></el-table-column>
            <el-table-column prop="weight" label="重量"></el-table-column>
            <el-table-column prop="unit" label="单位"></el-table-column>
            <el-table-column
              prop="calories"
              label="热量(kcal)"
            ></el-table-column>
          </el-table>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, getCurrentInstance } from "vue";
import { useRouter } from "vue-router";
const context = getCurrentInstance()?.appContext.config.globalProperties;
const router = useRouter();

// 用户发布的食谱帖子
const userRecipes = ref([]);
const getuserRecipes = () => {
  const userId = context?.$toolUtil.storageGet("userid");
  if (userId) {
    context
      ?.$http({
        url: "recipe/user/" + userId,
        method: "get",
        params: {
          page: 1,
          limit: 9, // 一行三个，显示三行
        },
      })
      .then((res) => {
        if (res?.data?.recipes) {
          userRecipes.value = res.data.recipes;
        } else if (res?.data?.data?.recipes) {
          userRecipes.value = res.data.data.recipes;
        }
      });
  } else {
    // 如果未登录，获取公开食谱
    context
      ?.$http({
        url: "recipe/public",
        method: "get",
        params: {
          page: 1,
          limit: 9,
        },
      })
      .then((res) => {
        if (res?.data?.recipes) {
          userRecipes.value = res.data.recipes;
        } else if (res?.data?.data?.recipes) {
          userRecipes.value = res.data.data.recipes;
        }
      });
  }
};

// 判断图片链接是否带http
const isHttp = (str) => {
  return str && str.substr(0, 4) == "http";
};

// 详情弹窗相关
const detailVisible = ref(false);
const detailForm = ref({});

// 打开食谱详情弹窗
const openRecipeDetail = (id) => {
  if (id) {
    context
      ?.$http({
        url: `recipe/info/${id}`,
        method: "get",
      })
      .then((res) => {
        if (res && res.data) {
          if (res.data.recipe) {
            detailForm.value = res.data.recipe;
            detailVisible.value = true;
          } else if (res.data.data && res.data.data.recipe) {
            detailForm.value = res.data.data.recipe;
            detailVisible.value = true;
          }
        }
      });
  }
};

// 查看更多
const moreClick = (table) => {
  router.push(`/index/${table}List`);
};

const init = () => {
  // 获取用户食谱帖子
  getuserRecipes();
};

init();
</script>

<style lang="scss">
@import "@/styles/variables.css";

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: var(--spacing-xl) var(--spacing-lg);
}

.section {
  margin-bottom: var(--spacing-xxl);
}

.section-title {
  font-size: var(--font-size-xxl);
  font-weight: 700;
  color: var(--text-color);
  margin-bottom: var(--spacing-lg);
  position: relative;
  padding-bottom: var(--spacing-md);

  &:after {
    content: "";
    position: absolute;
    bottom: 0;
    left: 0;
    width: 60px;
    height: 4px;
    background-color: var(--primary-color);
    border-radius: var(--border-radius-full);
  }
}

.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.card {
  background-color: var(--card-background);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow);
  overflow: hidden;
  transition: var(--transition);
  cursor: pointer;

  &:hover {
    box-shadow: var(--shadow-hover);
    transform: translateY(-4px);
  }
}

.card-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  position: relative;

  .card-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: var(--transition);

    .card:hover & {
      transform: scale(1.05);
    }
  }

  .card-image-placeholder {
    width: 100%;
    height: 100%;
    background-color: var(--secondary-color);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: var(--spacing-md);
    text-align: center;
    border: 2px dashed var(--border-color);
    transition: var(--transition);

    .card:hover & {
      background-color: #f0f0f0;
      border-color: var(--primary-color);
    }

    .placeholder-text {
      font-size: var(--font-size-md);
      font-weight: 600;
      color: var(--text-secondary);
      line-height: 1.4;
      word-break: break-word;
    }
  }
}

.card-content {
  padding: var(--spacing-lg);
}

.card-title {
  font-size: var(--font-size-lg);
  font-weight: 600;
  color: var(--text-color);
  margin-bottom: var(--spacing-md);
  line-height: 1.3;
}

.card-description {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  line-height: 1.5;
  margin-bottom: var(--spacing-md);
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.card-meta {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-md);

  .meta-item {
    font-size: var(--font-size-xs);
    color: var(--text-light);
    background-color: var(--secondary-color);
    padding: var(--spacing-xs) var(--spacing-sm);
    border-radius: var(--border-radius-full);
  }
}

.card-stats {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;

  .stat-item {
    display: flex;
    align-items: center;
    gap: var(--spacing-xs);
    font-size: var(--font-size-sm);
    color: var(--text-secondary);

    .stat-icon {
      font-size: var(--font-size-md);
    }
  }
}

.card-date {
  font-size: var(--font-size-xs);
  color: var(--text-light);
  margin-top: var(--spacing-md);
  text-align: right;
}

.section-more {
  text-align: center;
  margin-top: var(--spacing-lg);

  span {
    display: inline-block;
    font-size: var(--font-size-md);
    color: var(--primary-color);
    font-weight: 600;
    transition: var(--transition);

    &:hover {
      transform: translateX(5px);
    }
  }
}

@media (max-width: 768px) {
  .grid {
    grid-template-columns: 1fr;
  }

  .section-title {
    font-size: var(--font-size-xl);
  }

  .container {
    padding: var(--spacing-lg) var(--spacing-md);
  }
}

/* 食谱详情弹窗样式 */
.recipe-detail-dialog {
  .recipe-detail-content {
    padding: var(--spacing-lg);
  }

  .recipe-detail-image {
    margin-bottom: var(--spacing-lg);
    border-radius: var(--border-radius-lg);
    overflow: hidden;
    box-shadow: var(--shadow);

    .detail-img {
      width: 100%;
      height: auto;
      max-height: 400px;
      object-fit: cover;
    }
  }

  .recipe-detail-meta {
    display: flex;
    flex-wrap: wrap;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-lg);

    .meta-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-xs);

      .meta-label {
        font-weight: 600;
        color: var(--text-secondary);
      }

      .meta-value {
        color: var(--text-color);
      }
    }
  }

  .recipe-detail-description {
    margin-bottom: var(--spacing-lg);

    h3 {
      font-size: var(--font-size-lg);
      font-weight: 600;
      margin-bottom: var(--spacing-md);
      color: var(--text-color);
    }

    p {
      line-height: 1.6;
      color: var(--text-secondary);
    }
  }

  .recipe-detail-ingredients {
    h3 {
      font-size: var(--font-size-lg);
      font-weight: 600;
      margin-bottom: var(--spacing-md);
      color: var(--text-color);
    }
  }
}
</style>
