package com.campus.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.agent.entity.Leave;
import com.campus.agent.entity.User;
import com.campus.agent.mapper.LeaveMapper;
import com.campus.agent.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveService {

    @Autowired
    private LeaveMapper leaveMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 提交请假申请
     */
    public boolean addLeave(Leave leave) {
        if (leave.getUserId() == null) {
            throw new DataIntegrityViolationException("用户ID不能为空");
        }
        // 获取用户名
        User user = userMapper.selectById(leave.getUserId());
        if (user != null) {
            leave.setUserName(user.getUsername());
        }
        leave.setCreateTime(java.time.LocalDateTime.now());
        return leaveMapper.insert(leave) > 0;
    }

    /**
     * 查询用户的请假列表
     */
    public List<Leave> getLeaveList(Long userId) {
        LambdaQueryWrapper<Leave> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Leave::getUserId, userId).orderByDesc(Leave::getCreateTime);
        return leaveMapper.selectList(wrapper);
    }

    /**
     * 查询所有请假列表（管理员用）
     */
    public List<Leave> getAllLeaveList() {
        LambdaQueryWrapper<Leave> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(Leave::getCreateTime);
        return leaveMapper.selectList(wrapper);
    }

    /**
     * 审批请假
     */
    public boolean approveLeave(Long id, Integer status, String remark) {
        Leave leave = leaveMapper.selectById(id);
        if (leave != null) {
            leave.setStatus(status);
            leave.setApproveRemark(remark);
            leave.setApproveTime(java.time.LocalDateTime.now());
            return leaveMapper.updateById(leave) > 0;
        }
        return false;
    }

    /**
     * 删除请假记录
     */
    public boolean deleteLeave(Long id) {
        return leaveMapper.deleteById(id) > 0;
    }
}
