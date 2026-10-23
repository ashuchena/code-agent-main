package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("feedback")
public class Feedback {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;           // 用户ID
    private String userName;        // 用户姓名
    private String userPhone;      // 联系电话
    
    private String type;            // 反馈类型：ask, suggest, dining, dorm, course, safety
    private String title;           // 反馈标题
    private String content;         // 反馈内容
    private String images;          // 图片URLs
    
    // 额外字段
    private String targetTeacher;   // 目标老师（ask类型）
    private String courseName;      // 课程名称（course类型）
    private String location;        // 地点
    private Integer urgency;         // 紧急程度：0-普通, 1-紧急, 2-非常紧急
    
    private Integer status;         // 状态：0-待处理, 1-处理中, 2-已回复, 3-已完成
    private String reply;           // 回复内容
    private String replierName;     // 回复人姓名
    private LocalDateTime replyTime; // 回复时间
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
