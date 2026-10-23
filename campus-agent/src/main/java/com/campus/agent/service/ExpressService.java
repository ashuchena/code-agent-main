package com.campus.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.agent.entity.Express;
import com.campus.agent.entity.User;
import com.campus.agent.mapper.ExpressMapper;
import com.campus.agent.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpressService {

    @Autowired
    private ExpressMapper expressMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 发布快递代取
     */
    public boolean addExpress(Express express) {
        if (express.getPublisherId() == null) {
            throw new DataIntegrityViolationException("用户ID不能为空");
        }
        User user = userMapper.selectById(express.getPublisherId());
        if (user != null) {
            express.setPublisherName(user.getUsername());
        }
        express.setStatus(0);
        express.setApprovalStatus(0);
        express.setCreateTime(java.time.LocalDateTime.now());
        return expressMapper.insert(express) > 0;
    }

    /**
     * 查询待接单列表
     */
    public List<Express> getAvailableList() {
        LambdaQueryWrapper<Express> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Express::getStatus, 0)
                .eq(Express::getApprovalStatus, 1)
                .orderByDesc(Express::getCreateTime);
        return expressMapper.selectList(wrapper);
    }

    /**
     * 查询我的发布
     */
    public List<Express> getMyList(Long userId) {
        LambdaQueryWrapper<Express> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Express::getPublisherId, userId).orderByDesc(Express::getCreateTime);
        return expressMapper.selectList(wrapper);
    }

    /**
     * 查询我帮助的
     */
    public List<Express> getMyHelperList(Long userId) {
        LambdaQueryWrapper<Express> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Express::getHelperId, userId).orderByDesc(Express::getCreateTime);
        return expressMapper.selectList(wrapper);
    }

    /**
     * 接单
     */
    public boolean acceptOrder(Long id, Long helperId) {
        Express express = expressMapper.selectById(id);
        if (express != null && express.getStatus() == 0) {
            User user = userMapper.selectById(helperId);
            express.setStatus(1);
            express.setHelperId(helperId);
            if (user != null) {
                express.setHelperName(user.getUsername());
            }
            return expressMapper.updateById(express) > 0;
        }
        return false;
    }

    /**
     * 完成订单
     */
    public boolean completeOrder(Long id) {
        Express express = expressMapper.selectById(id);
        if (express != null && express.getStatus() == 1) {
            express.setStatus(2);
            express.setCompleteTime(java.time.LocalDateTime.now());
            return expressMapper.updateById(express) > 0;
        }
        return false;
    }

    /**
     * 删除订单
     */
    public boolean delete(Long id) {
        return expressMapper.deleteById(id) > 0;
    }
}
