package com.campus.agent.agent;

/**
 * 意图类型枚举
 */
public enum IntentType {
    STUDY_SCHEDULE, // 课表查询（学习Agent）
    LIFE_SERVICE,   // 生活服务（生活Agent）
    REMIND_ADD,     // 添加提醒（提醒Agent）
    CAMPUS_INFO,    // 校园资讯（资讯Agent）
    WEATHER,        // 天气查询（天气Agent）
    MAINTENANCE_ADD, // 维修登记（统一使用Maintenance表）
    UNKNOWN         // 未知意图
}

