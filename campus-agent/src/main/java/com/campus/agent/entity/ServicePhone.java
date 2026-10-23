package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("service_phone")
public class ServicePhone {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String number;
    private String description;
    private Integer sortOrder;
    private Integer enabled;

    @TableField(exist = false)
    private String icon;

    @TableField(exist = false)
    private String iconBg;

    @TableField(exist = false)
    private LocalDateTime createTime;

    @TableField(exist = false)
    private LocalDateTime updateTime;
}
