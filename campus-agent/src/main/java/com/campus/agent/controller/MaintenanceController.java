package com.campus.agent.controller;

import com.campus.agent.entity.Maintenance;
import com.campus.agent.service.MaintenanceService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    /**
     * 提交报修
     */
    @PostMapping("/add")
    public Result<Boolean> addMaintenance(@RequestBody Maintenance maintenance) {
        try {
            System.out.println("========== 收到报修申请 ==========");
            System.out.println("userId: " + maintenance.getUserId());
            System.out.println("category: " + maintenance.getCategory());
            System.out.println("location: " + maintenance.getLocation());
            System.out.println("description: " + maintenance.getDescription());
            System.out.println("==================================");
            boolean flag = maintenanceService.addMaintenance(maintenance);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交报修失败：" + e.getMessage());
        }
    }

    /**
     * 查询我的报修列表
     */
    @GetMapping("/list")
    public Result<List<Maintenance>> getMaintenanceList(@RequestParam Long userId) {
        try {
            System.out.println("========== 查询报修列表 ==========");
            System.out.println("userId: " + userId);
            List<Maintenance> list = maintenanceService.getMaintenanceList(userId);
            System.out.println("查询到记录数: " + list.size());
            System.out.println("=================================");
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询报修失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有报修列表（管理员）
     */
    @GetMapping("/all")
    public Result<List<Maintenance>> getAllMaintenanceList() {
        try {
            List<Maintenance> list = maintenanceService.getAllMaintenanceList();
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 更新报修状态
     */
    @PutMapping("/update")
    public Result<Boolean> updateStatus(@RequestParam Long id,
                                        @RequestParam Integer status,
                                        @RequestParam(required = false) String remark) {
        try {
            boolean flag = maintenanceService.updateStatus(id, status, remark);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除报修记录
     */
    @DeleteMapping("/delete")
    public Result<Boolean> deleteMaintenance(@RequestParam Long id) {
        try {
            boolean flag = maintenanceService.deleteMaintenance(id);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }

    /**
     * 获取报修详情
     */
    @GetMapping("/detail")
    public Result<Maintenance> getMaintenanceById(@RequestParam Long id) {
        try {
            Maintenance maintenance = maintenanceService.getMaintenanceById(id);
            return Result.success(maintenance);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}
