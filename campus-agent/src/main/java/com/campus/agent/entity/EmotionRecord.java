package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 情绪记录：存储AI对话中检测到的负面情绪
 */
@Data
@TableName("emotion_record")
public class EmotionRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;                    // 关联用户ID
    private String content;                 // 用户原始输入内容
    private String emotion;                 // 检测到的情绪类型（焦虑/悲伤/愤怒/孤独/倦怠/压力大等）
    private Integer emotionLevel;           // 情绪严重程度（1-轻微 2-中等 3-严重）
    private String emotionTags;              // 情绪标签（JSON数组字符串，如["焦虑","失眠"]）
    private String aiReply;                 // AI当时的回复
    private LocalDateTime createTime;       // 检测时间
}
