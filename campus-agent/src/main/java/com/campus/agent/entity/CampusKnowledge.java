package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("campus_knowledge")
public class CampusKnowledge {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String category;

    private String question;

    private String normalizedQuestion;

    private String answer;

    private String sourceType;

    private String sourceFile;

    private Integer enabled;

    private Integer hitCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private Integer matchScore;
}
