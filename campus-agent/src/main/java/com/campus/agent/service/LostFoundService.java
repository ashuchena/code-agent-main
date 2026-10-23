package com.campus.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.agent.entity.LostFound;
import com.campus.agent.entity.User;
import com.campus.agent.mapper.LostFoundMapper;
import com.campus.agent.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LostFoundService {

    @Autowired
    private LostFoundMapper lostFoundMapper;

    @Autowired
    private UserMapper userMapper;

    public boolean addLostFound(LostFound lostFound) {
        if (lostFound.getUserId() == null) {
            throw new DataIntegrityViolationException("用户ID不能为空");
        }
        User user = userMapper.selectById(lostFound.getUserId());
        if (user != null) {
            lostFound.setUserName(user.getUsername());
        }
        lostFound.setStatus(0);
        lostFound.setApprovalStatus(0);
        lostFound.setCreateTime(LocalDateTime.now());
        return lostFoundMapper.insert(lostFound) > 0;
    }

    public List<LostFound> getList(String type, Long userId) {
        LambdaQueryWrapper<LostFound> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(query -> query.eq(LostFound::getApprovalStatus, 1)
                .or(userId != null, own -> own.eq(LostFound::getUserId, userId)));
        if (type != null && !type.isEmpty()) {
            wrapper.eq(LostFound::getType, type);
        }
        wrapper.orderByDesc(LostFound::getCreateTime);
        return lostFoundMapper.selectList(wrapper);
    }

    public List<LostFound> getMyList(Long userId) {
        LambdaQueryWrapper<LostFound> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LostFound::getUserId, userId).orderByDesc(LostFound::getCreateTime);
        return lostFoundMapper.selectList(wrapper);
    }

    public boolean updateStatus(Long id) {
        LostFound item = lostFoundMapper.selectById(id);
        if (item != null) {
            item.setStatus(1);
            return lostFoundMapper.updateById(item) > 0;
        }
        return false;
    }

    public boolean delete(Long id) {
        return lostFoundMapper.deleteById(id) > 0;
    }
}
