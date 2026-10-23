package com.campus.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.agent.entity.Feedback;
import com.campus.agent.mapper.FeedbackMapper;
import com.campus.agent.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Override
    public boolean addFeedback(Feedback feedback) {
        feedback.setStatus(0); // 待处理
        feedback.setCreateTime(LocalDateTime.now());
        return feedbackMapper.insert(feedback) > 0;
    }

    @Override
    public List<Feedback> getFeedbackList(Long userId) {
        QueryWrapper<Feedback> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        return feedbackMapper.selectList(wrapper);
    }

    @Override
    public List<Feedback> getAllFeedbackList() {
        QueryWrapper<Feedback> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return feedbackMapper.selectList(wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status, String reply) {
        Feedback feedback = new Feedback();
        feedback.setId(id);
        feedback.setStatus(status);
        if (reply != null) {
            feedback.setReply(reply);
            feedback.setReplyTime(LocalDateTime.now());
        }
        return feedbackMapper.updateById(feedback) > 0;
    }

    @Override
    public Feedback getFeedbackById(Long id) {
        return feedbackMapper.selectById(id);
    }
}
