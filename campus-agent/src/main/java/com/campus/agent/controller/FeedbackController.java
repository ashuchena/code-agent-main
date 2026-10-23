package com.campus.agent.controller;

import com.campus.agent.entity.Feedback;
import com.campus.agent.service.FeedbackService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    /**
     * 提交反馈
     */
    @PostMapping("/add")
    public Result<Boolean> addFeedback(@RequestBody Feedback feedback) {
        try {
            System.out.println("========== 收到反馈 ==========");
            System.out.println("userId: " + feedback.getUserId());
            System.out.println("type: " + feedback.getType());
            System.out.println("content: " + feedback.getContent());
            System.out.println("============================");
            boolean flag = feedbackService.addFeedback(feedback);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交反馈失败：" + e.getMessage());
        }
    }

    /**
     * 查询我的反馈列表
     */
    @GetMapping("/list")
    public Result<List<Feedback>> getFeedbackList(@RequestParam Long userId) {
        try {
            List<Feedback> list = feedbackService.getFeedbackList(userId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询反馈失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有反馈列表（管理员）
     */
    @GetMapping("/all")
    public Result<List<Feedback>> getAllFeedbackList() {
        try {
            List<Feedback> list = feedbackService.getAllFeedbackList();
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 更新反馈状态
     */
    @PutMapping("/update")
    public Result<Boolean> updateStatus(@RequestParam Long id,
                                        @RequestParam Integer status,
                                        @RequestParam(required = false) String reply) {
        try {
            boolean flag = feedbackService.updateStatus(id, status, reply);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取反馈详情
     */
    @GetMapping("/detail")
    public Result<Feedback> getFeedbackById(@RequestParam Long id) {
        try {
            Feedback feedback = feedbackService.getFeedbackById(id);
            return Result.success(feedback);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}
