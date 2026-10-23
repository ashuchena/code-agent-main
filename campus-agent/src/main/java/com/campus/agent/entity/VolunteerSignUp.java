package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("volunteer_signup")
public class VolunteerSignUp {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long userId;
    private String userName;
    private LocalDateTime signUpTime;
}
