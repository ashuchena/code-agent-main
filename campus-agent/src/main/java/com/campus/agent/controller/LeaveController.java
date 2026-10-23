package com.campus.agent.controller;

import com.campus.agent.entity.Leave;
import com.campus.agent.entity.User;
import com.campus.agent.service.LeaveService;
import com.campus.agent.utils.Result;
import com.campus.agent.mapper.UserMapper;
import com.campus.agent.util.PushPlusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PushPlusUtil pushPlusUtil;

    /**
     * 提交请假申请
     */
    @PostMapping("/add")
    public Result<Boolean> addLeave(@RequestBody Leave leave) {
        try {
            System.out.println("收到请假申请: " + leave);
            boolean flag = leaveService.addLeave(leave);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("提交请假失败：" + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    /**
     * 查询我的请假列表
     */
    @GetMapping("/list")
    public Result<List<Leave>> getLeaveList(@RequestParam Long userId) {
        try {
            List<Leave> list = leaveService.getLeaveList(userId);
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询请假失败：" + e.getMessage());
        }
    }

    /**
     * 查询所有请假列表（管理员）
     */
    @GetMapping("/all")
    public Result<List<Leave>> getAllLeaveList() {
        try {
            List<Leave> list = leaveService.getAllLeaveList();
            return Result.success(list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 审批请假
     */
    @PutMapping("/approve")
    public Result<Boolean> approveLeave(@RequestParam Long id,
                                          @RequestParam Integer status,
                                          @RequestParam(required = false) String remark) {
        try {
            Leave leave = leaveService.getAllLeaveList().stream()
                    .filter(item -> item.getId().equals(id))
                    .findFirst()
                    .orElse(null);
            boolean flag = leaveService.approveLeave(id, status, remark);
            if (flag && leave != null) {
                User user = userMapper.selectById(leave.getUserId());
                if (user != null) {
                    String statusText = status != null && status == 1 ? "已批准" : "已拒绝";
                    String content = "<h3>你的请假申请审核结果已更新</h3>"
                            + "<p><b>请假类型：</b>" + (leave.getLeaveType() == null ? "-" : leave.getLeaveType()) + "</p>"
                            + "<p><b>请假时间：</b>" + leave.getStartDate() + " ~ " + leave.getEndDate() + "</p>"
                            + "<p><b>审核结果：</b>" + statusText + "</p>"
                            + "<p><b>审批备注：</b>" + (remark == null ? "" : remark) + "</p>";
                    pushPlusUtil.send(user, "请假审批结果通知", content);
                }
            }
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("审批失败：" + e.getMessage());
        }
    }

    /**
     * 删除请假记录
     */
    @DeleteMapping("/delete")
    public Result<Boolean> deleteLeave(@RequestParam Long id) {
        try {
            boolean flag = leaveService.deleteLeave(id);
            return Result.success(flag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败：" + e.getMessage());
        }
    }
}
