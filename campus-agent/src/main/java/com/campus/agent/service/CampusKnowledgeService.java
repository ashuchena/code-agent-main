package com.campus.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.agent.entity.CampusKnowledge;
import com.campus.agent.entity.CampusKnowledgeMissing;
import com.campus.agent.mapper.CampusKnowledgeMapper;
import com.campus.agent.mapper.CampusKnowledgeMissingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CampusKnowledgeService {

    private static final int DEFAULT_CANDIDATE_LIMIT = 12;

    @Value("${campus-knowledge.auto-learn-threshold:3}")
    private int autoLearnThreshold;

    @Value("${campus-knowledge.school-name:广州松田职业学院}")
    private String schoolName;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private CampusKnowledgeMapper campusKnowledgeMapper;

    @Autowired
    private CampusKnowledgeMissingMapper campusKnowledgeMissingMapper;

    public CampusKnowledge findBestMatch(String question) {
        String rewrittenQuestion = rewriteQuestion(question);
        String normalizedQuestion = normalize(rewrittenQuestion);
        if (!StringUtils.hasText(normalizedQuestion)) {
            return null;
        }

        CampusKnowledge exact = campusKnowledgeMapper.selectEnabledByNormalizedQuestion(normalizedQuestion);
        if (exact != null) {
            return exact;
        }

        List<CampusKnowledge> candidates = campusKnowledgeMapper.selectCandidates(
                safeKeyword(rewrittenQuestion),
                normalizedQuestion,
                DEFAULT_CANDIDATE_LIMIT
        );
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        for (CampusKnowledge candidate : candidates) {
            candidate.setMatchScore(score(rewrittenQuestion, normalizedQuestion, candidate));
        }

        CampusKnowledge best = Collections.max(
                candidates,
                Comparator.comparingInt(item -> item.getMatchScore() == null ? 0 : item.getMatchScore())
        );
        return best.getMatchScore() != null && best.getMatchScore() >= 55 ? best : null;
    }

    public CampusKnowledge queryAndTrack(String question) {
        try {
            CampusKnowledge match = findBestMatch(question);
            if (match != null && match.getId() != null) {
                campusKnowledgeMapper.incrementHitCount(match.getId());
            }
            return match;
        } catch (Exception e) {
            System.err.println("[CampusKnowledge] Query skipped: " + e.getMessage());
            return null;
        }
    }

    public void recordMissAndMaybePromote(String question, String answer) {
        try {
            int effectiveThreshold = getAutoLearnThreshold();
            String normalizedQuestion = normalize(question);
            if (!StringUtils.hasText(normalizedQuestion) || !StringUtils.hasText(answer)) {
                return;
            }

            CampusKnowledgeMissing existing = campusKnowledgeMissingMapper.selectByNormalizedQuestion(normalizedQuestion);
            LocalDateTime now = LocalDateTime.now();
            if (existing == null) {
                CampusKnowledgeMissing missing = new CampusKnowledgeMissing();
                missing.setNormalizedQuestion(normalizedQuestion);
                missing.setLatestQuestion(limit(question, 255));
                missing.setAnswer(answer);
                missing.setHitCount(1);
                missing.setPromoted(0);
                missing.setLastHitAt(now);
                missing.setCreatedAt(now);
                missing.setUpdatedAt(now);
                campusKnowledgeMissingMapper.insert(missing);
                existing = missing;
            } else {
                existing.setLatestQuestion(limit(question, 255));
                existing.setAnswer(answer);
                existing.setHitCount((existing.getHitCount() == null ? 0 : existing.getHitCount()) + 1);
                existing.setLastHitAt(now);
                existing.setUpdatedAt(now);
                campusKnowledgeMissingMapper.updateById(existing);
            }

            if ((existing.getHitCount() == null ? 0 : existing.getHitCount()) < effectiveThreshold) {
                return;
            }

            CampusKnowledge learned = campusKnowledgeMapper.selectAnyByNormalizedQuestion(normalizedQuestion);
            if (learned == null) {
                learned = new CampusKnowledge();
                learned.setCategory("auto_learned");
                learned.setQuestion(limit(question, 255));
                learned.setNormalizedQuestion(normalizedQuestion);
                learned.setAnswer(answer);
                learned.setSourceType("AUTO_LEARNED");
                learned.setSourceFile("campus_knowledge_missing");
                learned.setEnabled(1);
                learned.setHitCount(0);
                learned.setCreatedAt(now);
                learned.setUpdatedAt(now);
                campusKnowledgeMapper.insert(learned);
            }

            existing.setPromoted(1);
            existing.setUpdatedAt(now);
            campusKnowledgeMissingMapper.updateById(existing);
        } catch (Exception e) {
            System.err.println("[CampusKnowledge] Miss record skipped: " + e.getMessage());
        }
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        long totalKnowledge = campusKnowledgeMapper.selectCount(new QueryWrapper<CampusKnowledge>());
        long enabledKnowledge = campusKnowledgeMapper.selectCount(new QueryWrapper<CampusKnowledge>().eq("enabled", 1));
        long importedKnowledge = campusKnowledgeMapper.selectCount(new QueryWrapper<CampusKnowledge>().eq("source_type", "EXCEL_IMPORT"));
        long autoLearnedKnowledge = campusKnowledgeMapper.selectCount(new QueryWrapper<CampusKnowledge>().eq("source_type", "AUTO_LEARNED"));
        long missingTotal = campusKnowledgeMissingMapper.selectCount(new QueryWrapper<CampusKnowledgeMissing>());
        long promotedTotal = campusKnowledgeMissingMapper.selectCount(new QueryWrapper<CampusKnowledgeMissing>().eq("promoted", 1));

        stats.put("schoolName", getSchoolName());
        stats.put("totalKnowledge", totalKnowledge);
        stats.put("autoLearnThreshold", getAutoLearnThreshold());
        stats.put("enabledKnowledge", enabledKnowledge);
        stats.put("importedKnowledge", importedKnowledge);
        stats.put("autoLearnedKnowledge", autoLearnedKnowledge);
        stats.put("missingTotal", missingTotal);
        stats.put("promotedTotal", promotedTotal);
        stats.put("topMissing", campusKnowledgeMissingMapper.selectTopMissing(10));
        return stats;
    }

    public List<CampusKnowledgeMissing> getTopMissing(int limit) {
        return campusKnowledgeMissingMapper.selectTopMissing(limit);
    }

    public String normalize(String text) {
        if (!StringUtils.hasText(text)) {
            return "";
        }
        return text.toLowerCase().replaceAll("[\\p{Punct}\\p{IsPunctuation}\\s]+", "");
    }

    public String rewriteQuestion(String question) {
        if (!StringUtils.hasText(question)) {
            return "";
        }
        String activeSchoolName = getSchoolName();
        String rewritten = question.trim();
        rewritten = rewritten.replace("我们学校", activeSchoolName);
        rewritten = rewritten.replace("我校", activeSchoolName);
        rewritten = rewritten.replace("贵校", activeSchoolName);

        if ("学校在哪里".equals(rewritten) || "学校在哪".equals(rewritten) || "我们学校在哪里".equals(question) || "我们学校在哪".equals(question)) {
            return activeSchoolName + "的地址在哪里";
        }

        if (rewritten.contains("在哪里") || rewritten.contains("在哪") || rewritten.contains("地址") || rewritten.contains("位置")) {
            rewritten = rewritten.replace("学校", activeSchoolName);
            if (rewritten.contains(activeSchoolName) && !rewritten.contains("地址")) {
                rewritten = activeSchoolName + "的地址在哪里";
            }
        }

        return rewritten;
    }

    public String getSchoolName() {
        return systemConfigService.getConfigValue(SystemConfigService.SCHOOL_NAME_KEY, schoolName);
    }

    public Integer getAutoLearnThreshold() {
        return systemConfigService.getIntConfigValue(
                SystemConfigService.AUTO_LEARN_THRESHOLD_KEY,
                autoLearnThreshold
        );
    }

    public void saveOrUpdateKnowledge(String category, String question, String answer, String sourceType, String sourceFile) {
        String normalizedQuestion = normalize(question);
        if (!StringUtils.hasText(normalizedQuestion) || !StringUtils.hasText(answer)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        CampusKnowledge existing = campusKnowledgeMapper.selectAnyByNormalizedQuestion(normalizedQuestion);
        if (existing == null) {
            CampusKnowledge knowledge = new CampusKnowledge();
            knowledge.setCategory(limit(category, 100));
            knowledge.setQuestion(limit(question, 255));
            knowledge.setNormalizedQuestion(normalizedQuestion);
            knowledge.setAnswer(answer);
            knowledge.setSourceType(limit(sourceType, 50));
            knowledge.setSourceFile(limit(sourceFile, 255));
            knowledge.setEnabled(1);
            knowledge.setHitCount(0);
            knowledge.setCreatedAt(now);
            knowledge.setUpdatedAt(now);
            campusKnowledgeMapper.insert(knowledge);
            return;
        }

        existing.setCategory(limit(category, 100));
        existing.setQuestion(limit(question, 255));
        existing.setAnswer(answer);
        existing.setSourceType(limit(sourceType, 50));
        existing.setSourceFile(limit(sourceFile, 255));
        existing.setEnabled(1);
        existing.setUpdatedAt(now);
        campusKnowledgeMapper.updateById(existing);
    }

    private String safeKeyword(String question) {
        String keyword = question == null ? "" : question.trim();
        return keyword.length() > 80 ? keyword.substring(0, 80) : keyword;
    }

    private int score(String question, String normalizedQuestion, CampusKnowledge candidate) {
        String candidateQuestion = candidate.getQuestion() == null ? "" : candidate.getQuestion();
        String candidateNormalized = candidate.getNormalizedQuestion() == null
                ? normalize(candidateQuestion)
                : candidate.getNormalizedQuestion();

        if (normalizedQuestion.equals(candidateNormalized)) {
            return 100;
        }
        if (candidateQuestion.equalsIgnoreCase(question)) {
            return 95;
        }
        if (candidateQuestion.contains(question) || question.contains(candidateQuestion)) {
            return 85;
        }

        int overlap = longestCommonSubstring(normalizedQuestion, candidateNormalized);
        int base = (overlap * 100) / Math.max(1, Math.max(normalizedQuestion.length(), candidateNormalized.length()));
        return base + Math.min(candidate.getHitCount() == null ? 0 : candidate.getHitCount(), 10);
    }

    private int longestCommonSubstring(String a, String b) {
        if (!StringUtils.hasText(a) || !StringUtils.hasText(b)) {
            return 0;
        }
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        int max = 0;
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    max = Math.max(max, dp[i][j]);
                }
            }
        }
        return max;
    }

    private String limit(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        return value.length() <= maxLength ? value : value.substring(0, maxLength);
    }
}
