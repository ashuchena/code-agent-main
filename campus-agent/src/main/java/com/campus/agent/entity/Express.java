package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("express")
public class Express {
    @TableId(type = IdType.AUTO)
    private Long id; // ID
    private Long publisherId; // 发布人ID
    private String publisherName; // 发布人姓名
    private String publisherPhone; // 发布人电话
    private String expressCompany; // 快递公司
    private String trackingNo; // 快递单号
    private String pickupCode; // 取件码
    private String location; // 快递所在地点
    private BigDecimal reward; // 报酬
    private String remark; // 备注
    private Integer status; // 状态：0-待接单，1-进行中，2-已完成
    private Integer approvalStatus; // 审核状态：0-待审核，1-已通过，2-已拒绝
    private String approvalRemark; // 审核备注
    private LocalDateTime approvalTime; // 审核时间
    private Long approvalUserId; // 审核人ID
    private Long helperId; // 帮助者ID
    private String helperName; // 帮助者姓名
    private LocalDateTime createTime; // 发布时间
    private LocalDateTime completeTime; // 完成时间
}
