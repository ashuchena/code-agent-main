package com.campus.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.agent.entity.PartTime;
import com.campus.agent.entity.User;
import com.campus.agent.mapper.PartTimeMapper;
import com.campus.agent.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartTimeService {

    @Autowired
    private PartTimeMapper partTimeMapper;

    @Autowired
    private UserMapper userMapper;

    public boolean addPartTime(PartTime partTime) {
        if (partTime.getPublisherId() == null) {
            throw new DataIntegrityViolationException("用户ID不能为空");
        }
        User user = userMapper.selectById(partTime.getPublisherId());
        if (user != null) {
            partTime.setPublisherName(user.getUsername());
        }
        partTime.setStatus(0);
        partTime.setApprovalStatus(0);
        partTime.setCreateTime(LocalDateTime.now());
        return partTimeMapper.insert(partTime) > 0;
    }

    public List<PartTime> getAvailableList(String workType, Long userId) {
        LambdaQueryWrapper<PartTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartTime::getStatus, 0)
                .and(query -> query.eq(PartTime::getApprovalStatus, 1)
                        .or(userId != null, own -> own.eq(PartTime::getPublisherId, userId)));
        if (workType != null && !workType.isEmpty()) {
            wrapper.eq(PartTime::getWorkType, workType);
        }
        wrapper.orderByDesc(PartTime::getCreateTime);
        return partTimeMapper.selectList(wrapper);
    }

    public List<PartTime> getMyList(Long userId) {
        LambdaQueryWrapper<PartTime> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartTime::getPublisherId, userId).orderByDesc(PartTime::getCreateTime);
        return partTimeMapper.selectList(wrapper);
    }

    public boolean closeRecruitment(Long id) {
        PartTime partTime = partTimeMapper.selectById(id);
        if (partTime != null) {
            partTime.setStatus(1);
            return partTimeMapper.updateById(partTime) > 0;
        }
        return false;
    }

    public boolean delete(Long id) {
        return partTimeMapper.deleteById(id) > 0;
    }
}
