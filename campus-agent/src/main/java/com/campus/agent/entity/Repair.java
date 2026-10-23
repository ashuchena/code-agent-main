package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("repair")
public class Repair {
    @TableId(type = IdType.AUTO)
    private Long id; // 报修ID
    private Long userId; // 申请人ID
    private String userName; // 申请人姓名
    private String type; // 报修类型：water/furniture/network/door/other
    private String location; // 报修地点
    private String description; // 故障描述
    private String contact; // 联系方式
    private Integer status; // 状态：0-待处理，1-处理中，2-已完成
    private String remark; // 处理备注
    private LocalDateTime createTime; // 申请时间
    private LocalDateTime handleTime; // 处理时间
}
