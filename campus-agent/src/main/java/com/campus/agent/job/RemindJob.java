package com.campus.agent.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.agent.entity.Remind;
import com.campus.agent.entity.User;
import com.campus.agent.mapper.RemindMapper;
import com.campus.agent.mapper.UserMapper;
import com.campus.agent.util.PushPlusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 提醒定时任务：每分钟扫描待提醒任务，并推送微信通知
 */
@Component
public class RemindJob {

    @Autowired
    private RemindMapper remindMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PushPlusUtil pushPlusUtil;

    /**
     * 定时任务：每10秒执行一次，减少推送延迟
     * cron "0/10 * * * * ?" = 每分钟第0、10、20、30、40、50秒执行
     */
    @Scheduled(cron = "0/10 * * * * ?")
    public void checkRemind() {
        LocalDateTime now = LocalDateTime.now();
        // 查询：未完成 + 提醒时间<=当前时间
        LambdaQueryWrapper<Remind> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Remind::getStatus, 0)
                .le(Remind::getRemindTime, now);
        List<Remind> remindList = remindMapper.selectList(wrapper);

        // 推送提醒（可扩展为WebSocket推送到前端）
        if (!remindList.isEmpty()) {
            System.out.println("=====【校园小管家提醒】=====" + now);
            for (Remind remind : remindList) {
                System.out.println("用户ID：" + remind.getUserId() + " | 提醒内容：" + remind.getContent()
                        + " | 重试次数：" + remind.getPushRetryCount());

                // 推送失败超过3次，标记完成，不再重试
                if (remind.getPushRetryCount() >= 3) {
                    System.out.println("  → 推送已重试3次，标记完成，不再重试");
                    remind.setStatus(1);
                    remindMapper.updateById(remind);
                    continue;
                }

                // 推送到微信，成功后标记完成
                User user = userMapper.selectById(remind.getUserId());
                if (user != null) {
                    String title = "⏰ 校园小管家提醒";
                    String content = "<h3>" + remind.getContent() + "</h3>"
                            + "<p>提醒时间：" + remind.getRemindTime() + "</p>";
                    boolean pushSuccess = pushPlusUtil.send(user, title, content);
                    if (pushSuccess) {
                        // 推送成功，标记为已完成
                        remind.setStatus(1);
                        remindMapper.updateById(remind);
                        System.out.println("  → 推送成功，已标记完成");
                    } else {
                        // 推送失败，重试次数+1
                        remind.setPushRetryCount(remind.getPushRetryCount() + 1);
                        remindMapper.updateById(remind);
                        System.out.println("  → 推送失败，重试次数：" + remind.getPushRetryCount());
                    }
                } else {
                    // 用户不存在，直接标记完成
                    remind.setStatus(1);
                    remindMapper.updateById(remind);
                }
            }
            System.out.println("===========================");
        }
    }
}

