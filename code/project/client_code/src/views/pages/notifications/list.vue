<template>
  <div class="notifications-page">
    <div class="page-header">
      <h1>消息中心</h1>
      <div class="header-actions">
        <el-button v-if="unreadCount > 0" type="primary" @click="markAllAsRead">
          全部标为已读
        </el-button>
      </div>
    </div>
    <div class="tab-container">
      <el-tabs v-model="activeTab" @tab-click="handleTabClick">
        <el-tab-pane label="全部消息" name="all"></el-tab-pane>
        <el-tab-pane label="审核通知" name="audit"></el-tab-pane>
        <el-tab-pane label="互动通知" name="interaction"></el-tab-pane>
      </el-tabs>
    </div>
    <div class="notifications-list">
      <el-skeleton :loading="loading" animated :rows="5" v-if="loading">
        <template #template>
          <el-skeleton-item variant="h3" style="width: 50%"></el-skeleton-item>
          <el-skeleton-item variant="p"></el-skeleton-item>
          <el-skeleton-item variant="p"></el-skeleton-item>
        </template>
      </el-skeleton>
      <div v-else>
        <div
          v-for="(notification, index) in filteredNotifications"
          :key="notification.id"
          class="notification-item"
          :class="{ unread: !notification.read }"
          @click="markAsRead(notification.id)"
        >
          <div class="notification-content">
            <div class="notification-header">
              <h3 class="notification-title">{{ notification.title }}</h3>
              <span class="notification-time">{{
                formatTime(notification.createTime)
              }}</span>
            </div>
            <p class="notification-body">{{ notification.content }}</p>
          </div>
          <div class="notification-actions">
            <el-button size="small" @click.stop="navigateToRelated(notification)">
              查看详情
            </el-button>
          </div>
        </div>
        <div v-if="filteredNotifications.length === 0" class="empty-state">
          <el-empty description="暂无消息"></el-empty>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, computed, onMounted, getCurrentInstance } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";

const router = useRouter();
const activeTab = ref("all");
const notifications = ref([]);
const unreadCount = ref(0);
const loading = ref(false);
const context = getCurrentInstance()?.proxy;
const userid = ref(context?.$toolUtil.storageGet("userid"));

const filteredNotifications = computed(() => {
  if (activeTab.value === "all") {
    return notifications.value;
  } else {
    return notifications.value.filter(
      (notification) => notification.type === activeTab.value,
    );
  }
});

const loadNotifications = () => {
  if (!context || !userid.value) return;

  loading.value = true;
  context
    .$http({
      url: `/message/list`,
      method: "get",
      params: {
        userId: userid.value,
      },
    })
    .then((res) => {
      if (res.data.code === 0) {
        // 处理后端返回的消息数据
        notifications.value = (res.data.list || []).map((item) => ({
          id: item.id,
          title: item.type === 1 ? "审核通知" : "互动通知",
          content: item.content,
          type: item.type === 1 ? "audit" : "interaction",
          relatedId: item.relatedId,
          relatedType: item.type === 1 ? "recipe" : "recipe",
          read: item.status === 1,
          createTime: item.createTime,
        }));
        updateUnreadCount();
      } else {
        ElMessage.error("获取消息失败");
      }
    })
    .catch((error) => {
      console.error("获取消息失败:", error);
      ElMessage.error("获取消息失败");
    })
    .finally(() => {
      loading.value = false;
    });
};

const updateUnreadCount = () => {
  unreadCount.value = notifications.value.filter((n) => !n.read).length;
};

const handleTabClick = (tab) => {
  activeTab.value = tab.props.name;
};

const markAsRead = (id) => {
  if (!context) return;

  context
    .$http({
      url: `/message/read/${id}`,
      method: "post",
    })
    .then((res) => {
      if (res.data.code === 0) {
        const notification = notifications.value.find((n) => n.id === id);
        if (notification) {
          notification.read = true;
          updateUnreadCount();
          ElMessage.success("消息已标为已读");
        }
      } else {
        ElMessage.error("标记已读失败");
      }
    })
    .catch((error) => {
      console.error("标记已读失败:", error);
      ElMessage.error("标记已读失败");
    });
};

const markAllAsRead = () => {
  if (!context || !userid.value) return;

  context
    .$http({
      url: `/message/read/all`,
      method: "post",
      params: {
        userId: userid.value,
      },
    })
    .then((res) => {
      if (res.data.code === 0) {
        notifications.value.forEach((notification) => {
          notification.read = true;
        });
        updateUnreadCount();
        ElMessage.success("所有消息已标为已读");
      } else {
        ElMessage.error("标记全部已读失败");
      }
    })
    .catch((error) => {
      console.error("标记全部已读失败:", error);
      ElMessage.error("标记全部已读失败");
    });
};

const navigateToRelated = (notification) => {
  if (notification.relatedId) {
    router.push(`/index/forumList?id=${notification.relatedId}`);
  }
};

const formatTime = (time) => {
  if (!time) return "";
  return new Date(time).toLocaleString();
};

onMounted(() => {
  loadNotifications();
});
</script>
<style lang="scss" scoped>
.notifications-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: var(--spacing-xl) var(--spacing-lg);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);

  h1 {
    font-size: var(--font-size-xxl);
    font-weight: 600;
    color: var(--text-primary);
    margin: 0;
  }
}

.tab-container {
  margin-bottom: var(--spacing-lg);

  :deep(.el-tabs) {
    .el-tabs__header {
      margin-bottom: var(--spacing-lg);

      .el-tabs__nav {
        border-bottom: 1px solid var(--border-color);

        .el-tabs__item {
          font-size: var(--font-size-md);
          color: var(--text-secondary);
          padding: 10px 20px;
          margin-right: 20px;

          &:hover {
            color: var(--primary-color);
          }

          &.is-active {
            color: var(--primary-color);
            font-weight: 500;
            border-bottom: 2px solid var(--primary-color);
          }
        }
      }
    }
  }
}

.notifications-list {
  .notification-item {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    padding: var(--spacing-lg);
    margin-bottom: var(--spacing-md);
    background-color: var(--card-bg);
    border-radius: var(--border-radius-md);
    box-shadow: var(--shadow-sm);
    transition: all var(--transition-normal);
    cursor: pointer;

    &:hover {
      box-shadow: var(--shadow-md);
      transform: translateY(-2px);
    }

    &.unread {
      border-left: 4px solid var(--primary-color);
      background-color: rgba(3, 204, 136, 0.05);
    }

    .notification-content {
      flex: 1;

      .notification-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: var(--spacing-sm);

        .notification-title {
          font-size: var(--font-size-md);
          font-weight: 600;
          color: var(--text-primary);
          margin: 0;
        }

        .notification-time {
          font-size: var(--font-size-sm);
          color: var(--text-secondary);
        }
      }

      .notification-body {
        font-size: var(--font-size-sm);
        color: var(--text-secondary);
        line-height: 1.5;
        margin: 0;
      }
    }

    .notification-actions {
      margin-left: var(--spacing-md);
      flex-shrink: 0;

      :deep(.el-button) {
        font-size: var(--font-size-sm);
      }
    }
  }
}

.empty-state {
  text-align: center;
  padding: var(--spacing-xxl) 0;
}

@media (max-width: 768px) {
  .notifications-page {
    padding: var(--spacing-lg) var(--spacing-md);
  }

  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);
  }

  .notification-item {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--spacing-md);

    .notification-actions {
      margin-left: 0;
      align-self: flex-end;
    }
  }
}
</style>
