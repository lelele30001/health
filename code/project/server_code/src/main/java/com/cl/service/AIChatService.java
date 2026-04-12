package com.cl.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * AI聊天服务
 * 接入智谱AI GLM-4-Air模型
 */
@Service
public class AIChatService {

    @Value("${zhipu.api.key}")
    private String apiKey;

    private static final String API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    private static final String MODEL = "glm-4-air"; // 免费模型，100%可用

    /**
     * 发送消息给AI并获取回复
     * @param message 用户消息
     * @return AI回复
     */
    public String chat(String message) {
        return chatWithHistory(message, null);
    }

    /**
     * 发送消息给AI并获取回复（带历史记录）
     * @param message 用户消息
     * @param history 历史对话记录
     * @return AI回复
     */
    public String chatWithHistory(String message, List<Map<String, String>> history) {
        try {
            System.out.println("===== AI Chat Debug =====");
            System.out.println("Received message: " + message);
            System.out.println("History size: " + (history != null ? history.size() : 0));
            System.out.println("API Key: " + (apiKey != null ? apiKey.substring(0, 10) + "..." : "null"));
            
            // 检查API Key
            if (apiKey == null || apiKey.trim().isEmpty()) {
                System.err.println("ERROR: API Key is null or empty!");
                return "AI 服务配置错误：API Key未配置";
            }
            
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", MODEL);

            // 构建消息列表
            JSONArray messages = new JSONArray();

            // 添加系统提示词
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个专业的健康饮食顾问，请用专业友好的语气回答用户问题。");
            messages.add(systemMessage);

            // 添加历史记录
            if (history != null) {
                for (Map<String, String> h : history) {
                    JSONObject historyMessage = new JSONObject();
                    historyMessage.put("role", h.get("role"));
                    historyMessage.put("content", h.get("content"));
                    messages.add(historyMessage);
                }
            }

            // 添加用户消息
            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.add(userMessage);

            requestBody.put("messages", messages);

            String requestJson = requestBody.toJSONString();
            System.out.println("Request JSON: " + requestJson);

            // 发送请求
            HttpResponse response = HttpRequest.post(API_URL)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .body(requestJson)
                    .timeout(30000) // 设置30秒超时
                    .execute();

            System.out.println("Response status: " + response.getStatus());
            System.out.println("Response body: " + response.body());

            // 解析响应
            if (response.getStatus() == 200) {
                JSONObject resp = JSON.parseObject(response.body());
                
                // 检查是否有错误
                if (resp.containsKey("error")) {
                    JSONObject error = resp.getJSONObject("error");
                    String errorMessage = error.getString("message");
                    System.err.println("API Error: " + errorMessage);
                    return "AI 服务错误：" + errorMessage;
                }
                
                // 正常响应
                if (resp.containsKey("choices")) {
                    JSONArray choices = resp.getJSONArray("choices");
                    if (choices != null && !choices.isEmpty()) {
                        JSONObject choice = choices.getJSONObject(0);
                        if (choice.containsKey("message")) {
                            JSONObject messageObj = choice.getJSONObject("message");
                            String content = messageObj.getString("content");
                            System.out.println("AI response content: " + content);
                            System.out.println("===== End Debug =====");
                            return content;
                        }
                    }
                }
                
                System.err.println("Unexpected response format: " + response.body());
                return "AI 服务返回格式异常";
            } else {
                System.err.println("HTTP Error: " + response.getStatus());
                System.err.println("Response body: " + response.body());
                return "AI 服务HTTP错误：" + response.getStatus();
            }

        } catch (Exception e) {
            System.err.println("===== Error Debug =====");
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
            System.err.println("===== End Debug =====");
            return "AI 调用失败：" + e.getMessage();
        }
    }

    /**
     * 流式聊天（用于实现打字机效果）
     * @param message 用户消息
     * @param history 历史对话记录
     * @return 流式响应
     */
    public String chatStream(String message, List<Map<String, String>> history) {
        // 智谱AI支持SSE流式输出，可以在这里实现
        // 为简化起见，先使用普通聊天
        return chatWithHistory(message, history);
    }
}
