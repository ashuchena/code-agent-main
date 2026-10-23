package com.campus.agent.service;

import com.campus.agent.entity.ChatHistory;
import com.campus.agent.mapper.ChatHistoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatHistoryService {

    private static final int MAX_CONTEXT_MESSAGES = 24;

    @Autowired
    private ChatHistoryMapper chatHistoryMapper;

    public List<Map<String, String>> loadHistoryForQwen(Long userId) {
        return loadHistoryForQwen(userId, MAX_CONTEXT_MESSAGES);
    }

    public List<Map<String, String>> loadHistoryForQwen(Long userId, int messageLimit) {
        return toMessageMaps(chatHistoryMapper.selectRecentWindowByUserId(userId, Math.max(1, messageLimit)));
    }

    public List<Map<String, String>> loadVisibleHistory(Long userId) {
        return toMessageMaps(chatHistoryMapper.selectAllByUserId(userId));
    }

    public void saveMessage(Long userId, String role, String content) {
        ChatHistory record = new ChatHistory();
        record.setUserId(userId);
        record.setRole(role);
        record.setContent(content);
        record.setCreateTime(LocalDateTime.now());
        chatHistoryMapper.insert(record);
    }

    public void saveConversation(Long userId, String userContent, String assistantContent) {
        saveMessage(userId, "user", userContent);
        saveMessage(userId, "assistant", assistantContent);
    }

    public void clearHistory(Long userId) {
        chatHistoryMapper.deleteByUserId(userId);
    }

    public String exportHistoryAsMarkdown(Long userId) {
        List<ChatHistory> history = chatHistoryMapper.selectAllByUserId(userId);
        StringBuilder md = new StringBuilder();
        md.append("# 校园助手聊天记录\n\n");
        md.append("> 导出时间：").append(LocalDateTime.now().toString().replace("T", " ")).append("\n\n");
        md.append("---\n\n");

        for (ChatHistory msg : history) {
            String role = "user".equals(msg.getRole()) ? "用户" : "AI助手";
            String time = msg.getCreateTime().toString().replace("T", " ");
            md.append("### ").append(role).append("\n");
            md.append("*").append(time).append("*\n\n");
            md.append(msg.getContent()).append("\n\n");
            md.append("---\n\n");
        }

        return md.toString();
    }

    public String exportHistoryAsText(Long userId) {
        List<ChatHistory> history = chatHistoryMapper.selectAllByUserId(userId);
        StringBuilder text = new StringBuilder();
        text.append("=====================================\n");
        text.append("         校园助手聊天记录\n");
        text.append("=====================================\n");
        text.append("导出时间：").append(LocalDateTime.now().toString().replace("T", " ")).append("\n\n");

        for (ChatHistory msg : history) {
            String role = "user".equals(msg.getRole()) ? "【用户】" : "【AI助手】";
            String time = msg.getCreateTime().toString().replace("T", " ");
            text.append(role).append(" [").append(time).append("]\n");
            text.append(msg.getContent()).append("\n\n");
            text.append("-------------------------------------\n\n");
        }

        return text.toString();
    }

    private List<Map<String, String>> toMessageMaps(List<ChatHistory> history) {
        return history.stream()
            .map(this::toMessageMap)
            .filter(msg -> !msg.get("content").contains("AI服务调用出错"))
            .collect(Collectors.toList());
    }

    private Map<String, String> toMessageMap(ChatHistory history) {
        Map<String, String> message = new HashMap<>();
        message.put("role", history.getRole());
        message.put("content", history.getContent());
        return message;
    }
}
