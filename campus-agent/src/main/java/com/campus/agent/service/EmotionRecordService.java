package com.campus.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.agent.entity.EmotionRecord;
import com.campus.agent.entity.User;
import com.campus.agent.mapper.EmotionRecordMapper;
import com.campus.agent.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 情绪记录服务：检测用户情绪并保存记录
 */
@Service
public class EmotionRecordService {

    @Autowired
    private EmotionRecordMapper emotionRecordMapper;

    @Autowired
    private UserMapper userMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 情绪关键词配置：关键词 -> 情绪类型
    private static final Map<String[], String> EMOTION_KEYWORDS = new LinkedHashMap<>();

    static {
        // 焦虑类
        EMOTION_KEYWORDS.put(new String[]{"焦虑", "焦虑的", "好焦虑", "很焦虑", "焦虑症", "焦虑不安", "焦虑感", "焦虑情绪"}, "焦虑");
        // 压力大
        EMOTION_KEYWORDS.put(new String[]{"压力大", "压力好大", "压力太大", "压力好大", "喘不过气", "压得我", "压力", "鸭梨山大"}, "压力大");
        // 悲伤/难过
        EMOTION_KEYWORDS.put(new String[]{"难过", "好难过", "好伤心", "伤心", "悲伤", "悲痛", "心碎", "崩溃", "哭", "想哭"}, "悲伤");
        // 孤独
        EMOTION_KEYWORDS.put(new String[]{"孤独", "好孤独", "孤单", "寂寞", "没人陪", "没人理解", "没人懂", "一个人", "孤独感"}, "孤独");
        // 愤怒
        EMOTION_KEYWORDS.put(new String[]{"生气", "愤怒", "好气", "气愤", "恼火", "火大", "烦死了", "气死了"}, "愤怒");
        // 倦怠/疲惫
        EMOTION_KEYWORDS.put(new String[]{"累", "好累", "太累了", "身心疲惫", "疲惫", "倦怠", "没力气", "精疲力尽", "困"}, "倦怠");
        // 迷茫
        EMOTION_KEYWORDS.put(new String[]{"迷茫", "好迷茫", "迷茫的", "不知所措", "没方向", "不知道该怎么办"}, "迷茫");
        // 失眠/睡眠问题
        EMOTION_KEYWORDS.put(new String[]{"失眠", "睡不着", "睡不好", "难入睡", "早醒", "失眠的", "熬夜", "一整晚没睡"}, "失眠");
        // 自卑
        EMOTION_KEYWORDS.put(new String[]{"自卑", "不自信", "自我怀疑", "怀疑自己", "觉得自己不行", "比不上", "差劲"}, "自卑");
        // 失落/空虚
        EMOTION_KEYWORDS.put(new String[]{"失落", "空虚", "无聊", "没意义", "什么都没有", "什么都没有意思"}, "失落");
    }

    /**
     * 检测用户输入中的情绪
     * @param content 用户输入内容
     * @return 检测结果，null表示未检测到负面情绪；否则返回EmotionRecord
     */
    public EmotionRecord detectAndRecord(String content, Long userId, String aiReply) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        List<String> matchedTags = new ArrayList<>();
        String primaryEmotion = null;
        int maxLevel = 0;

        String lowerContent = content.toLowerCase();

        for (Map.Entry<String[], String> entry : EMOTION_KEYWORDS.entrySet()) {
            for (String keyword : entry.getKey()) {
                if (lowerContent.contains(keyword.toLowerCase())) {
                    matchedTags.add(entry.getValue());
                    if (primaryEmotion == null) {
                        primaryEmotion = entry.getValue();
                    }
                    // 根据关键词数量和强度估算情绪级别
                    int level = estimateLevel(content, keyword);
                    if (level > maxLevel) {
                        maxLevel = level;
                    }
                    break; // 每个情绪类型只匹配一次
                }
            }
        }

        if (matchedTags.isEmpty()) {
            return null; // 未检测到负面情绪
        }

        // 去重并限制标签数量
        Set<String> uniqueTags = new LinkedHashSet<>(matchedTags);
        if (uniqueTags.size() > 5) {
            uniqueTags = new LinkedHashSet<>(new ArrayList<>(uniqueTags).subList(0, 5));
        }

        // 保存记录
        EmotionRecord record = new EmotionRecord();
        record.setUserId(userId);
        record.setContent(content.length() > 500 ? content.substring(0, 500) : content);
        record.setEmotion(primaryEmotion);
        record.setEmotionLevel(maxLevel);
        record.setCreateTime(LocalDateTime.now());
        record.setAiReply(aiReply != null && aiReply.length() > 300 ? aiReply.substring(0, 300) : aiReply);

        try {
            record.setEmotionTags(objectMapper.writeValueAsString(new ArrayList<>(uniqueTags)));
        } catch (Exception e) {
            record.setEmotionTags("[\"" + String.join("\",\"", uniqueTags) + "\"]");
        }

        // 确保用户存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            // 自动创建用户（演示模式）
            User newUser = new User();
            newUser.setId(userId);
            newUser.setUsername("用户" + userId);
            newUser.setPassword("default");
            userMapper.insert(newUser);
        }

        try {
            emotionRecordMapper.insert(record);
            System.out.println("【情绪检测】检测到用户" + userId + "情绪波动: " + matchedTags + ", 级别:" + maxLevel);
        } catch (Exception e) {
            System.err.println("【情绪检测】保存情绪记录失败: " + e.getMessage());
        }

        return record;
    }

    /**
     * 估算情绪严重级别
     */
    private int estimateLevel(String content, String keyword) {
        String lower = content.toLowerCase();
        int count = 0;
        for (String kw : new String[]{keyword}) {
            int idx = 0;
            while ((idx = lower.indexOf(kw.toLowerCase(), idx)) != -1) {
                count++;
                idx += kw.length();
            }
        }

        // 根据关键词数量和修饰词判断级别
        boolean hasExtreme = lower.contains("特别") || lower.contains("非常") || lower.contains("极其")
                || lower.contains("超级") || lower.contains("好") || lower.contains("太") || lower.contains("好");
        boolean hasQuestion = lower.contains("怎么办") || lower.contains("怎么") || lower.contains("?")
                || lower.contains("？") || lower.contains("救救");

        if (count >= 3 || (hasExtreme && hasQuestion)) {
            return 3; // 严重
        } else if (count >= 2 || hasExtreme) {
            return 2; // 中等
        } else {
            return 1; // 轻微
        }
    }

    /**
     * 查询用户情绪记录列表
     */
    public List<EmotionRecord> getRecordList(Long userId) {
        LambdaQueryWrapper<EmotionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmotionRecord::getUserId, userId)
                .orderByDesc(EmotionRecord::getCreateTime)
                .last("LIMIT 50");
        return emotionRecordMapper.selectList(wrapper);
    }

    /**
     * 获取近N条未读情绪预警（最近7天）
     */
    public List<EmotionRecord> getRecentAlerts(Long userId) {
        LambdaQueryWrapper<EmotionRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(EmotionRecord::getUserId, userId)
                .ge(EmotionRecord::getCreateTime, LocalDateTime.now().minusDays(7))
                .orderByDesc(EmotionRecord::getCreateTime);
        return emotionRecordMapper.selectList(wrapper);
    }

    /**
     * 删除情绪记录
     */
    public boolean deleteRecord(Long id) {
        return emotionRecordMapper.deleteById(id) > 0;
    }
}
