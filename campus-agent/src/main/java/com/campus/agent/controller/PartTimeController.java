package com.campus.agent.controller;

import com.campus.agent.entity.PartTime;
import com.campus.agent.service.PartTimeService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parttime")
public class PartTimeController {

    @Autowired
    private PartTimeService partTimeService;

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody PartTime partTime) {
        try {
            boolean flag = partTimeService.addPartTime(partTime);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发布失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<PartTime>> getAvailableList(@RequestParam(required = false) String workType,
                                                   @RequestParam(required = false) Long userId) {
        try {
            List<PartTime> list = partTimeService.getAvailableList(workType, userId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<PartTime>> getMyList(@RequestParam Long userId) {
        try {
            List<PartTime> list = partTimeService.getMyList(userId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @PutMapping("/close")
    public Result<Boolean> close(@RequestParam Long id) {
        try {
            boolean flag = partTimeService.closeRecruitment(id);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public Result<Boolean> delete(@RequestParam Long id) {
        try {
            boolean flag = partTimeService.delete(id);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
