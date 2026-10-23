package com.campus.agent.agent;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 意图解析器：识别用户意图，提取关键信息
 */
@Component
public class IntentResolver {

    /**
     * 解析用户输入
     * @param content 用户输入内容
     * @return Map：key=intent(意图), key=keywords(关键词)
     */
    public Map<String, Object> resolve(String content) {
        Map<String, Object> result = new HashMap<>();
        content = content.trim().toLowerCase();
        IntentType intentType = IntentType.UNKNOWN;
        String keywords = content;

        // 打印日志，方便调试
        System.out.println("========== IntentResolver ==========");
        System.out.println("原始内容: " + content);

        // 1. 匹配学习Agent：课表、上课、课程、今日课表、明日课表
        if (content.contains("课表") || content.contains("上课") || content.contains("课程")) {
            intentType = IntentType.STUDY_SCHEDULE;
            System.out.println("→ 匹配到：STUDY_SCHEDULE");
        }
        // 2. 匹配提醒Agent：提醒、记得、帮我记、下课后、明天上午
        else if (content.contains("提醒") || content.contains("记得") || content.contains("帮我记")) {
            intentType = IntentType.REMIND_ADD;
            // 提取提醒内容（去除"提醒我"等前缀）
            keywords = content.replace("提醒我", "").replace("帮我提醒", "").replace("记得", "").trim();
            System.out.println("→ 匹配到：REMIND_ADD");
        }
        // 3. 【增强】匹配报修Agent：我要报修、申请报修、东西坏了、报修申请
        // 扩展关键词：水龙头、门锁、灯、空调、桌椅、床、窗户、下水道、网络断了、厕所坏了等
        else if (content.contains("报修") || content.contains("需要维修")
                || content.contains("设备故障")
                // 【新增】各种报修场景
                || (content.contains("坏了") && !content.contains("不需要") && !content.contains("不用"))
                || content.contains("水龙头") || content.contains("门锁")
                || content.contains("网络断了") || content.contains("网断了")
                || content.contains("空调不制冷") || content.contains("空调坏了")
                || content.contains("灯不亮") || content.contains("灯坏了")
                || content.contains("窗户") && (content.contains("关不上") || content.contains("坏了"))
                || content.contains("厕所") && (content.contains("堵") || content.contains("漏") || content.contains("坏"))
                || content.contains("下水道") && (content.contains("堵") || content.contains("漏"))
                || content.contains("花洒") || content.contains("热水器")
                || content.contains("马桶") || content.contains("水表")
                // 【增强】更多水管/漏水/堵塞类关键词
                || content.contains("水管") || content.contains("漏水")
                || content.contains("堵塞") || content.contains("不通")
                || content.contains("裂了") || content.contains("断了")
                || content.contains("门锁") || content.contains("打不开")
                || content.contains("椅子") || content.contains("桌子") || content.contains("床")
                || content.contains("宿舍") && (content.contains("坏") || content.contains("漏") || content.contains("堵"))
                || content.contains("教室") && (content.contains("坏") || content.contains("不能") || content.contains("漏水"))
                // 【新增】更多报修触发词
                || content.contains("报障") || content.contains("故障")
                || content.contains("需要修理") || content.contains("出问题")
                || content.contains("不能用了") || content.contains("坏了")) {
            intentType = IntentType.MAINTENANCE_ADD;  // 统一使用Maintenance表
            keywords = content;
            System.out.println("→ 匹配到：MAINTENANCE_ADD");
        }
        // 3.5 匹配维修登记（新独立模块）：我要登记维修、申请维修、提交维修
        else if (content.contains("维修登记") || content.contains("登记维修") || content.contains("提交维修")
                || content.contains("申请维修") || content.contains("我要维修")) {
            intentType = IntentType.MAINTENANCE_ADD;
            keywords = content;
        }
        // 4. 匹配生活Agent：食堂、快递、电话、菜鸟驿站、吃什么
        else if (content.contains("食堂") || content.contains("快递") || content.contains("电话")
                || content.contains("吃什么") || content.contains("菜鸟驿站")) {
            intentType = IntentType.LIFE_SERVICE;
        }
        // 5. 匹配资讯Agent：校历、放假、考试、办事指南、校园通知
        else if (content.contains("校历") || content.contains("放假") || content.contains("考试")
                || content.contains("办事") || content.contains("通知")) {
            intentType = IntentType.CAMPUS_INFO;
        }
        // 6. 匹配天气Agent：天气、气温、温度、下雨、下雪、晴、冷、热
        else if (content.contains("天气") || content.contains("气温") || content.contains("温度")
                || content.contains("下雨") || content.contains("下雪") || content.contains("晴")
                || content.contains("冷") || content.contains("热") || content.contains("风")) {
            intentType = IntentType.WEATHER;
        }

        result.put("intent", intentType);
        result.put("keywords", keywords);
        return result;
    }
}

