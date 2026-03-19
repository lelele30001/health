<template>
  <el-dialog
    :model-value="visible"
    @update:model-value="$emit('update:visible', $event)"
    :title="''"
    width="60%"
    destroy-on-close
    custom-class="modern-dialog"
  >
    <div class="forum_detail">
      <!-- 标题区 -->
      <div class="forum_title">
        <h1>{{ detailForm.title }}</h1>
        <div class="title-divider"></div>
      </div>

      <!-- 基本信息区 -->
      <div class="forum_info">
        <div class="info-tag">
          <span class="tag-value">{{ detailForm.username }}</span>
        </div>
        <div class="info-tag">
          <span class="tag-value">{{ detailForm.addtime }}</span>
        </div>
        <div class="info-tag calories-tag">
          <span class="tag-value">{{ detailForm.totalCalories }}kcal</span>
        </div>
        <span
          v-if="detailForm.userId == userid && btnAuth('forum', '删除')"
          class="forum_delete"
        >
          <el-button
            type="danger"
            size="small"
            @click="$emit('del', detailForm.id)"
            class="delete-btn"
            >删除</el-button
          >
        </span>
      </div>

      <!-- 操作按钮区 -->
      <div class="forum_actions">
        <el-button
          :type="isLiked ? 'primary' : 'default'"
          size="small"
          @click="recordBehavior('like')"
          class="action-btn"
          :class="{ active: isLiked }"
        >
          <el-icon class="action-icon">
            <Thumb />
          </el-icon>
          <span>点赞</span>
          <span class="action-count">({{ likeCount }})</span>
        </el-button>
        <el-button
          :type="isCollected ? 'primary' : 'default'"
          size="small"
          @click="recordBehavior('collect')"
          class="action-btn"
          :class="{ active: isCollected }"
        >
          <el-icon class="action-icon">
            <Star />
          </el-icon>
          <span>收藏</span>
          <span class="action-count">({{ collectCount }})</span>
        </el-button>
        <el-button
          :type="isDisliked ? 'primary' : 'default'"
          size="small"
          @click="recordBehavior('dislike')"
          class="action-btn"
          :class="{ active: isDisliked }"
        >
          <el-icon class="action-icon">
            <Close />
          </el-icon>
          <span>踩</span>
          <span class="action-count">({{ dislikeCount }})</span>
        </el-button>
        <el-button
          type="warning"
          size="small"
          @click="goToJD"
          class="action-btn"
        >
          <el-icon class="action-icon">
            <ShoppingCart />
          </el-icon>
          <span>去京东购买食材</span>
        </el-button>
      </div>

      <!-- 元信息卡片 -->
      <div class="forum_meta">
        <div class="meta-item">
          <span class="meta-label">健身目标</span>
          <span class="meta-value">{{ detailForm.fitnessGoal }}</span>
        </div>
        <div class="meta-item">
          <span class="meta-label">用餐场景</span>
          <span class="meta-value">{{ detailForm.mealScene }}</span>
        </div>
        <div class="meta-item">
          <span class="meta-label">饮食禁忌</span>
          <span class="meta-value">{{
            detailForm.dietaryRestrictions || "无"
          }}</span>
        </div>
      </div>

      <!-- 正文内容 -->
      <div class="forum_content">
        {{ detailForm.description }}
      </div>

      <!-- 食材表格 -->
      <div class="forum_ingredients">
        <h3>食材明细</h3>
        <div class="ingredients-card">
          <el-table
            :data="detailForm.ingredients"
            style="width: 100%"
            class="modern-table"
            :header-cell-style="{
              backgroundColor: '#f9f9f9',
              fontWeight: '600',
            }"
            :cell-style="{ borderColor: '#e8e8e8' }"
          >
            <el-table-column
              prop="ingredientName"
              label="食材名称"
            ></el-table-column>
            <el-table-column prop="weight" label="用量"></el-table-column>
            <el-table-column
              prop="calories"
              label="热量(kcal)"
            ></el-table-column>
          </el-table>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="forum_comment_box">
        <h3 class="comment-title">评论</h3>
        <div class="comment_add_box">
          <el-input
            v-model="commentContent"
            type="textarea"
            :rows="3"
            placeholder="分享你的看法..."
            class="comment-input"
          ></el-input>
          <div class="comment-btn-container">
            <el-button
              class="comment_add"
              type="primary"
              @click="addComment"
              :disabled="!commentContent.trim()"
            >
              发表评论
            </el-button>
          </div>
        </div>
        <div class="comment_list">
          <div
            v-for="(comment, index) in detailForm.comments || []"
            :key="index"
            class="forum_comment_item"
          >
            <div class="forum_comment_header">
              <span class="forum_comment_username">{{ comment.username }}</span>
              <span class="forum_comment_time">{{ comment.addtime }}</span>
              <span
                v-if="comment.userId == userid && btnAuth('forum', '删除')"
                class="forum_comment_del"
              >
                <el-button
                  type="danger"
                  size="small"
                  @click="delComment(comment.id)"
                  class="delete-btn"
                  >删除</el-button
                >
              </span>
            </div>
            <div class="forum_comment_content">{{ comment.content }}</div>
          </div>
          <div
            v-if="!detailForm.comments || detailForm.comments.length === 0"
            class="no-comments"
          >
            暂无评论，快来发表你的看法吧！
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { Thumb, Star, Close, ShoppingCart } from "@element-plus/icons-vue";

export default {
  name: "ForumDetail",
  components: {
    Thumb,
    Star,
    Close,
    ShoppingCart,
  },
  props: {
    visible: {
      type: Boolean,
      default: false,
    },
    detailForm: {
      type: Object,
      default: () => ({}),
    },
    userid: {
      type: Number,
      required: true,
    },
    likeCount: {
      type: Number,
      default: 0,
    },
    collectCount: {
      type: Number,
      default: 0,
    },
    dislikeCount: {
      type: Number,
      default: 0,
    },
    isLiked: {
      type: Boolean,
      default: false,
    },
    isCollected: {
      type: Boolean,
      default: false,
    },
    isDisliked: {
      type: Boolean,
      default: false,
    },
    btnAuth: {
      type: Function,
      required: true,
    },
  },
  data() {
    return {
      commentContent: "",
      replyContent: "",
      replyIndex: -1,
    };
  },
  methods: {
    recordBehavior(type) {
      this.$emit("recordBehavior", type);
    },
    addComment() {
      this.$emit("addComment", this.commentContent);
      this.commentContent = "";
    },
    delComment(id) {
      this.$emit("delComment", id);
    },
    showReplyInput(index) {
      this.replyIndex = index;
    },
    addReply(commentId) {
      this.$emit("addReply", commentId, this.replyContent);
      this.replyContent = "";
      this.replyIndex = -1;
    },
    goToJD() {
      window.open("https://www.jd.com/", "_blank");
    },
  },
};
</script>

<style scoped>
/* 整体弹窗样式 */
.modern-dialog {
  border-radius: 16px !important;
  overflow: hidden;
}

.modern-dialog .el-dialog__header {
  padding: 0;
  border-bottom: none;
}

.modern-dialog .el-dialog__body {
  padding: 0;
  background: #f8f9fa;
}

/* 整体弹窗内容 */
.forum_detail {
  width: 100%;
  padding: 30px;
  background: #f8f9fa;
  border-radius: 16px;
}

/* 标题 */
.forum_title {
  text-align: center;
  margin-bottom: 24px;
  position: relative;
}

.forum_title h1 {
  font-size: 26px;
  font-weight: 700;
  color: #333;
  margin: 0 0 12px 0;
  line-height: 1.3;
}

.title-divider {
  width: 60px;
  height: 3px;
  background: linear-gradient(90deg, #ee6723, #03cc88);
  border-radius: 2px;
  margin: 0 auto;
}

/* 基本信息行 */
.forum_info {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;
  padding: 0 20px;
}

.info-tag {
  background: #f5f5f5;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 14px;
  color: #666;
  display: inline-flex;
  align-items: center;
}

.calories-tag {
  background: rgba(238, 103, 35, 0.1);
  color: #ee6723;
  font-weight: 500;
}

/* 点赞收藏按钮区 */
.forum_actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 0 0 24px 0;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.action-btn.active {
  background: #ee6723 !important;
  border-color: #ee6723 !important;
  color: white !important;
}

.action-icon {
  font-size: 16px;
}

.action-count {
  font-size: 13px;
  opacity: 0.8;
}

/* 元信息卡片 */
.forum_meta {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
  font-size: 14px;
  color: #555;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-label {
  color: #999;
  font-size: 13px;
}

.meta-value {
  font-weight: 500;
  color: #333;
}

/* 正文内容 */
.forum_content {
  font-size: 16px;
  color: #444;
  line-height: 1.8;
  margin-bottom: 24px;
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

/* 食材表格 */
.forum_ingredients {
  margin-bottom: 32px;
}

.forum_ingredients h3 {
  font-size: 18px;
  margin-bottom: 16px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.ingredients-card {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.modern-table {
  border-radius: 8px;
  overflow: hidden;
}

.modern-table .el-table__header-wrapper {
  border-bottom: 1px solid #e8e8e8;
}

.modern-table .el-table__row {
  transition: background-color 0.2s ease;
}

.modern-table .el-table__row:hover {
  background-color: #f9f9f9 !important;
}

/* 评论区 */
.forum_comment_box {
  margin-top: 32px;
}

.comment-title {
  font-size: 18px;
  margin-bottom: 20px;
  font-weight: 600;
  color: #333;
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment_add_box {
  margin-bottom: 24px;
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.comment-input {
  margin-bottom: 12px;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
  transition: all 0.3s ease;
}

.comment-input:focus {
  border-color: #03cc88;
  box-shadow: 0 0 0 2px rgba(3, 204, 136, 0.1);
}

.comment-btn-container {
  display: flex;
  justify-content: flex-end;
}

.comment_add {
  background: #03cc88 !important;
  border-color: #03cc88 !important;
  border-radius: 8px;
  padding: 8px 24px;
  transition: all 0.3s ease;
}

.comment_add:hover {
  background: #02b37a !important;
  border-color: #02b37a !important;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(3, 204, 136, 0.3);
}

.comment_list {
  margin-top: 0;
}

/* 一级评论 */
.forum_comment_item {
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.forum_comment_item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}

.forum_comment_header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.forum_comment_username {
  font-weight: 600;
  color: #333;
  font-size: 15px;
}

.forum_comment_time {
  font-size: 13px;
  color: #999;
}

/* 评论内容 */
.forum_comment_content {
  margin-bottom: 0;
  line-height: 1.7;
  color: #444;
  font-size: 15px;
}

.no-comments {
  text-align: center;
  padding: 40px 20px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  color: #999;
  font-size: 14px;
}

/* 删除按钮 */
.delete-btn {
  border-radius: 6px;
  font-size: 12px;
  padding: 4px 12px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .forum_detail {
    padding: 20px;
  }

  .forum_title h1 {
    font-size: 22px;
  }

  .forum_info {
    padding: 0;
  }

  .forum_meta {
    gap: 16px;
    padding: 16px;
  }

  .forum_content {
    padding: 20px;
  }

  .ingredients-card {
    padding: 16px;
  }

  .comment_add_box {
    padding: 16px;
  }

  .forum_comment_item {
    padding: 16px;
  }
}
</style>
