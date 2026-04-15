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
        <h1 v-if="!isEditMode">{{ detailForm.title }}</h1>
        <el-input
          v-else
          v-model="editForm.title"
          placeholder="请输入标题"
          class="edit-title-input"
        ></el-input>
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
          v-if="(detailForm.userId == userid || btnAuth('forum', '删除'))"
          class="forum_delete"
        >
          <el-button
            type="primary"
            size="small"
            @click="enterEditMode"
            class="edit-btn"
            v-if="!isEditMode && btnAuth('forum', '修改')"
            >修改</el-button
          >
          <el-button
            type="success"
            size="small"
            @click="saveEdit"
            class="save-btn"
            v-if="isEditMode"
            >保存</el-button
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
            <Star />
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
          <span v-if="!isEditMode" class="meta-value">{{
            detailForm.fitnessGoal
          }}</span>
          <el-select
            v-else
            v-model="editForm.fitnessGoal"
            placeholder="请选择健身目标"
            class="meta-select"
          >
            <el-option label="增肌" value="增肌"></el-option>
            <el-option label="减脂" value="减脂"></el-option>
            <el-option label="维持" value="维持"></el-option>
          </el-select>
        </div>
        <div class="meta-item">
          <span class="meta-label">用餐场景</span>
          <span v-if="!isEditMode" class="meta-value">{{
            detailForm.mealScene
          }}</span>
          <el-select
            v-else
            v-model="editForm.mealScene"
            placeholder="请选择用餐场景"
            class="meta-select"
          >
            <el-option label="早餐" value="早餐"></el-option>
            <el-option label="午餐" value="午餐"></el-option>
            <el-option label="晚餐" value="晚餐"></el-option>
            <el-option label="加餐" value="加餐"></el-option>
          </el-select>
        </div>
        <div class="meta-item">
          <span class="meta-label">饮食禁忌</span>
          <span v-if="!isEditMode" class="meta-value">{{
            detailForm.dietaryRestrictions || "无"
          }}</span>
          <el-input
            v-else
            v-model="editForm.dietaryRestrictions"
            placeholder="请输入饮食禁忌"
            class="meta-input"
          ></el-input>
        </div>
      </div>

      <!-- 正文内容 -->
      <div class="forum_content">
        <span v-if="!isEditMode">{{ detailForm.description }}</span>
        <el-input
          v-else
          v-model="editForm.description"
          type="textarea"
          :rows="4"
          placeholder="请输入正文内容"
          class="content-textarea"
        ></el-input>
      </div>

      <!-- 食材表格 -->
      <div class="forum_ingredients">
        <h3>食材明细</h3>
        <div class="ingredients-card">
          <el-table
            v-if="!isEditMode"
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
          <el-table
            v-else
            :data="editForm.ingredients"
            style="width: 100%"
            class="modern-table"
            :header-cell-style="{
              backgroundColor: '#f9f9f9',
              fontWeight: '600',
            }"
            :cell-style="{ borderColor: '#e8e8e8' }"
          >
            <el-table-column prop="ingredientName" label="食材名称">
              <template #default="scope">
                <el-input
                  v-model="scope.row.ingredientName"
                  placeholder="请输入食材名称"
                ></el-input>
              </template>
            </el-table-column>
            <el-table-column prop="weight" label="用量">
              <template #default="scope">
                <el-input
                  v-model.number="scope.row.weight"
                  type="number"
                  placeholder="请输入用量"
                ></el-input>
              </template>
            </el-table-column>
            <el-table-column prop="calories" label="热量(kcal)">
              <template #default="scope">
                <el-input
                  v-model.number="scope.row.calories"
                  type="number"
                  placeholder="请输入热量"
                ></el-input>
              </template>
            </el-table-column>
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
            v-for="(comment, index) in comments"
            :key="comment.id"
            class="forum_comment_item"
          >
            <div class="forum_comment_header">
              <span class="forum_comment_username">{{ comment.username }}</span>
              <span class="forum_comment_time">{{
                formatDateTime(comment.createTime)
              }}</span>
              <span
                v-if="(comment.userId == userid || btnAuth('forum', '删除'))"
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
          <div v-if="comments.length === 0" class="no-comments">
            暂无评论，快来发表你的看法吧！
          </div>
        </div>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import { Star, Close, ShoppingCart } from "@element-plus/icons-vue";
import toolUtil from "@/utils/toolUtil";
import "@/assets/dialog-styles.css";

export default {
  name: "ForumDetail",
  components: {
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
      isEditMode: false,
      comments: [],
      editForm: {
        title: "",
        description: "",
        fitnessGoal: "",
        mealScene: "",
        dietaryRestrictions: "",
        ingredients: [],
        totalCalories: 0,
      },
    };
  },
  watch: {
    "detailForm.id": {
      handler(newVal) {
        if (newVal) {
          this.getComments();
        }
      },
      immediate: true,
    },
  },
  methods: {
    // 格式化日期时间
    formatDateTime(date) {
      return toolUtil.formatDateTime(date);
    },
    getComments() {
      if (!this.detailForm.id) return;

      this.$http({
        url: `comment/list/${this.detailForm.id}`,
        method: "get",
      })
        .then((res) => {
          if (res.data.code === 0) {
            this.comments = res.data.data || [];
          }
        })
        .catch((error) => {
          console.error("获取评论失败:", error);
        });
    },
    recordBehavior(type) {
      this.$emit("recordBehavior", type);
    },
    addComment() {
      if (!this.commentContent.trim()) return;

      this.$http({
        url: "comment/add",
        method: "post",
        data: {
          postId: this.detailForm.id,
          userId: this.userid,
          username: this.detailForm.username,
          content: this.commentContent,
        },
      })
        .then((res) => {
          if (res.data.code === 0) {
            this.$message.success("评论成功");
            this.commentContent = "";
            this.getComments();
          } else {
            this.$message.error(res.data.msg || "评论失败");
          }
        })
        .catch((error) => {
          console.error("评论失败:", error);
          this.$message.error("评论失败");
        });
    },
    delComment(id) {
      this.$confirm("确定要删除这条评论吗？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          this.$http({
            url: `comment/delete/${id}`,
            method: "delete",
            params: {
              userId: this.userid,
            },
          })
            .then((res) => {
              if (res.data.code === 0) {
                this.$message.success("删除成功");
                this.getComments();
              } else {
                this.$message.error(res.data.msg || "删除失败");
              }
            })
            .catch((error) => {
              console.error("删除失败:", error);
              this.$message.error("删除失败");
            });
        })
        .catch(() => {
          // 取消删除
        });
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
    enterEditMode() {
      // 进入编辑模式，复制当前数据到编辑表单
      this.isEditMode = true;
      this.editForm = {
        title: this.detailForm.title || "",
        description: this.detailForm.description || "",
        fitnessGoal: this.detailForm.fitnessGoal || "",
        mealScene: this.detailForm.mealScene || "",
        dietaryRestrictions: this.detailForm.dietaryRestrictions || "",
        ingredients: JSON.parse(
          JSON.stringify(this.detailForm.ingredients || []),
        ),
        totalCalories: this.detailForm.totalCalories || 0,
      };
    },
    saveEdit() {
      // 保存编辑内容
      this.$emit("update", this.editForm);
      this.isEditMode = false;
    },
  },
};
</script>

<style scoped>
/* 整体弹窗样式 */
.modern-dialog {
  border-radius: 16px !important;
  overflow: hidden;
  max-height: 90vh !important;
  height: 90vh !important;
}

.modern-dialog .el-dialog__header {
  padding: 0;
  border-bottom: none;
  height: 40px;
}

.modern-dialog .el-dialog__body {
  padding: 0;
  background: #f8f9fa;
  max-height: calc(90vh - 40px) !important;
  height: calc(90vh - 40px) !important;
  overflow: hidden !important;
}

/* 整体弹窗内容 */
.forum_detail {
  width: 100%;
  padding: 30px;
  background: #f8f9fa;
  border-radius: 16px;
  height: 100%;
  overflow-y: auto;
  max-height: 100%;
}

/* 覆盖Element UI默认样式 */
:deep(.el-dialog) {
  max-height: 90vh !important;
  height: 90vh !important;
}

:deep(.el-dialog__body) {
  max-height: calc(90vh - 40px) !important;
  height: calc(90vh - 40px) !important;
  overflow: hidden !important;
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

/* 编辑按钮 */
.edit-btn {
  border-radius: 6px;
  font-size: 12px;
  padding: 4px 12px;
  margin-right: 8px;
}

/* 保存按钮 */
.save-btn {
  border-radius: 6px;
  font-size: 12px;
  padding: 4px 12px;
  margin-right: 8px;
}

/* 编辑模式样式 */
.edit-title-input {
  width: 100%;
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 12px;
}

.meta-select {
  width: 120px;
}

.meta-input {
  width: 150px;
}

.content-textarea {
  width: 100%;
  resize: vertical;
}

.ingredients-card .el-input {
  width: 100%;
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