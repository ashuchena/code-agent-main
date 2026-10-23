package com.campus.agent.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通义千问大模型服务类
 * 调用阿里云 DashScope API 实现AI自由对话
 */
@Service
public class QwenService {

    @Value("${qwen.api-key:}")
    private String apiKey;

    @Value("${qwen.model:qwen-plus}")
    private String model;

    @Value("${qwen.vl-model:qwen3-vl-plus}")
    private String vlModel;

    @Value("${qwen.url:https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public QwenService() {
        // 禁用 JVM 系统代理（解决系统配置代理但代理未运行的问题）
        System.setProperty("java.net.useSystemProxies", "false");
        System.clearProperty("http.proxyHost");
        System.clearProperty("http.proxyPort");
        System.clearProperty("https.proxyHost");
        System.clearProperty("https.proxyPort");

        // 创建 RestTemplate
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000);
        factory.setReadTimeout(60000);
        this.restTemplate = new RestTemplate(factory);
        System.out.println("[QwenService] RestTemplate 已初始化，代理已禁用");
    }

    /**
     * 动态切换模型（供管理员后台调用）
     */
    public void setModel(String modelKey) {
        this.model = modelKey;
        System.out.println("[QwenService] 模型已切换为：" + modelKey);
    }

    /**
     * 获取当前模型名称
     */
    public String getModel() {
        return this.model;
    }

    /**
     * 带系统提示词的对话（校园管家模式）
     * @param userMessage 用户输入
     * @param systemPrompt 系统提示词
     * @return AI回复内容
     */
    public String chatWithSystem(String userMessage, String systemPrompt) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 系统提示词
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        // 用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        return callQwenApi(messages);
    }

    /**
     * 普通对话（无系统提示词）
     * @param userMessage 用户输入
     * @return AI回复内容
     */
    public String chat(String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        return callQwenApi(messages);
    }

    /**
     * 多轮对话（带历史上下文，带系统提示词）
     * @param history  历史消息列表（每条含 role + content）
     * @param userMessage 用户当前输入
     * @param systemPrompt 系统提示词
     * @return AI回复内容
     */
    public String chatWithHistory(List<Map<String, String>> history, String userMessage, String systemPrompt) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 系统提示词
        Map<String, String> systemMsg = new HashMap<>();
        systemMsg.put("role", "system");
        systemMsg.put("content", systemPrompt);
        messages.add(systemMsg);

        // 历史消息
        if (history != null) {
            messages.addAll(history);
        }

        // 当前用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        return callQwenApi(messages);
    }

    /**
     * 多轮对话（带历史上下文，无系统提示词）
     * @param history  历史消息列表
     * @param userMessage 用户当前输入
     * @return AI回复内容
     */
    public String chatWithHistory(List<Map<String, String>> history, String userMessage) {
        List<Map<String, String>> messages = new ArrayList<>();

        // 历史消息
        if (history != null) {
            messages.addAll(history);
        }

        // 当前用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);

        return callQwenApi(messages);
    }

    /**
     * 调用通义千问API核心方法
     * @param messages 消息列表
     * @return AI回复文本
     */
    private String callQwenApi(List<Map<String, String>> messages) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            // 发送请求
            HttpEntity<String> entity = new HttpEntity<>(JSON.toJSONString(requestBody), headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // 解析响应，提取AI回复
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JSONObject json = JSON.parseObject(response.getBody());
                JSONArray choices = json.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    if (message != null) {
                        String content = message.getString("content");
                        // Qwen3系列默认开启思考模式，过滤掉 <think>...</think> 内部推理过程
                        if (content != null) {
                            content = content.replaceAll("(?s)<think>.*?</think>", "").trim();
                        }
                        return content;
                    }
                }
            }

            return "抱歉，AI暂时无法回复，请稍后重试～";

        } catch (Exception e) {
            e.printStackTrace();
            return "AI服务调用出错：" + e.getMessage();
        }
    }

    /**
     * 图片理解：用户发送图片+文字，AI看图回答（带系统提示词）
     * @param userMessage 用户输入（文本）
     * @param imageBase64 图片Base64编码（data:image/xxx;base64,前缀）
     * @param systemPrompt 系统提示词
     * @return AI回复
     */
    public String chatWithImage(String userMessage, String imageBase64, String systemPrompt) {
        List<Map<String, Object>> messages = new ArrayList<>();

        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
        }

        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", buildVisionContent(userMessage, imageBase64));
        messages.add(userMsg);

        return callQwenApiWithVision(messages, vlModel);
    }

    /**
     * 图片理解：用户发送图片+文字，AI看图回答（无系统提示词）
     * @param userMessage 用户输入（文本）
     * @param imageBase64 图片Base64
     * @return AI回复
     */
    public String chatWithImage(String userMessage, String imageBase64) {
        return chatWithImage(userMessage, imageBase64, null);
    }

    /**
     * 图片理解 + 多轮对话历史
     * @param history  历史对话（每条含 role + content）
     * @param userMessage 用户当前输入
     * @param imageBase64 图片Base64
     * @param systemPrompt 系统提示词
     * @return AI回复
     */
    public String chatWithImageAndHistory(
            List<Map<String, String>> history,
            String userMessage,
            String imageBase64,
            String systemPrompt) {
        List<Map<String, Object>> messages = new ArrayList<>();

        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            Map<String, Object> systemMsg = new HashMap<>();
            systemMsg.put("role", "system");
            systemMsg.put("content", systemPrompt);
            messages.add(systemMsg);
        }

        // 转换历史消息
        for (Map<String, String> h : history) {
            Map<String, Object> histMsg = new HashMap<>();
            histMsg.put("role", h.get("role"));
            histMsg.put("content", h.get("content"));
            messages.add(histMsg);
        }

        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", buildVisionContent(userMessage, imageBase64));
        messages.add(userMsg);

        return callQwenApiWithVision(messages, vlModel);
    }

    private List<Map<String, Object>> buildVisionContent(String userMessage, String imageBase64) {
        String imageUrl = normalizeImageDataUri(imageBase64);

        Map<String, Object> imageUrlPart = new HashMap<>();
        imageUrlPart.put("url", imageUrl);

        Map<String, Object> imagePart = new HashMap<>();
        imagePart.put("type", "image_url");
        imagePart.put("image_url", imageUrlPart);

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("type", "text");
        textPart.put("text", userMessage == null || userMessage.isEmpty() ? "请识别并说明这张图片的内容。" : userMessage);

        List<Map<String, Object>> content = new ArrayList<>();
        content.add(imagePart);
        content.add(textPart);
        return content;
    }

    private String normalizeImageDataUri(String imageBase64) {
        if (imageBase64 == null) {
            return "";
        }
        String trimmed = imageBase64.trim();
        if (trimmed.startsWith("data:image/")) {
            return trimmed;
        }
        return "data:image/jpeg;base64," + trimmed;
    }

    /**
     * 调用支持视觉的大模型API（多模态）
     */
    private String callQwenApiWithVision(List<Map<String, Object>> messages, String model) {
        try {
            System.out.println("[QwenService] callQwenApiWithVision 开始");
            // 检查图片数据
            for (Map<String, Object> msg : messages) {
                if ("user".equals(msg.get("role")) && msg.get("content") instanceof List) {
                    List<?> content = (List<?>) msg.get("content");
                    System.out.println("[QwenService] 用户消息包含 " + content.size() + " 个部分");
                    for (Object part : content) {
                        if (part instanceof Map) {
                            Map<?, ?> partMap = (Map<?, ?>) part;
                            if ("image_url".equals(partMap.get("type"))) {
                                Map<?, ?> imageUrl = (Map<?, ?>) partMap.get("image_url");
                                if (imageUrl != null) {
                                    Object url = imageUrl.get("url");
                                    if (url != null) {
                                        String urlStr = url.toString();
                                        System.out.println("[QwenService] Image URL length: " + urlStr.length());
                                        System.out.println("[QwenService] Image URL prefix: " + urlStr.substring(0, Math.min(80, urlStr.length())));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String requestJson = JSON.toJSONString(requestBody);
            System.out.println("[QwenService] Request model: " + model);
            System.out.println("[QwenService] Request body 长度: " + requestJson.length());
            
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JSONObject json = JSON.parseObject(response.getBody());
                JSONArray choices = json.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject choice = choices.getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    if (message != null) {
                        String content = message.getString("content");
                        if (content != null) {
                            content = content.replaceAll("(?s)<think>.*?</think>", "").trim();
                            // 如果AI返回无法看图的错误，检查是否是图片问题
                            if (content.contains("无法直接查看图片") || content.contains("无法查看图片") || content.contains("无法识别图片")) {
                                System.out.println("[QwenService] AI无法处理图片，请检查图片格式或大小");
                            }
                        }
                        return content;
                    }
                }
            }
            System.out.println("[QwenService] API响应为空或状态异常");
            return "抱歉，AI暂时无法回复，请稍后重试～";
        } catch (Exception e) {
            e.printStackTrace();
            return "AI服务调用出错：" + e.getMessage();
        }
    }
}
