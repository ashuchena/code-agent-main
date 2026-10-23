package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Long id; // 用户ID
    private String username; // 用户名
    private String password; // 密码
    private String pushplusToken; // PushPlus推送Token
    private String role; // 角色：user / admin
    private LocalDateTime createTime; // 创建时间
}

