<template>
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="80%" center>
    <div v-if="type === 'recipe'" class="recipe-detail">
      <div class="recipe-header">
        <h2 class="recipe-title">{{ detailForm.title }}</h2>
        <div class="recipe-meta">
          <span class="author"
            >发布者：{{ detailForm.username || "未知" }}</span
          >
          <span class="time"
            >发布时间：{{ formatTime(detailForm.addtime) }}</span
          >
          <el-tag
            :type="getStatusType(detailForm.auditStatus)"
            class="status-tag"
          >
            {{ getStatusText(detailForm.auditStatus) }}
          </el-tag>
        </div>
      </div>
      <div class="recipe-content">
        <div class="recipe-image" v-if="detailForm.cover_image">
          <img :src="detailForm.cover_image" :alt="detailForm.title" />
        </div>
        <div class="recipe-info">
          <div class="info-item">
            <span class="info-label">食谱分类：</span>
            <span class="info-value">{{
              detailForm.category || "未分类"
            }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">准备时间：</span>
            <span class="info-value">{{
              detailForm.preparationTime || "未知"
            }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">烹饪时间：</span>
            <span class="info-value">{{
              detailForm.cookingTime || "未知"
            }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">难度：</span>
            <span class="info-value">{{
              detailForm.difficulty || "未知"
            }}</span>
          </div>
        </div>
        <div class="recipe-ingredients">
          <h3>食材</h3>
          <ul class="ingredients-list">
            <li
              v-for="(ingredient, index) in detailForm.ingredients"
              :key="index"
            >
              {{ ingredient.name }} - {{ ingredient.weight
              }}{{ ingredient.unit }}
            </li>
          </ul>
        </div>
        <div class="recipe-steps">
          <h3>步骤</h3>
          <div class="steps-list">
            <div
              v-for="(step, index) in detailForm.steps"
              :key="index"
              class="step-item"
            >
              <div class="step-number">{{ index + 1 }}</div>
              <div class="step-content">
                <p>{{ step.description }}</p>
                <img
                  v-if="step.image"
                  :src="step.image"
                  :alt="`步骤${index + 1}`"
                />
              </div>
            </div>
          </div>
        </div>
        <div class="recipe-nutrition" v-if="detailForm.nutritionInfo">
          <h3>营养信息</h3>
          <div class="nutrition-info">
            <div class="nutrition-item">
              <span class="nutrition-label">热量：</span>
              <span class="nutrition-value"
                >{{ detailForm.nutritionInfo.calories }} kcal</span
              >
            </div>
            <div class="nutrition-item">
              <span class="nutrition-label">蛋白质：</span>
              <span class="nutrition-value"
                >{{ detailForm.nutritionInfo.protein }} g</span
              >
            </div>
            <div class="nutrition-item">
              <span class="nutrition-label">碳水化合物：</span>
              <span class="nutrition-value"
                >{{ detailForm.nutritionInfo.carbs }} g</span
              >
            </div>
            <div class="nutrition-item">
              <span class="nutrition-label">脂肪：</span>
              <span class="nutrition-value"
                >{{ detailForm.nutritionInfo.fat }} g</span
              >
            </div>
          </div>
        </div>
      </div>
    </div>
    <div v-else-if="type === 'comment'" class="comment-detail">
      <div class="comment-header">
        <div class="comment-meta">
          <span class="author"
            >评论者：{{ detailForm.username || "未知" }}</span
          >
          <span class="time"
            >评论时间：{{ formatTime(detailForm.addtime) }}</span
          >
          <el-tag
            :type="getStatusType(detailForm.auditStatus)"
            class="status-tag"
          >
            {{ getStatusText(detailForm.auditStatus) }}
          </el-tag>
        </div>
      </div>
      <div class="comment-content">
        <p>{{ detailForm.content }}</p>
      </div>
      <div class="comment-recipe" v-if="detailForm.recipeTitle">
        <h4>关联食谱：</h4>
        <p>{{ detailForm.recipeTitle }}</p>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleCancel">取消</el-button>
        <el-button
          type="danger"
          @click="handleLock"
          v-if="detailForm.auditStatus === 'pending'"
        >
          锁定
        </el-button>
        <el-button
          type="primary"
          @click="handleApprove"
          v-if="detailForm.auditStatus === 'pending'"
        >
          通过
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>
<script setup>
import { ref, computed, watch, getCurrentInstance } from "vue";
import { ElMessage } from "element-plus";

const context = getCurrentInstance()?.appContext.config.globalProperties;

const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  detailForm: {
    type: Object,
    default: () => ({}),
  },
  type: {
    type: String,
    default: "recipe",
    validator: (value) => ["recipe", "comment"].includes(value),
  },
});

const emit = defineEmits(["update:visible", "audit-success"]);

const dialogVisible = ref(props.visible);
const dialogTitle = computed(() => {
  return props.type === "recipe" ? "食谱审核详情" : "评论审核详情";
});

watch(
  () => props.visible,
  (newValue) => {
    dialogVisible.value = newValue;
  },
);

watch(
  () => dialogVisible.value,
  (newValue) => {
    emit("update:visible", newValue);
  },
);

const formatTime = (time) => {
  if (!time) return "";
  return new Date(time).toLocaleString();
};

const getStatusType = (status) => {
  switch (status) {
    case "pending":
      return "info";
    case "approved":
      return "success";
    case "locked":
      return "danger";
    default:
      return "";
  }
};

const getStatusText = (status) => {
  switch (status) {
    case "pending":
      return "待审核";
    case "approved":
      return "已通过";
    case "locked":
      return "已锁定";
    default:
      return status;
  }
};

const handleCancel = () => {
  dialogVisible.value = false;
};

const handleApprove = () => {
  // 调用后端API更新状态
  const url =
    props.type === "recipe" ? `/recipe/updateStatus` : `/comment/updateStatus`;

  context
    ?.$http({
      url: url,
      method: "post",
      data: {
        id: props.detailForm.id,
        status: "approved",
      },
    })
    .then((res) => {
      console.log("审核通过成功:", res);

      // 发送消息通知
      sendNotification("approved");

      emit("audit-success", {
        message:
          props.type === "recipe" ? "该食谱已通过审核" : "该评论已通过审核",
        data: {
          id: props.detailForm.id,
          status: "approved",
        },
      });

      dialogVisible.value = false;
    })
    .catch((error) => {
      console.error("审核通过失败:", error);
      ElMessage.error("审核操作失败，请重试");
    });
};

const handleLock = () => {
  // 调用后端API更新状态
  const url =
    props.type === "recipe" ? `/recipe/updateStatus` : `/comment/updateStatus`;

  context
    ?.$http({
      url: url,
      method: "post",
      data: {
        id: props.detailForm.id,
        status: "locked",
      },
    })
    .then((res) => {
      console.log("锁定成功:", res);

      // 发送消息通知
      sendNotification("locked");

      emit("audit-success", {
        message: props.type === "recipe" ? "该食谱已被锁定" : "该评论已被锁定",
        data: {
          id: props.detailForm.id,
          status: "locked",
        },
      });

      dialogVisible.value = false;
    })
    .catch((error) => {
      console.error("锁定失败:", error);
      ElMessage.error("审核操作失败，请重试");
    });
};

const sendNotification = (status) => {
  // 发送通知
  const content =
    status === "approved"
      ? props.type === "recipe"
        ? `你的食谱《${props.detailForm.title}》已通过审核`
        : "你的评论已通过审核"
      : props.type === "recipe"
      ? `你的食谱《${props.detailForm.title}》被锁定`
      : "你的评论因违规被锁定";

  console.log("发送通知:", {
    userId: props.detailForm.userId || 1,
    type: "audit",
    content: content,
    relatedId: props.detailForm.id,
    status: status,
  });

  // 调用后端API发送通知
  context
    ?.$http({
      url: `/message/send`,
      method: "post",
      data: {
        userId: props.detailForm.userId || 1,
        type: 1, // 1=审核通知
        content: content,
        relatedId: props.detailForm.id,
      },
    })
    .then((res) => {
      console.log("通知发送成功:", res);
    })
    .catch((error) => {
      console.error("通知发送失败:", error);
    });
};
</script>
<style lang="scss" scoped>
.recipe-detail {
  .recipe-header {
    margin-bottom: var(--spacing-lg);
    padding-bottom: var(--spacing-md);
    border-bottom: 1px solid var(--border-color);

    .recipe-title {
      font-size: var(--font-size-xl);
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: var(--spacing-md);
    }

    .recipe-meta {
      display: flex;
      align-items: center;
      gap: var(--spacing-lg);
      font-size: var(--font-size-sm);
      color: var(--text-secondary);

      .author,
      .time {
        display: flex;
        align-items: center;
      }

      .status-tag {
        margin-left: auto;
      }
    }
  }

  .recipe-content {
    .recipe-image {
      margin-bottom: var(--spacing-lg);

      img {
        width: 100%;
        max-height: 400px;
        object-fit: cover;
        border-radius: var(--border-radius-md);
      }
    }

    .recipe-info {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: var(--spacing-md);
      margin-bottom: var(--spacing-lg);
      padding: var(--spacing-lg);
      background-color: var(--card-bg);
      border-radius: var(--border-radius-md);

      .info-item {
        display: flex;
        flex-direction: column;

        .info-label {
          font-size: var(--font-size-sm);
          color: var(--text-secondary);
          margin-bottom: 4px;
        }

        .info-value {
          font-size: var(--font-size-md);
          color: var(--text-primary);
          font-weight: 500;
        }
      }
    }

    .recipe-ingredients,
    .recipe-steps,
    .recipe-nutrition {
      margin-bottom: var(--spacing-lg);

      h3 {
        font-size: var(--font-size-lg);
        font-weight: 600;
        color: var(--text-primary);
        margin-bottom: var(--spacing-md);
        padding-bottom: 8px;
        border-bottom: 1px solid var(--border-color);
      }
    }

    .ingredients-list {
      list-style: none;
      padding: 0;
      margin: 0;

      li {
        padding: 8px 0;
        border-bottom: 1px solid var(--border-color);
        display: flex;
        align-items: center;
      }
    }

    .steps-list {
      .step-item {
        display: flex;
        gap: var(--spacing-md);
        margin-bottom: var(--spacing-lg);

        .step-number {
          width: 32px;
          height: 32px;
          border-radius: 50%;
          background-color: var(--primary-color);
          color: white;
          display: flex;
          align-items: center;
          justify-content: center;
          font-weight: 600;
          flex-shrink: 0;
        }

        .step-content {
          flex: 1;

          p {
            margin-bottom: var(--spacing-sm);
            line-height: 1.5;
          }

          img {
            width: 100%;
            max-height: 300px;
            object-fit: cover;
            border-radius: var(--border-radius-md);
          }
        }
      }
    }

    .nutrition-info {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
      gap: var(--spacing-md);
      padding: var(--spacing-lg);
      background-color: var(--card-bg);
      border-radius: var(--border-radius-md);

      .nutrition-item {
        display: flex;
        flex-direction: column;

        .nutrition-label {
          font-size: var(--font-size-sm);
          color: var(--text-secondary);
          margin-bottom: 4px;
        }

        .nutrition-value {
          font-size: var(--font-size-md);
          color: var(--text-primary);
          font-weight: 500;
        }
      }
    }
  }
}

.comment-detail {
  .comment-header {
    margin-bottom: var(--spacing-lg);
    padding-bottom: var(--spacing-md);
    border-bottom: 1px solid var(--border-color);

    .comment-meta {
      display: flex;
      align-items: center;
      gap: var(--spacing-lg);
      font-size: var(--font-size-sm);
      color: var(--text-secondary);

      .author,
      .time {
        display: flex;
        align-items: center;
      }

      .status-tag {
        margin-left: auto;
      }
    }
  }

  .comment-content {
    margin-bottom: var(--spacing-lg);
    padding: var(--spacing-lg);
    background-color: var(--card-bg);
    border-radius: var(--border-radius-md);
    line-height: 1.6;
  }

  .comment-recipe {
    padding: var(--spacing-lg);
    background-color: var(--card-bg);
    border-radius: var(--border-radius-md);

    h4 {
      font-size: var(--font-size-md);
      font-weight: 500;
      color: var(--text-primary);
      margin-bottom: var(--spacing-sm);
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
}

@media (max-width: 768px) {
  .recipe-detail {
    .recipe-info {
      grid-template-columns: 1fr;
    }

    .nutrition-info {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}
</style>
