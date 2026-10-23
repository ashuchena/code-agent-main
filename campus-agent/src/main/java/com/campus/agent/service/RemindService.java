package com.campus.agent.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.agent.entity.Remind;
import com.campus.agent.entity.User;
import com.campus.agent.mapper.RemindMapper;
import com.campus.agent.mapper.UserMapper;
import com.campus.agent.util.PushPlusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 提醒服务
 */
@Service
public class RemindService {

    @Autowired
    private RemindMapper remindMapper;

    @Autowired
    private UserMapper userMapper;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String PUSH_URL = "http://www.pushplus.plus/send";

    /**
     * 添加提醒
     * @param remind 提醒实体
     * @return 是否成功
     */
    public boolean addRemind(Remind remind) {
        // 先检查用户是否存在
        if (remind.getUserId() == null) {
            throw new DataIntegrityViolationException("用户ID不能为空");
        }
        // 如果用户不存在，自动创建用户（演示模式）
        User user = userMapper.selectById(remind.getUserId());
        if (user == null) {
            System.out.println("用户不存在，自动创建用户: " + remind.getUserId());
            User newUser = new User();
            newUser.setId(remind.getUserId());
            newUser.setUsername("用户" + remind.getUserId());
            newUser.setPassword("default");
            userMapper.insert(newUser);
        }
        return remindMapper.insert(remind) > 0;
    }

    /**
     * 查询用户提醒列表
     * @param userId 用户ID
     * @return 提醒列表
     */
    public List<Remind> getRemindList(Long userId) {
        LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Remind::getUserId, userId).orderByDesc(Remind::getRemindTime);
        return remindMapper.selectList(wrapper);
    }

    /**
     * 更新提醒状态
     * @param remind 提醒实体
     * @return 是否成功
     */
    public boolean updateRemind(Remind remind) {
        return remindMapper.updateById(remind) > 0;
    }

    /**
     * 删除提醒
     * @param id 提醒ID
     * @return 是否成功
     */
    public boolean deleteRemind(Long id) {
        return remindMapper.deleteById(id) > 0;
    }

    /**
     * 检查并推送到期的提醒（供云函数调用）
     */
    public void checkAndPushReminders() {
        LocalDateTime now = LocalDateTime.now();
        // 查询：未完成 + 提醒时间<=当前时间
        LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Remind::getStatus, 0)
                .le(Remind::getRemindTime, now);
        List<Remind> remindList = remindMapper.selectList(wrapper);

        if (!remindList.isEmpty()) {
            System.out.println("=====【云函数触发】检查提醒=====" + now);
            for (Remind remind : remindList) {
                System.out.println("提醒ID：" + remind.getId() + " | 内容：" + remind.getContent()
                        + " | 重试次数：" + remind.getPushRetryCount());

                // 推送失败超过3次，标记完成，不再重试
                if (remind.getPushRetryCount() != null && remind.getPushRetryCount() >= 3) {
                    remind.setStatus(1);
                    remindMapper.updateById(remind);
                    System.out.println("  → 重试超限，标记完成");
                    continue;
                }

                User user = userMapper.selectById(remind.getUserId());
                if (user != null && user.getPushplusToken() != null && !user.getPushplusToken().isEmpty()) {
                    boolean success = sendPush(user.getPushplusToken(),
                            "⏰ 校园小管家提醒",
                            "<h3>" + remind.getContent() + "</h3><p>提醒时间：" + remind.getRemindTime() + "</p>");
                    if (success) {
                        remind.setStatus(1);
                        remindMapper.updateById(remind);
                        System.out.println("  → 推送成功");
                    } else {
                        remind.setPushRetryCount(
                                (remind.getPushRetryCount() == null ? 0 : remind.getPushRetryCount()) + 1);
                        remindMapper.updateById(remind);
                        System.out.println("  → 推送失败");
                    }
                } else {
                    remind.setStatus(1);
                    remindMapper.updateById(remind);
                }
            }
            System.out.println("=================================");
        }
    }

    /**
     * 发送 PushPlus 推送
     */
    private boolean sendPush(String token, String title, String content) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("title", title);
            body.put("content", content);
            body.put("contentType", "HTML");

            Map<String, Object> resp = restTemplate.postForObject(PUSH_URL, body, Map.class);
            return resp != null && "200".equals(String.valueOf(resp.get("code")));
        } catch (Exception e) {
            System.out.println("PushPlus 推送异常：" + e.getMessage());
            return false;
        }
    }
}

