package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("volunteer")
public class Volunteer {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long publisherId;

    @TableField(exist = false)
    private String publisherName;

    private String title;
    private String description;
    private String location;
    private LocalDate startDate;
    private String startTime;
    private String endTime;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private BigDecimal hours;
    private Integer status;
    private LocalDateTime createTime;

    private Integer approvalStatus;
    private String approvalRemark;
    private LocalDateTime approvalTime;
    private Long approvalUserId;
}
