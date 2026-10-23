package com.campus.agent.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.agent.entity.Repair;

import java.util.List;

public interface RepairService extends IService<Repair> {
    boolean addRepair(Repair repair);
    List<Repair> getRepairList(Long userId);
    List<Repair> getAllRepairList();
    boolean updateStatus(Long id, Integer status, String remark);
}
