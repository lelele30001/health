<template>
  <div class="ai-chat-container">
    <div class="ai-chat-header">
      <div class="ai-avatar">
        <el-icon><ChatDotRound /></el-icon>
      </div>
      <div class="ai-title">
        <h4>AI健康助手</h4>
        <span class="ai-status">在线</span>
      </div>
      <div class="ai-chat-actions">
        <el-button type="text" @click="$emit('close')" class="close-btn">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>
    </div>

    <div class="ai-chat-messages" ref="messagesContainer">
      <div
        v-for="(message, index) in messages"
        :key="index"
        :class="[
          'message',
          message.role === 'user' ? 'user-message' : 'ai-message',
        ]"
      >
        <div class="message-avatar">
          <el-icon v-if="message.role === 'user'"><User /></el-icon>
          <el-icon v-else><ChatDotRound /></el-icon>
        </div>
        <div class="message-content">
          <div
            class="message-text"
            v-html="formatMessage(message.content)"
          ></div>
          <div class="message-time">{{ message.time }}</div>
        </div>
      </div>

      <div v-if="loading" class="message ai-message loading">
        <div class="message-avatar">
          <el-icon><ChatDotRound /></el-icon>
        </div>
        <div class="message-content">
          <div class="typing-indicator">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>
      </div>
    </div>

    <div class="ai-chat-input">
      <div class="quick-actions">
        <el-button
          size="small"
          @click="sendQuickMessage('帮我制定一份减脂餐计划')"
          type="info"
          plain
        >
          减脂餐
        </el-button>
        <el-button
          size="small"
          @click="sendQuickMessage('增肌应该怎么吃？')"
          type="info"
          plain
        >
          增肌饮食
        </el-button>
        <el-button
          size="small"
          @click="sendQuickMessage('推荐一些低热量食材')"
          type="info"
          plain
        >
          低热量食材
        </el-button>
      </div>
      <div class="input-area">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          placeholder="请输入您的问题，AI助手将为您解答..."
          @keyup.enter.ctrl="sendMessage"
          resize="none"
        />
        <el-button
          type="primary"
          @click="sendMessage"
          :loading="loading"
          :disabled="!inputMessage.trim()"
          class="send-btn"
        >
          <el-icon><Promotion /></el-icon>
        </el-button>
      </div>
      <div class="input-tip">按 Ctrl + Enter 发送</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, getCurrentInstance } from "vue";
import { ChatDotRound, User, Promotion, Close } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import axios from "axios";

const context = getCurrentInstance()?.appContext.config.globalProperties;

const props = defineProps({
  userProfile: {
    type: Object,
    default: null,
  },
});

const emit = defineEmits(["close"]);

// 系统欢迎消息（不参与API调用）
const systemWelcomeMessage = {
  role: "system",
  content:
    "您好！我是您的AI健康助手。我可以帮您：\n\n1. 制定个性化的饮食计划\n2. 推荐适合您的健康食谱\n3. 解答营养健康问题\n4. 分析食材营养价值\n\n请问有什么可以帮助您的吗？",
  time: new Date().toLocaleTimeString(),
  isSystem: true, // 标记为系统消息
};

const messages = ref([systemWelcomeMessage]);

const inputMessage = ref("");
const loading = ref(false);
const messagesContainer = ref(null);

const formatMessage = (content) => {
  return content.replace(/\n/g, "<br>");
};

const scrollToBottom = () => {
  nextTick(() => {
    if (messagesContainer.value) {
      messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight;
    }
  });
};

const sendQuickMessage = (message) => {
  inputMessage.value = message;
  sendMessage();
};

const sendMessage = async () => {
  const message = inputMessage.value.trim();
  if (!message || loading.value) return;

  console.log("===== Frontend Debug =====");
  console.log("Sending message:", message);

  messages.value.push({
    role: "user",
    content: message,
    time: new Date().toLocaleTimeString(),
  });

  inputMessage.value = "";
  loading.value = true;
  scrollToBottom();

  // 构建历史记录，只包含真实的用户和AI对话，不包含系统欢迎消息
  const history = messages.value
    .slice(0, -1) // 排除最后一条（刚添加的用户消息）
    .filter((msg) => !msg.isSystem) // 过滤掉系统欢迎消息
    .map((msg) => ({
      role: msg.role === "user" ? "user" : "assistant",
      content: msg.content,
    }));

  const requestData = {
    message: message,
    history: history,
  };

  console.log("Request data:", JSON.stringify(requestData));

  try {
    const res = await axios.post("/project/ai/chat", requestData, {
      headers: {
        "Content-Type": "application/json",
      },
    });

    console.log("Response:", res);

    if (res.data && res.data.code === 0) {
      messages.value.push({
        role: "assistant",
        content: res.data.response,
        time: new Date().toLocaleTimeString(),
      });
    } else {
      console.error("Error response:", res);
      ElMessage.error(res?.data?.msg || "AI服务响应失败");
      messages.value.push({
        role: "assistant",
        content: "抱歉，我暂时无法回答您的问题，请稍后再试。",
        time: new Date().toLocaleTimeString(),
      });
    }
  } catch (error) {
    console.error("AI请求失败:", error);
    console.error("Error details:", error.response || error.message);
    ElMessage.error("AI服务调用失败: " + (error.message || "未知错误"));
    messages.value.push({
      role: "assistant",
      content: "抱歉，网络连接失败，请检查网络后重试。",
      time: new Date().toLocaleTimeString(),
    });
  } finally {
    loading.value = false;
    scrollToBottom();
    console.log("===== End Frontend Debug =====");
  }
};

onMounted(() => {
  scrollToBottom();
  console.log("AIChat component mounted");
});
</script>

<style scoped>
.ai-chat-container {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.ai-chat-header {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background: linear-gradient(135deg, #ff9800 0%, #f57c00 100%);
  color: #fff;
}

.ai-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  font-size: 20px;
}

.ai-title h4 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
}

.ai-status {
  font-size: 12px;
  opacity: 0.8;
  display: flex;
  align-items: center;
  gap: 5px;
}

.ai-chat-actions {
  margin-left: auto;
}

.close-btn {
  color: rgba(255, 255, 255, 0.8);
  font-size: 18px;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.ai-status::before {
  content: "";
  width: 8px;
  height: 8px;
  background: #4ade80;
  border-radius: 50%;
  display: inline-block;
}

.ai-chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background: #f5f7fa;
}

.message {
  display: flex;
  margin-bottom: 20px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.user-message {
  flex-direction: row-reverse;
}

.user-message .message-avatar {
  margin-right: 0;
  margin-left: 12px;
  background: #667eea;
  color: #fff;
}

.message-content {
  max-width: 70%;
  background: #fff;
  padding: 12px 16px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.user-message .message-content {
  background: #667eea;
  color: #fff;
}

.message-text {
  font-size: 14px;
  line-height: 1.6;
  word-wrap: break-word;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 5px;
  text-align: right;
}

.user-message .message-time {
  color: rgba(255, 255, 255, 0.7);
}

.loading .message-content {
  background: #fff;
}

.typing-indicator {
  display: flex;
  gap: 5px;
  padding: 5px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  background: #667eea;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%,
  60%,
  100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

.ai-chat-input {
  padding: 15px 20px;
  background: #fff;
  border-top: 1px solid #e4e7ed;
}

.quick-actions {
  display: flex;
  gap: 8px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.input-area {
  display: flex;
  gap: 10px;
}

.input-area :deep(.el-textarea__inner) {
  border-radius: 8px;
  resize: none;
}

.send-btn {
  height: auto;
  padding: 0 20px;
  border-radius: 8px;
}

.input-tip {
  font-size: 11px;
  color: #999;
  text-align: right;
  margin-top: 5px;
}

.ai-chat-messages::-webkit-scrollbar {
  width: 6px;
}

.ai-chat-messages::-webkit-scrollbar-track {
  background: transparent;
}

.ai-chat-messages::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 3px;
}

.ai-chat-messages::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>
