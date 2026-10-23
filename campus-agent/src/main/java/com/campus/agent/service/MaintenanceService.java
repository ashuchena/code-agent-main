package com.campus.agent.service;

import com.campus.agent.entity.Maintenance;
import java.util.List;

public interface MaintenanceService {
    // 提交报修
    boolean addMaintenance(Maintenance maintenance);
    
    // 查询用户的报修列表
    List<Maintenance> getMaintenanceList(Long userId);
    
    // 查询所有报修（管理员）
    List<Maintenance> getAllMaintenanceList();
    
    // 更新报修状态
    boolean updateStatus(Long id, Integer status, String remark);
    
    // 获取报修详情
    Maintenance getMaintenanceById(Long id);
    
    // 删除报修记录
    boolean deleteMaintenance(Long id);
}
