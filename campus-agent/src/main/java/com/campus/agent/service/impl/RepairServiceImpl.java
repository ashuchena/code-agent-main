package com.campus.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.agent.entity.Repair;
import com.campus.agent.entity.User;
import com.campus.agent.mapper.RepairMapper;
import com.campus.agent.mapper.UserMapper;
import com.campus.agent.service.RepairService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean addRepair(Repair repair) {
        repair.setStatus(0); // 待处理
        repair.setCreateTime(LocalDateTime.now());
        // 获取用户名
        if (repair.getUserId() != null) {
            User user = userMapper.selectById(repair.getUserId());
            if (user != null) {
                repair.setUserName(user.getUsername());
            }
        }
        return this.save(repair);
    }

    @Override
    public List<Repair> getRepairList(Long userId) {
        QueryWrapper<Repair> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        return this.list(wrapper);
    }

    @Override
    public List<Repair> getAllRepairList() {
        QueryWrapper<Repair> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return this.list(wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status, String remark) {
        Repair repair = this.getById(id);
        if (repair == null) return false;
        repair.setStatus(status);
        if (remark != null) repair.setRemark(remark);
        if (status == 2) repair.setHandleTime(LocalDateTime.now());
        return this.updateById(repair);
    }
}
