package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("parttime")
public class PartTime {
    @TableId(type = IdType.AUTO)
    private Long id; // ID
    private Long publisherId; // 发布人ID
    private String publisherName; // 发布人姓名
    private String title; // 职位标题
    private String description; // 工作描述
    private String workType; // 工作类型：校内/校外
    private String location; // 工作地点
    private String workTime; // 工作时间
    private BigDecimal salary; // 薪资/小时
    private String requirement; // 要求
    private String contactPhone; // 联系电话
    private Integer status; // 状态：0-招聘中，1-已结束
    private Integer approvalStatus; // 审核状态：0-待审核，1-已通过，2-已拒绝
    private String approvalRemark; // 审核备注
    private LocalDateTime approvalTime; // 审核时间
    private Long approvalUserId; // 审核人ID
    private LocalDateTime createTime; // 发布时间
}
