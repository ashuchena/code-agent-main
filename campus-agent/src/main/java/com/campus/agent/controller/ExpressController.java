package com.campus.agent.controller;

import com.campus.agent.entity.Express;
import com.campus.agent.service.ExpressService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/express")
public class ExpressController {

    @Autowired
    private ExpressService expressService;

    /**
     * 发布快递代取
     */
    @PostMapping("/add")
    public Result<Boolean> add(@RequestBody Express express) {
        try {
            boolean flag = expressService.addExpress(express);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("发布失败：" + e.getMessage());
        }
    }

    /**
     * 查询待接单列表
     */
    @GetMapping("/available")
    public Result<List<Express>> getAvailableList() {
        try {
            List<Express> list = expressService.getAvailableList();
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询我的发布
     */
    @GetMapping("/my")
    public Result<List<Express>> getMyList(@RequestParam Long userId) {
        try {
            List<Express> list = expressService.getMyList(userId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 查询我帮助的
     */
    @GetMapping("/helper")
    public Result<List<Express>> getMyHelperList(@RequestParam Long userId) {
        try {
            List<Express> list = expressService.getMyHelperList(userId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 接单
     */
    @PutMapping("/accept")
    public Result<Boolean> accept(@RequestParam Long id, @RequestParam Long helperId) {
        try {
            boolean flag = expressService.acceptOrder(id, helperId);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("接单失败：" + e.getMessage());
        }
    }

    /**
     * 完成订单
     */
    @PutMapping("/complete")
    public Result<Boolean> complete(@RequestParam Long id) {
        try {
            boolean flag = expressService.completeOrder(id);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败：" + e.getMessage());
        }
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/delete")
    public Result<Boolean> delete(@RequestParam Long id) {
        try {
            boolean flag = expressService.delete(id);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
