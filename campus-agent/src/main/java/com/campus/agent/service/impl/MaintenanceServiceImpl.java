package com.campus.agent.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.campus.agent.entity.Maintenance;
import com.campus.agent.mapper.MaintenanceMapper;
import com.campus.agent.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    @Autowired
    private MaintenanceMapper maintenanceMapper;

    @Override
    public boolean addMaintenance(Maintenance maintenance) {
        maintenance.setStatus(0); // 待处理
        maintenance.setCreateTime(LocalDateTime.now());
        return maintenanceMapper.insert(maintenance) > 0;
    }

    @Override
    public List<Maintenance> getMaintenanceList(Long userId) {
        QueryWrapper<Maintenance> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("create_time");
        return maintenanceMapper.selectList(wrapper);
    }

    @Override
    public List<Maintenance> getAllMaintenanceList() {
        QueryWrapper<Maintenance> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return maintenanceMapper.selectList(wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status, String remark) {
        Maintenance maintenance = new Maintenance();
        maintenance.setId(id);  // 必须设置主键，否则 updateById 无法工作
        maintenance.setStatus(status);
        if (remark != null) {
            maintenance.setRemark(remark);
        }
        if (status == 2) {
            maintenance.setCompleteTime(LocalDateTime.now());
        }
        return maintenanceMapper.updateById(maintenance) > 0;
    }

    @Override
    public Maintenance getMaintenanceById(Long id) {
        return maintenanceMapper.selectById(id);
    }

    @Override
    public boolean deleteMaintenance(Long id) {
        return maintenanceMapper.deleteById(id) > 0;
    }
}
