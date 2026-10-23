package com.campus.agent.service;

import com.campus.agent.agent.AgentDispatcher;
import com.campus.agent.entity.CampusKnowledge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    @Autowired
    private AgentDispatcher agentDispatcher;

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private DocumentParserService documentParserService;

    @Autowired
    private CampusKnowledgeService campusKnowledgeService;

    @Autowired
    private QwenService qwenService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Value("${campus-knowledge.school-name:广州松田职业学院}")
    private String schoolName;

    public Map<String, Object> chat(String content, Long userId, String docBase64, String docName) {
        if (docBase64 != null && !docBase64.isEmpty() && docName != null && !docName.isEmpty()) {
            try {
                byte[] docBytes = java.util.Base64.getDecoder().decode(docBase64);
                String docContent = documentParserService.parseDocument(docBytes, docName);
                if (docContent != null && !docContent.isEmpty()) {
                    content = content == null ? "" : content;
                    content = docContent + "\n\n【用户问题】" + content;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        CampusKnowledge directKnowledge = campusKnowledgeService.queryAndTrack(content);
        if (directKnowledge != null && directKnowledge.getAnswer() != null && !directKnowledge.getAnswer().trim().isEmpty()) {
            try {
                chatHistoryService.saveMessage(userId, "user", content);
                chatHistoryService.saveMessage(userId, "assistant", directKnowledge.getAnswer());
            } catch (Exception ignored) {
            }

            Map<String, Object> result = new HashMap<>();
            result.put("intent", "CAMPUS_KNOWLEDGE");
            result.put("keywords", content);
            result.put("data", directKnowledge);
            result.put("reply", directKnowledge.getAnswer());
            return result;
        }

        if (shouldUseSchoolScopedFallback(content)) {
            try {
                chatHistoryService.saveMessage(userId, "user", content);
            } catch (Exception ignored) {
            }

            List<Map<String, String>> history = loadHistorySafely(userId);
            String fallbackReply = qwenService.chatWithHistory(
                    history,
                    content,
                    buildSchoolScopedPrompt()
            );

            try {
                if (fallbackReply != null && !fallbackReply.startsWith("AI")) {
                    chatHistoryService.saveMessage(userId, "assistant", fallbackReply);
                }
            } catch (Exception ignored) {
            }

            campusKnowledgeService.recordMissAndMaybePromote(content, fallbackReply);

            Map<String, Object> result = new HashMap<>();
            result.put("intent", "SCHOOL_SCOPED_FALLBACK");
            result.put("keywords", content);
            result.put("data", null);
            result.put("reply", fallbackReply);
            return result;
        }

        return agentDispatcher.dispatch(content, userId);
    }

    public Map<String, Object> chat(String content, Long userId) {
        return chat(content, userId, null, null);
    }

    private boolean shouldUseSchoolScopedFallback(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        String normalized = content.trim();
        return normalized.contains("学校")
                || normalized.contains("我校")
                || normalized.contains("我们学校")
                || normalized.contains("贵校")
                || normalized.contains("校区")
                || normalized.contains("地址")
                || normalized.contains("食堂")
                || normalized.contains("宿舍")
                || normalized.contains("图书馆")
                || normalized.contains("教学设施")
                || normalized.contains("奖学金")
                || normalized.contains("学费")
                || normalized.contains("专业")
                || normalized.contains("招生")
                || normalized.contains("报到");
    }

    private String buildSchoolScopedPrompt() {
        String activeSchoolName = systemConfigService.getConfigValue(SystemConfigService.SCHOOL_NAME_KEY, schoolName);
        return "你是" + activeSchoolName + "的校园智能助手。"
                + "你只能回答与" + activeSchoolName + "有关的信息，不能扩展到其他学校。"
                + "如果问题没有知识库直接答案，请仍然优先站在" + activeSchoolName + "的场景下回答。"
                + "如果无法确认，就明确说明暂未查到" + activeSchoolName + "的准确信息，并建议咨询学校相关部门。"
                + "回答要简洁、自然、直接，不要使用Markdown。";
    }

    private List<Map<String, String>> loadHistorySafely(Long userId) {
        try {
            return chatHistoryService.loadHistoryForQwen(userId);
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }
}
