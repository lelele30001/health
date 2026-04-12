<template>
  <div
    class="forum_item"
    @mouseenter="$emit('mouseenter', index)"
    @mouseleave="$emit('mouseleave')"
    @click.stop="$emit('click', item.refid || item.id)"
  >
    <div class="forum_item_left">
      <span class="forum_item_title">{{ item.title }}</span>
      <span class="forum_item_name"> (发布人：{{ item.username }}) </span>
      <span class="forum_item_calories">
        (热量：{{ item.totalCalories }}kcal)
      </span>
    </div>
    <div
      class="forum_item_btn_box"
      v-if="
        forumShowIndex == index &&
        (btnAuth('forum', '修改') || btnAuth('forum', '删除'))
      "
    >
      <el-button
        class="forum_edit_btn"
        v-if="btnAuth('forum', '修改')"
        type="primary"
        @click.stop="$emit('edit', item.id)"
        >修改</el-button
      >
      <el-button
        class="forum_del_btn"
        v-if="btnAuth('forum', '删除')"
        type="danger"
        @click.stop="$emit('del', item.id)"
        >删除</el-button
      >
    </div>
    <div class="forum_time" v-else>{{ item.addtime }}</div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from "vue";

const props = defineProps({
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
});

defineEmits(["mouseenter", "mouseleave", "click", "edit", "del"]);
</script>

<style scoped>
.forum_item {
  display: flex;
  flex-direction: column; /* 从横向改成竖向卡片 */
  padding: 20px;
  border-radius: 10px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.2s ease;
  height: 100%; /* 让卡片高度自适应 */
}

.forum_item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.forum_item_left {
  display: flex;
  flex-direction: column; /* 改成竖排 */
  align-items: flex-start;
  margin-bottom: 10px;
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

.forum_time {
  font-size: 13px;
  color: #999;
  margin-top: auto; /* 时间靠下 */
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