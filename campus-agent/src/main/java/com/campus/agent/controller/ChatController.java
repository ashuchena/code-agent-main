package com.campus.agent.controller;

import com.campus.agent.agent.AgentDispatcher;
import com.campus.agent.service.ChatHistoryService;
import com.campus.agent.service.ChatService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * AI对话控制器：核心接口 /agent/chat
 */
@RestController
@RequestMapping("/agent")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private AgentDispatcher agentDispatcher;

    /**
     * AI对话入口，支持图片和文档上传。
     */
    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(
            @RequestParam String content,
            @RequestParam Long userId,
            @RequestParam(required = false) String docBase64,
            @RequestParam(required = false) String docName,
            @RequestParam(required = false, name = "file") MultipartFile[] files) {
        try {
            String finalDocBase64 = docBase64;
            String finalDocName = docName;
            String imageBase64 = null;

            if (files != null) {
                for (MultipartFile file : files) {
                    if (file == null || file.isEmpty()) {
                        continue;
                    }
                    try {
                        byte[] bytes = file.getBytes();
                        String contentType = file.getContentType();
                        String originalFilename = file.getOriginalFilename();
                        if (contentType != null && contentType.startsWith("image/") && imageBase64 == null) {
                            String mimeType = contentType.isEmpty() ? "image/jpeg" : contentType;
                            imageBase64 = "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(bytes);
                        } else if (finalDocBase64 == null || finalDocBase64.isEmpty()) {
                            finalDocBase64 = Base64.getEncoder().encodeToString(bytes);
                            finalDocName = originalFilename;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            Map<String, Object> result;
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                result = agentDispatcher.dispatchWithImage(content, userId, imageBase64, finalDocBase64, finalDocName);
            } else {
                result = chatService.chat(content, userId, finalDocBase64, finalDocName);
            }
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("对话失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户对话历史。
     */
    @GetMapping("/history")
    public Result<List<Map<String, String>>> getHistory(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer limit) {
        try {
            List<Map<String, String>> history = limit != null && limit > 0
                    ? chatHistoryService.loadHistoryForQwen(userId, limit)
                    : chatHistoryService.loadVisibleHistory(userId);
            return Result.success(history);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取历史失败：" + e.getMessage());
        }
    }

    /**
     * 清除用户对话历史。
     */
    @PostMapping("/clear-history")
    public Result<Map<String, Object>> clearHistory(@RequestParam Long userId) {
        try {
            chatHistoryService.clearHistory(userId);
            return Result.success(new java.util.HashMap<>());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("清除失败：" + e.getMessage());
        }
    }

    /**
     * 保存单条对话消息到历史记录。
     */
    @PostMapping("/save-message")
    public Result<Void> saveMessage(
            @RequestParam Long userId,
            @RequestParam String role,
            @RequestParam String content) {
        try {
            chatHistoryService.saveMessage(userId, role, content);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 导出聊天记录。
     */
    @GetMapping("/export")
    public Result<Map<String, String>> exportHistory(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "md") String format) {
        try {
            String content;
            if ("txt".equalsIgnoreCase(format)) {
                content = chatHistoryService.exportHistoryAsText(userId);
            } else {
                content = chatHistoryService.exportHistoryAsMarkdown(userId);
            }
            Map<String, String> result = new java.util.HashMap<>();
            result.put("content", content);
            result.put("format", format);
            result.put("filename", "聊天记录_" + java.time.LocalDate.now() + "." + format);
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("导出失败：" + e.getMessage());
        }
    }
}
