package com.campus.agent.service;

import com.campus.agent.entity.Feedback;
import java.util.List;

public interface FeedbackService {
    // 提交反馈
    boolean addFeedback(Feedback feedback);
    
    // 查询用户的反馈列表
    List<Feedback> getFeedbackList(Long userId);
    
    // 查询所有反馈（管理员）
    List<Feedback> getAllFeedbackList();
    
    // 更新反馈状态
    boolean updateStatus(Long id, Integer status, String reply);
    
    // 获取反馈详情
    Feedback getFeedbackById(Long id);
}
