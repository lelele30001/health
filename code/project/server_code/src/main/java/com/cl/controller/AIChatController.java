package com.cl.controller;

import com.cl.annotation.IgnoreAuth;
import com.cl.entity.dto.ChatRequestDTO;
import com.cl.service.AIChatService;
import com.cl.service.PostRecommendationService;
import com.cl.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI聊天控制器
 * 提供AI对话相关的API接口
 */
@RestController
@RequestMapping("/ai")
public class AIChatController {

    @Autowired
    private AIChatService aiChatService;
    
    @Autowired
    private PostRecommendationService postRecommendationService;

    /**
     * AI聊天接口
     * @param requestDTO 聊天请求DTO
     * @param request HTTP请求
     * @return AI回复
     */
    @IgnoreAuth
    @PostMapping(value = "/chat", produces = "application/json;charset=UTF-8")
    public R chat(@RequestBody ChatRequestDTO requestDTO, HttpServletRequest request) {
        System.out.println("===== AIChatController Debug =====");
        
        // 设置请求编码为UTF-8
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("Received DTO: " + requestDTO);
        
        // 如果DTO为null，尝试从request中读取原始数据
        if (requestDTO == null) {
            System.out.println("DTO is null, trying to read raw request body...");
            try {
                BufferedReader reader = request.getReader();
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                System.out.println("Raw request body: " + sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return R.error("请求参数解析失败");
        }
        
        String message = requestDTO.getMessage();
        List<Map<String, String>> history = requestDTO.getHistory();

        System.out.println("Extracted message: " + message);
        System.out.println("Extracted history: " + history);

        if (message == null || message.trim().isEmpty()) {
            System.out.println("Message is empty, returning error");
            System.out.println("===== End Debug =====");
            return R.error("消息不能为空");
        }

        try {
            String response = aiChatService.chatWithHistory(message, history);
            System.out.println("AI response: " + response);
            System.out.println("===== End Debug =====");
            return R.ok().put("response", response);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception: " + e.getMessage());
            System.out.println("===== End Debug =====");
            return R.error("AI服务调用失败：" + e.getMessage());
        }
    }

    /**
     * 简单的AI聊天接口（不带历史记录）
     * @param message 用户消息
     * @return AI回复
     */
    @IgnoreAuth
    @GetMapping(value = "/chat", produces = "application/json;charset=UTF-8")
    public R chatSimple(@RequestParam String message) {
        if (message == null || message.trim().isEmpty()) {
            return R.error("消息不能为空");
        }

        try {
            String response = aiChatService.chat(message);
            return R.ok().put("response", response);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("AI服务调用失败：" + e.getMessage());
        }
    }

    /**
     * 获取AI健康建议
     * @param params 包含身高、体重、目标等参数
     * @return AI建议
     */
    @PostMapping(value = "/health-advice", produces = "application/json;charset=UTF-8")
    public R getHealthAdvice(@RequestBody Map<String, Object> params) {
        String height = (String) params.get("height");
        String weight = (String) params.get("weight");
        String goal = (String) params.get("goal");
        String restrictions = (String) params.get("restrictions");

        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下信息给我提供个性化的健康饮食建议：\n");
        if (height != null) prompt.append("身高：").append(height).append("cm\n");
        if (weight != null) prompt.append("体重：").append(weight).append("kg\n");
        if (goal != null) prompt.append("健身目标：").append(goal).append("\n");
        if (restrictions != null) prompt.append("饮食禁忌：").append(restrictions).append("\n");
        prompt.append("\n请给出：\n");
        prompt.append("1. 每日建议热量摄入\n");
        prompt.append("2. 三餐搭配建议\n");
        prompt.append("3. 推荐的食材清单\n");
        prompt.append("4. 注意事项");

        try {
            String response = aiChatService.chat(prompt.toString());
            return R.ok().put("response", response);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("AI服务调用失败：" + e.getMessage());
        }
    }

    /**
     * 获取食谱建议
     * @param params 包含食材、口味等参数
     * @return AI建议的食谱
     */
    @PostMapping(value = "/recipe-suggestion", produces = "application/json;charset=UTF-8")
    public R getRecipeSuggestion(@RequestBody Map<String, Object> params) {
        String ingredients = (String) params.get("ingredients");
        String taste = (String) params.get("taste");
        String mealType = (String) params.get("mealType");

        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据以下信息给我推荐一道健康美味的食谱：\n");
        if (ingredients != null) prompt.append("现有食材：").append(ingredients).append("\n");
        if (taste != null) prompt.append("口味偏好：").append(taste).append("\n");
        if (mealType != null) prompt.append("用餐场景：").append(mealType).append("\n");
        prompt.append("\n请给出：\n");
        prompt.append("1. 食谱名称\n");
        prompt.append("2. 所需食材及用量\n");
        prompt.append("3. 详细制作步骤\n");
        prompt.append("4. 营养价值分析\n");
        prompt.append("5. 适合人群");

        try {
            String response = aiChatService.chat(prompt.toString());
            return R.ok().put("response", response);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("AI服务调用失败：" + e.getMessage());
        }
    }

    /**
     * 保存AI问题到数据库
     * @param params 包含用户ID和问题内容
     * @return 保存结果
     */
    @IgnoreAuth
    @PostMapping(value = "/save-question", produces = "application/json;charset=UTF-8")
    public R saveQuestion(@RequestBody Map<String, Object> params) {
        try {
            Long userId = ((Number) params.get("userId")).longValue();
            String question = (String) params.get("question");
            
            if (userId == null || question == null || question.trim().isEmpty()) {
                return R.error("参数不能为空");
            }
            
            // 保存问题到数据库
            postRecommendationService.saveUserQuestion(userId, question);
            
            return R.ok().put("message", "问题保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("保存失败：" + e.getMessage());
        }
    }
}
