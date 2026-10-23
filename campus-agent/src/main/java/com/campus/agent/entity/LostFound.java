package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("lost_found")
public class LostFound {
    @TableId(type = IdType.AUTO)
    private Long id; // ID
    private Long userId; // 发布人ID
    private String userName; // 发布人姓名
    private String type; // 类型：lost-寻物，found-招领
    private String title; // 标题
    private String description; // 物品描述
    private String location; // 丢失/捡到地点
    private String contactPhone; // 联系方式
    private String imageUrl; // 图片URL
    private Integer status; // 状态：0-进行中，1-已找到/已归还
    private Integer approvalStatus; // 审核状态：0-待审核，1-已通过，2-已拒绝
    private String approvalRemark; // 审核备注
    private LocalDateTime approvalTime; // 审核时间
    private Long approvalUserId; // 审核人ID
    private LocalDateTime createTime; // 发布时间
}
