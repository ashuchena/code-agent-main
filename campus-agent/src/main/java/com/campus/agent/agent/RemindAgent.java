package com.campus.agent.agent;

import com.campus.agent.entity.Remind;
import com.campus.agent.mapper.RemindMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 智能提醒Agent：添加提醒、时间解析
 */
@Component
public class RemindAgent {

    @Autowired
    private RemindMapper remindMapper;

    /**
     * 新增提醒，自动从原始用户输入中解析时间
     * @param userId  用户ID
     * @param originalContent 用户原始输入（含时间描述）
     * @return 是否创建成功（失败时抛出带说明的异常）
     */
    public boolean addRemind(Long userId, String originalContent) {
        Remind remind = new Remind();
        remind.setUserId(userId);
        remind.setStatus(0);

        // 解析提醒时间
        LocalDateTime remindTime = parseRemindTime(originalContent);
        remind.setRemindTime(remindTime);

        // 提取提醒内容（去除时间描述，保留事项）
        String content = extractContent(originalContent);
        remind.setContent(content);

        System.out.println("【提醒添加】userId=" + userId
                + " 内容=" + content
                + " 时间=" + remindTime);

        return remindMapper.insert(remind) > 0;
    }

    /**
     * 从用户输入中解析提醒时间
     * 支持：X分钟后、X小时后、今天/明天 X点、X点X分、下午/上午/晚上 X点
     */
    public LocalDateTime parseRemindTime(String content) {
        LocalDateTime now = LocalDateTime.now();

        // 1. X分钟后
        Pattern minPattern = Pattern.compile("(\\d+)\\s*分钟后");
        Matcher minMatcher = minPattern.matcher(content);
        if (minMatcher.find()) {
            return now.plusMinutes(Long.parseLong(minMatcher.group(1)));
        }

        // 2. X小时后
        Pattern hourPattern = Pattern.compile("(\\d+)\\s*小时后");
        Matcher hourMatcher = hourPattern.matcher(content);
        if (hourMatcher.find()) {
            return now.plusHours(Long.parseLong(hourMatcher.group(1)));
        }

        // 3. 半小时后
        if (content.contains("半小时后") || content.contains("半个小时后")) {
            return now.plusMinutes(30);
        }

        // 4. 明天 X点 or 明天X:XX
        boolean isTomorrow = content.contains("明天") || content.contains("明日");
        boolean isToday = content.contains("今天") || content.contains("今日");

        // 5. 上午/下午/晚上/早上 + X点
        LocalDateTime timeResult = parseTimeWithPeriod(content, now, isTomorrow);
        if (timeResult != null) {
            return timeResult;
        }

        // 6. 单纯 X点X分 或 X:XX
        LocalDateTime plainTime = parsePlainTime(content, now, isTomorrow, isToday);
        if (plainTime != null) {
            return plainTime;
        }

        // 7. 下午 / 晚上 只有时段没有具体时间
        if (content.contains("下午")) {
            return now.withHour(14).withMinute(0).withSecond(0).withNano(0)
                    .plusDays(isTomorrow ? 1 : 0);
        }
        if (content.contains("晚上") || content.contains("夜晚")) {
            return now.withHour(20).withMinute(0).withSecond(0).withNano(0)
                    .plusDays(isTomorrow ? 1 : 0);
        }
        if (content.contains("早上") || content.contains("上午")) {
            return now.withHour(8).withMinute(0).withSecond(0).withNano(0)
                    .plusDays(isTomorrow ? 1 : 0);
        }
        if (content.contains("中午")) {
            return now.withHour(12).withMinute(0).withSecond(0).withNano(0)
                    .plusDays(isTomorrow ? 1 : 0);
        }

        // 默认：1小时后
        return now.plusHours(1);
    }

    /**
     * 解析带时段的时间：上午X点、下午X点、晚上X点
     */
    private LocalDateTime parseTimeWithPeriod(String content, LocalDateTime now, boolean isTomorrow) {
        // 上午X点X分 / 上午X点X（支持有无"分"字）
        Pattern morningPattern = Pattern.compile("(上午|早上|早)(\\d{1,2})点(\\d{1,2}分?)?");
        Matcher morningMatcher = morningPattern.matcher(content);
        if (morningMatcher.find()) {
            int hour = Integer.parseInt(morningMatcher.group(2));
            int min = 0;
            if (morningMatcher.group(3) != null) {
                min = Integer.parseInt(morningMatcher.group(3).replaceAll("[^\\d]", ""));
            }
            return buildDateTime(now, hour, min, isTomorrow);
        }

        // 下午X点X分 / 下午X点X（支持有无"分"字）
        Pattern afternoonPattern = Pattern.compile("(下午|午后)(\\d{1,2})点(\\d{1,2}分?)?");
        Matcher afternoonMatcher = afternoonPattern.matcher(content);
        if (afternoonMatcher.find()) {
            int hour = Integer.parseInt(afternoonMatcher.group(2));
            if (hour < 12) hour += 12; // 下午2点 → 14
            int min = 0;
            if (afternoonMatcher.group(3) != null) {
                min = Integer.parseInt(afternoonMatcher.group(3).replaceAll("[^\\d]", ""));
            }
            return buildDateTime(now, hour, min, isTomorrow);
        }

        // 晚上X点X分 / 晚上X点X（支持有无"分"字）
        Pattern eveningPattern = Pattern.compile("(晚上|夜晚|夜里|晚)(\\d{1,2})点(\\d{1,2}分?)?");
        Matcher eveningMatcher = eveningPattern.matcher(content);
        if (eveningMatcher.find()) {
            int hour = Integer.parseInt(eveningMatcher.group(2));
            if (hour < 12) hour += 12; // 晚上8点 → 20
            int min = 0;
            if (eveningMatcher.group(3) != null) {
                min = Integer.parseInt(eveningMatcher.group(3).replaceAll("[^\\d]", ""));
            }
            return buildDateTime(now, hour, min, isTomorrow);
        }

        // 中午X点X分 / 中午X点X（支持有无"分"字）
        Pattern noonPattern = Pattern.compile("(中午)(\\d{1,2})点(\\d{1,2}分?)?");
        Matcher noonMatcher = noonPattern.matcher(content);
        if (noonMatcher.find()) {
            int hour = Integer.parseInt(noonMatcher.group(2));
            if (hour < 12) hour += 12;
            int min = 0;
            if (noonMatcher.group(3) != null) {
                min = Integer.parseInt(noonMatcher.group(3).replaceAll("[^\\d]", ""));
            }
            return buildDateTime(now, hour, min, isTomorrow);
        }

        return null;
    }

    /**
     * 解析纯时间格式：X点、X点X分、X点X（无分字）、X:XX
     */
    private LocalDateTime parsePlainTime(String content, LocalDateTime now, boolean isTomorrow, boolean isToday) {
        // X点X分（有"分"字）
        Pattern chineseTimePattern = Pattern.compile("(\\d{1,2})点(\\d{1,2})分");
        Matcher chineseMatcher = chineseTimePattern.matcher(content);
        if (chineseMatcher.find()) {
            int hour = Integer.parseInt(chineseMatcher.group(1));
            int min = Integer.parseInt(chineseMatcher.group(2));
            // 智能补充am/pm：如果小时<8且没有"上午"，推测为下午
            if (hour >= 1 && hour <= 7 && !content.contains("上午") && !content.contains("早")) {
                hour += 12;
            }
            return buildDateTime(now, hour, min, isTomorrow);
        }

        // X点X（无"分"字，如"3点20"）—— 需排除后面跟着非数字字符的情况
        Pattern chineseTimeNoFen = Pattern.compile("(\\d{1,2})点(\\d{1,2})(?!分)");
        Matcher noFenMatcher = chineseTimeNoFen.matcher(content);
        if (noFenMatcher.find()) {
            int hour = Integer.parseInt(noFenMatcher.group(1));
            int min = Integer.parseInt(noFenMatcher.group(2));
            if (hour >= 1 && hour <= 7 && !content.contains("上午") && !content.contains("早")) {
                hour += 12;
            }
            return buildDateTime(now, hour, min, isTomorrow);
        }

        // X点整
        Pattern hourOnlyPattern = Pattern.compile("(\\d{1,2})点");
        Matcher hourOnlyMatcher = hourOnlyPattern.matcher(content);
        if (hourOnlyMatcher.find()) {
            int hour = Integer.parseInt(hourOnlyMatcher.group(1));
            if (hour >= 1 && hour <= 7 && !content.contains("上午") && !content.contains("早")) {
                hour += 12;
            }
            return buildDateTime(now, hour, 0, isTomorrow);
        }

        // X:XX 格式
        Pattern colonPattern = Pattern.compile("(\\d{1,2}):(\\d{2})");
        Matcher colonMatcher = colonPattern.matcher(content);
        if (colonMatcher.find()) {
            int hour = Integer.parseInt(colonMatcher.group(1));
            int min = Integer.parseInt(colonMatcher.group(2));
            return buildDateTime(now, hour, min, isTomorrow);
        }

        return null;
    }

    /**
     * 构建目标日期时间，如果时间已过则自动推到明天
     */
    private LocalDateTime buildDateTime(LocalDateTime now, int hour, int min, boolean isTomorrow) {
        LocalDateTime target = now.withHour(hour).withMinute(min).withSecond(0).withNano(0);
        if (isTomorrow) {
            target = target.plusDays(1);
        } else if (target.isBefore(now)) {
            // 时间已过，推到明天
            target = target.plusDays(1);
        }
        return target;
    }

    /**
     * 提取提醒内容（去掉时间描述）
     */
    private String extractContent(String content) {
        // 去掉常见的时间描述词
        String cleaned = content
                .replaceAll("(明天|明日|今天|今日)", "")
                .replaceAll("(上午|下午|晚上|早上|中午|夜晚|夜里|晚)", "")
                .replaceAll("\\d+小时后", "")
                .replaceAll("\\d+分钟后", "")
                .replaceAll("半(个)?小时后", "")
                .replaceAll("\\d{1,2}点\\d{0,2}分?", "")
                .replaceAll("\\d{1,2}:\\d{2}", "")
                .replaceAll("提醒我|帮我提醒|记得|提醒一下", "")
                .replaceAll("\\s+", " ")
                .trim();
        // 如果清理后为空，使用原始内容
        return cleaned.isEmpty() ? content : cleaned;
    }
}
