package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("leave_request")
public class Leave {
    @TableId(type = IdType.AUTO)
    private Long id; // 请假ID
    private Long userId; // 申请人ID
    private String userName; // 申请人姓名
    private String leaveType; // 请假类型：事假/病假/公假
    private LocalDate startDate; // 开始日期
    private LocalDate endDate; // 结束日期
    private String reason; // 请假原因
    private String contactPhone; // 联系电话
    private Integer status; // 状态：0-待审批，1-已批准，2-已拒绝
    private String approveRemark; // 审批备注
    private LocalDateTime createTime; // 申请时间
    private LocalDateTime approveTime; // 审批时间
}
