package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("campus_knowledge_missing")
public class CampusKnowledgeMissing {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String normalizedQuestion;

    private String latestQuestion;

    private String answer;

    private Integer hitCount;

    private Integer promoted;

    private LocalDateTime lastHitAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
