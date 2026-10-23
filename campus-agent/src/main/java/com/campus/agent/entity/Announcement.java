package com.campus.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String title;       // 公告标题
    private String content;     // 公告内容
    private String type;        // 类型：notice/urgent/activity
    private Integer isTop;      // 是否置顶
    private Integer enabled;    // 是否显示
    private Long createBy;      // 发布人ID
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
