package com.campus.agent.controller;

import com.campus.agent.entity.EmotionRecord;
import com.campus.agent.service.EmotionRecordService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 情绪记录控制器：查询情绪预警列表
 */
@RestController
@RequestMapping("/emotion")
public class EmotionRecordController {

    @Autowired
    private EmotionRecordService emotionRecordService;

    /**
     * 获取用户情绪预警列表（最近7天）
     */
    @GetMapping("/alerts")
    public Result<List<EmotionRecord>> getRecentAlerts(@RequestParam Long userId) {
        try {
            List<EmotionRecord> alerts = emotionRecordService.getRecentAlerts(userId);
            return Result.success(alerts);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询情绪预警失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户所有情绪记录
     */
    @GetMapping("/list")
    public Result<List<EmotionRecord>> getRecordList(@RequestParam Long userId) {
        try {
            List<EmotionRecord> list = emotionRecordService.getRecordList(userId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询情绪记录失败：" + e.getMessage());
        }
    }

    /**
     * 删除情绪记录
     */
    @DeleteMapping("/delete")
    public Result<Boolean> deleteRecord(@RequestParam Long id) {
        try {
            boolean flag = emotionRecordService.deleteRecord(id);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
