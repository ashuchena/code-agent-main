package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ai_model_config")
public class AiModelConfig {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String modelKey;
    private Integer isActive;
    private Integer enabled;
    private String description;

    @TableField(exist = false)
    private String createTime;
}
