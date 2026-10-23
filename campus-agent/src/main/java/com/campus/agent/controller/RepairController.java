package com.campus.agent.controller;

import com.campus.agent.entity.Repair;
import com.campus.agent.service.RepairService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repair")
public class RepairController {

    @Autowired
    private RepairService repairService;

    /**
     * 提交报修
     */
    @PostMapping("/add")
    public Result<Boolean> addRepair(@RequestBody Repair repair) {
        try {
            System.out.println("========== 收到报修申请 ==========");
            System.out.println("userId: " + repair.getUserId());
            System.out.println("type: " + repair.getType());
            System.out.println("location: " + repair.getLocation());
            System.out.println("description: " + repair.getDescription());
            System.out.println("==================================");
            boolean flag = repairService.addRepair(repair);
            System.out.println("保存结果: " + flag);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交报修失败：" + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    /**
     * 查询我的报修列表
     */
    @GetMapping("/list")
    public Result<List<Repair>> getRepairList(@RequestParam Long userId) {
        try {
            System.out.println("========== 查询报修列表 ==========");
            System.out.println("userId: " + userId);
            List<Repair> list = repairService.getRepairList(userId);
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
    public Result<List<Repair>> getAllRepairList() {
        try {
            List<Repair> list = repairService.getAllRepairList();
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
            boolean flag = repairService.updateStatus(id, status, remark);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败：" + e.getMessage());
        }
    }
}
