<template>
  <div
    class="forum_item"
    :class="[getStatusClass(item.status)]"
    @click.stop="$emit('click', item.refid || item.id)"
  >
    <div class="forum_item_content">
      <div class="forum_item_left">
        <span class="forum_item_title">{{ item.title }}</span>
        <span class="forum_item_name"> (发布人：{{ item.username }}) </span>
        <span class="forum_item_calories">
          (热量：{{ item.totalCalories }}kcal)
        </span>
        <span class="forum_item_nutrition" v-if="item.nutritionTag">
          (营养标签：{{ item.nutritionTag }})
        </span>
        <div class="forum_time">{{ formatDate(item.addtime) }}</div>
        <div
          class="forum_status"
          v-if="item.status && item.status !== 'approved'"
        >
          {{ getStatusText(item.status) }}
        </div>
      </div>
      <div class="forum_item_right">
        <img
          v-if="item.cover_image"
          :src="getImageUrl(item.cover_image)"
          class="forum_item_image"
          alt="封面图片"
        />
        <div v-else class="forum_item_image_placeholder">无封面图片</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "ForumItem",
  props: {
    item: {
      type: Object,
      required: true,
    },
    index: {
      type: Number,
      required: true,
    },
    userid: {
      type: Number,
      required: true,
    },
    forumShowIndex: {
      type: Number,
      default: -1,
    },
    btnAuth: {
      type: Function,
      required: true,
    },
  },
  methods: {
    getImageUrl(coverImage) {
      if (!coverImage) return "";
      // 如果是完整的URL，直接使用
      if (coverImage.startsWith("http")) {
        return coverImage;
      }
      // 处理不同类型的路径
      if (coverImage.startsWith("/recipe_pic")) {
        // 对于recipe_pic路径，直接使用相对路径
        return coverImage;
      } else {
        // 对于file路径，使用完整的后端服务器路径
        return `http://localhost:8080/project/${coverImage}`;
      }
    },
    formatDate(timestamp) {
      if (!timestamp) return "";
      // 处理时间戳格式
      if (typeof timestamp === "number") {
        const date = new Date(timestamp);
        return this.formatDateObj(date);
      }
      // 处理字符串格式
      if (typeof timestamp === "string") {
        // 如果是纯数字字符串，按时间戳处理
        if (/^\d+$/.test(timestamp)) {
          const date = new Date(parseInt(timestamp));
          return this.formatDateObj(date);
        }
        // 如果是日期字符串，直接返回
        return timestamp;
      }
      return "";
    },
    formatDateObj(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");
      const seconds = String(date.getSeconds()).padStart(2, "0");
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    },
    getStatusClass(status) {
      if (status === "pending") {
        return "forum_item_pending";
      } else if (status === "locked") {
        return "forum_item_locked";
      }
      return "";
    },
    getStatusText(status) {
      if (status === "pending") {
        return "待审核";
      } else if (status === "locked") {
        return "已锁定";
      }
      return "";
    },
  },
};
</script>

<style scoped>
.forum_item {
  padding: 20px;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.2s ease;
  height: 100%; /* 让卡片高度自适应 */
  border: 2px solid transparent;
}

/* 待审核状态 */
.forum_item_pending {
  border-color: #999;
  background-color: rgba(153, 153, 153, 0.05);
}

/* 锁定状态 */
.forum_item_locked {
  border-color: #ff4d4f;
  background-color: rgba(255, 77, 79, 0.05);
}

/* 状态标签 */
.forum_status {
  margin-top: 10px;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.forum_item_pending .forum_status {
  background-color: #999;
  color: #fff;
}

.forum_item_locked .forum_status {
  background-color: #ff4d4f;
  color: #fff;
}

.forum_item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.forum_item_content {
  display: flex;
  gap: 20px;
  align-items: center;
}

.forum_item_left {
  flex: 1;
  display: flex;
  flex-direction: column; /* 改成竖排 */
  align-items: flex-start;
}

.forum_item_title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
}

.forum_item_name,
.forum_item_calories {
  font-size: 14px;
  color: #777;
  margin-bottom: 4px;
}

.forum_item_calories {
  color: #ff6666;
}

.forum_item_nutrition {
  font-size: 14px;
  color: #4caf50;
  margin-bottom: 4px;
}

.forum_time {
  font-size: 13px;
  color: #999;
  margin-top: 10px;
}

.forum_item_right {
  width: 120px;
  height: 90px;
  flex-shrink: 0;
}

.forum_item_image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.forum_item_image_placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 8px;
  font-size: 12px;
  color: #999;
}

.forum_item_btn_box {
  margin-top: 10px;
  display: flex;
  gap: 10px;
}

.forum_edit_btn {
  margin: 0;
  padding: 0px 24px;
  width: auto;
  height: 40px;
  font-size: 14px;
  color: rgb(255, 255, 255);
  border-radius: 4px;
  border: 0px;
  background: rgba(254, 176, 67, 0.8);
  cursor: pointer;
}

.forum_del_btn {
  margin: 0;
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
</style>
