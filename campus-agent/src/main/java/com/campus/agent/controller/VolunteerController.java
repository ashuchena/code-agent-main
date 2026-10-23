package com.campus.agent.controller;

import com.campus.agent.entity.Volunteer;
import com.campus.agent.entity.VolunteerSignUp;
import com.campus.agent.service.VolunteerService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/volunteer")
public class VolunteerController {

    @Autowired
    private VolunteerService volunteerService;

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Volunteer activity) {
        try {
            boolean flag = volunteerService.addActivity(activity);
            return Result.success(flag);
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            if (message != null && message.contains("approval_status")) {
                return Result.error("志愿者活动表缺少审核字段，请执行 SQL 迁移脚本后再发布");
            }
            return Result.error("创建失败：" + message);
        }
    }

    @GetMapping("/available")
    public Result<List<Volunteer>> getAvailableList() {
        try {
            List<Volunteer> list = volunteerService.getAvailableList();
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<Volunteer>> getAllList() {
        try {
            List<Volunteer> list = volunteerService.getAllList();
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @PostMapping("/signup")
    public Result<Boolean> signUp(@RequestParam Long activityId,
                                  @RequestParam Long userId,
                                  @RequestParam String userName) {
        try {
            boolean flag = volunteerService.signUp(activityId, userId, userName);
            if (flag) {
                return Result.success(true);
            }
            return Result.error("报名失败，请检查是否已报名或名额已满");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("报名失败：" + e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<VolunteerSignUp>> getMySignUps(@RequestParam Long userId) {
        try {
            List<VolunteerSignUp> list = volunteerService.getMySignUps(userId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/cancel")
    public Result<Boolean> cancelSignUp(@RequestParam Long activityId, @RequestParam Long userId) {
        try {
            boolean flag = volunteerService.cancelSignUp(activityId, userId);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("取消失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public Result<Boolean> delete(@RequestParam Long id) {
        try {
            boolean flag = volunteerService.delete(id);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
