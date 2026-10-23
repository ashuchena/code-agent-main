package com.campus.agent.controller;

import com.campus.agent.entity.Remind;
import com.campus.agent.service.RemindService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 提醒控制器：添加提醒、查询提醒列表
 */
@RestController
@RequestMapping("/remind")
public class RemindController {

    @Autowired
    private RemindService remindService;

    /**
     * 添加提醒（说明书核心接口2）
     * @param remind 提醒实体
     * @return 操作结果
     */
    @PostMapping("/add")
    public Result<Boolean> addRemind(@RequestBody Remind remind) {
        System.out.println("========== 添加提醒 ==========");
        System.out.println("接收到的数据: " + remind);
        System.out.println("userId: " + (remind != null ? remind.getUserId() : "null"));
        System.out.println("content: " + (remind != null ? remind.getContent() : "null"));
        System.out.println("remindTime: " + (remind != null ? remind.getRemindTime() : "null"));
        System.out.println("================================");
        try {
            boolean flag = remindService.addRemind(remind);
            System.out.println("添加结果: " + flag);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("添加失败: " + e.getMessage());
            String message = e.getMessage();
            if (message != null && message.contains("用户不存在")) {
                return Result.error("请先登录后再添加提醒～");
            }
            if (message != null && message.contains("用户ID不能为空")) {
                return Result.error("请先登录后再添加提醒～");
            }
            return Result.error("添加提醒失败：" + message);
        }
    }

    /**
     * 查询用户提醒列表
     * @param userId 用户ID
     * @return 提醒列表
     */
    @GetMapping("/list")
    public Result<List<Remind>> getRemindList(@RequestParam Long userId) {
        try {
            List<Remind> list = remindService.getRemindList(userId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询提醒失败：" + e.getMessage());
        }
    }

    /**
     * 更新提醒状态
     * @param remind 提醒实体（包含id和status）
     * @return 操作结果
     */
    @PutMapping("/update")
    public Result<Boolean> updateRemind(@RequestBody Remind remind) {
        try {
            boolean flag = remindService.updateRemind(remind);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新提醒失败：" + e.getMessage());
        }
    }

    /**
     * 删除提醒
     * @param id 提醒ID
     * @return 操作结果
     */
    @DeleteMapping("/delete")
    public Result<Boolean> deleteRemind(@RequestParam Long id) {
        try {
            boolean flag = remindService.deleteRemind(id);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除提醒失败：" + e.getMessage());
        }
    }

    /**
     * 触发提醒检查（供云函数定时调用）
     * @param secret 校验密钥，防止他人滥用
     * @return 检查结果
     */
    @PostMapping("/trigger")
    public Result<String> triggerCheck(@RequestParam(required = false) String secret) {
        // 简单的密钥校验，可改成环境变量
        if (!"campus-agent-2026".equals(secret)) {
            return Result.error("授权失败");
        }
        try {
            remindService.checkAndPushReminders();
            return Result.success("触发成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("触发失败：" + e.getMessage());
        }
    }
}

