package com.campus.agent.controller;

import com.campus.agent.entity.LostFound;
import com.campus.agent.service.LostFoundService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lostfound")
public class LostFoundController {

    @Autowired
    private LostFoundService lostFoundService;

    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody LostFound lostFound) {
        try {
            boolean flag = lostFoundService.addLostFound(lostFound);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发布失败：" + e.getMessage());
        }
    }

    @GetMapping("/list")
    public Result<List<LostFound>> getList(@RequestParam(required = false) String type,
                                           @RequestParam(required = false) Long userId) {
        try {
            List<LostFound> list = lostFoundService.getList(type, userId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @GetMapping("/my")
    public Result<List<LostFound>> getMyList(@RequestParam Long userId) {
        try {
            List<LostFound> list = lostFoundService.getMyList(userId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @PutMapping("/resolve")
    public Result<Boolean> resolve(@RequestParam Long id) {
        try {
            boolean flag = lostFoundService.updateStatus(id);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public Result<Boolean> delete(@RequestParam Long id) {
        try {
            boolean flag = lostFoundService.delete(id);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
