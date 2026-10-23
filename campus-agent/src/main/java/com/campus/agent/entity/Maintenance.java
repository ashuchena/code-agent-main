package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("maintenance")
public class Maintenance {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;           // 报修人ID
    private String userName;        // 报修人姓名
    private String userPhone;      // 报修人电话
    
    private String category;        // 报修类别：water, furniture, network, door, other
    
    private String location;        // 报修地点
    private String description;     // 问题描述
    private String images;          // 图片URLs
    
    private Integer status;         // 状态：0-待处理，1-处理中，2-已完成
    
    private String handlerName;     // 处理人姓名
    private String handlerPhone;     // 处理人电话
    private String handleTime;      // 预约处理时间
    private String remark;          // 处理备注
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    private LocalDateTime completeTime;  // 完成时间
}
