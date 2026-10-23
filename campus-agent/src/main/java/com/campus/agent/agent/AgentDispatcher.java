package com.campus.agent.agent;

import com.campus.agent.service.ChatHistoryService;
import com.campus.agent.service.CampusKnowledgeService;
import com.campus.agent.service.DocumentParserService;
import com.campus.agent.service.QwenService;
import com.campus.agent.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调度中枢：统一分配任务，管理子Agent执行
 */
@Component
public class AgentDispatcher {

    @Autowired
    private StudyAgent studyAgent;
    @Autowired
    private LifeAgent lifeAgent;
    @Autowired
    private RemindAgent remindAgent;
    @Autowired
    private InfoAgent infoAgent;
    @Autowired
    private IntentResolver intentResolver;
    @Autowired
    private QwenService qwenService;
    @Autowired
    private ChatHistoryService chatHistoryService;
    @Autowired
    private CampusKnowledgeService campusKnowledgeService;
    @Autowired
    private DocumentParserService documentParserService;
    @Autowired
    private com.campus.agent.service.WeatherService weatherService;
    @Autowired
    private com.campus.agent.service.EmotionRecordService emotionRecordService;
    @Autowired
    private com.campus.agent.service.RepairService repairService;
    @Autowired
    private com.campus.agent.service.MaintenanceService maintenanceService;
    // 【新增】引入ScheduleService用于AI对话时提供用户课表上下文
    @Autowired
    private com.campus.agent.service.ScheduleService scheduleService;
    // 注意：DateUtil是静态工具类，无需@Autowired，直接静态调用

    /**
     * 调度核心方法
     * @param content 用户输入
     * @param userId  用户ID
     * @return 执行结果（文本/卡片数据）
     */
    public Map<String, Object> dispatch(String content, Long userId) {
        // 0. 先保存用户消息（MySQL可用时）
        try {
            chatHistoryService.saveMessage(userId, "user", content);
        } catch (Exception e) {
            // MySQL不可用时跳过历史记录，不影响对话核心功能
        }

        // 1. 意图解析
        Map<String, Object> resolveResult = intentResolver.resolve(content);
        IntentType intentType = (IntentType) resolveResult.get("intent");
        String keywords = (String) resolveResult.get("keywords");

        // 2. 根据意图调度对应子Agent
        Map<String, Object> result = new HashMap<>();
        result.put("intent", intentType.name());
        result.put("keywords", keywords);

        switch (intentType) {
            case STUDY_SCHEDULE:
                result.put("data", studyAgent.querySchedule(userId, DateUtil.getCurrentWeekday()));
                result.put("reply", "已为你查询到今日课表，请查看～");
                try { chatHistoryService.saveMessage(userId, "assistant", "已为你查询到今日课表，请查看～"); } catch (Exception ignored) {}
                break;
            case LIFE_SERVICE:
                com.campus.agent.entity.Knowledge lifeResult = lifeAgent.queryLifeService(keywords);
                if (lifeResult != null) {
                    result.put("data", lifeResult);
                    result.put("reply", lifeResult.getAnswer());
                } else {
                    // 知识库未匹配到 → fallback到通义千问AI对话（带历史上下文）
                    List<Map<String, String>> history = loadHistorySafely(userId);
                    String lifeSystemPrompt = "你是校园生活小管家。用户咨询生活服务类问题（食堂、快递、报修、校医等），"
                        + "但知识库中没有找到匹配的答案。请根据你的知识友好地回复用户，"
                        + "如果不确定可以建议用户咨询相关部门或拨打校园服务热线。"
                        + "请直接输出纯文本回复，不要使用Markdown格式，直接用自然语言回答即可。";
                    String lifeAiReply = qwenService.chatWithHistory(history, content, lifeSystemPrompt);
                    result.put("data", null);
                    result.put("reply", lifeAiReply);
                    // 【修复】不要保存AI错误消息到历史记录
                    if (!lifeAiReply.startsWith("AI服务调用出错")) {
                        try { chatHistoryService.saveMessage(userId, "assistant", lifeAiReply); } catch (Exception ignored) {}
                    }
                    // 情绪检测
                    try {
                        emotionRecordService.detectAndRecord(content, userId, lifeAiReply);
                    } catch (Exception e) {
                        System.err.println("【情绪检测】LIFE_SERVICE情绪检测失败: " + e.getMessage());
                    }
                }
                break;
            case REMIND_ADD:
                boolean addFlag;
                try {
                    // 传原始 content（含时间描述），让 RemindAgent 自行解析时间
                    addFlag = remindAgent.addRemind(userId, content);
                    if (addFlag) {
                        // 解析出时间，给用户友好提示
                        java.time.LocalDateTime remindTime = remindAgent.parseRemindTime(content);
                        String timeStr = remindTime.format(java.time.format.DateTimeFormatter.ofPattern("MM月dd日 HH:mm"));
                        String remindContent = content.replaceAll("提醒我|帮我提醒|记得|提醒一下", "").trim();
                        String successReply = "✅ 提醒已设置！\n\n📅 提醒时间：" + timeStr + "\n📝 提醒内容：" + remindContent + "\n\n到时间会推送通知到你的微信～";
                        result.put("data", true);
                        result.put("reply", successReply);
                        try { chatHistoryService.saveMessage(userId, "assistant", successReply); } catch (Exception ignored) {}
                    } else {
                        result.put("data", false);
                        result.put("reply", "提醒创建失败，请重试！");
                        try { chatHistoryService.saveMessage(userId, "assistant", "提醒创建失败，请重试！"); } catch (Exception ignored) {}
                    }
                } catch (Exception e) {
                    result.put("data", false);
                    result.put("reply", e.getMessage() != null ? e.getMessage() : "提醒创建失败，请重试！");
                    try { chatHistoryService.saveMessage(userId, "assistant", e.getMessage() != null ? e.getMessage() : "提醒创建失败，请重试！"); } catch (Exception ignored) {}
                }
                break;
            case CAMPUS_INFO:
                result.put("data", infoAgent.queryInfo(keywords));
                String campusReply = infoAgent.queryInfo(keywords) != null ? infoAgent.queryInfo(keywords).getAnswer() : "暂无相关资讯～";
                result.put("reply", campusReply);
                try { chatHistoryService.saveMessage(userId, "assistant", campusReply); } catch (Exception ignored) {}
                break;
            case WEATHER:
                // 天气查询
                Map<String, Object> weatherData = weatherService.getCurrentWeather();
                result.put("data", weatherData);
                String weatherReply = buildWeatherReply(weatherData);
                result.put("reply", weatherReply);
                try { chatHistoryService.saveMessage(userId, "assistant", weatherReply); } catch (Exception ignored) {}
                break;
            case MAINTENANCE_ADD:
                // 维修登记（新独立模块）
                try {
                    com.campus.agent.entity.Maintenance maintenance = parseMaintenanceFromText(content, userId);
                    boolean maintenanceFlag = maintenanceService.addMaintenance(maintenance);
                    String maintenanceReply = maintenanceFlag
                        ? "✅ 维修登记成功！\n\n📝 报修信息：\n类型：" + getCategoryNameCN(maintenance.getCategory()) + "\n地点：" + maintenance.getLocation() + "\n描述：" + maintenance.getDescription() + "\n\n我们会尽快安排人员处理，请保持电话（" + maintenance.getUserPhone() + "）畅通～"
                        : "维修登记失败，请稍后重试或拨打报修热线！";
                    result.put("data", maintenanceFlag);
                    result.put("reply", maintenanceReply);
                    try { chatHistoryService.saveMessage(userId, "assistant", maintenanceReply); } catch (Exception ignored) {}
                } catch (Exception e) {
                    result.put("data", false);
                    result.put("reply", "维修登记失败：" + (e.getMessage() != null ? e.getMessage() : "请稍后重试"));
                    try { chatHistoryService.saveMessage(userId, "assistant", result.get("reply").toString()); } catch (Exception ignored) {}
                }
                break;
            default:
                // 未知意图 → fallback到通义千问AI自由对话（带历史上下文 + 校园管家系统提示词）
                // 【增强】AI对话时关联用户课表和食堂信息
                List<Map<String, String>> history = loadHistorySafely(userId);
                // 获取用户上下文信息（课表、食堂等）
                String userContext = buildUserContext(userId, content);
                String systemPrompt = "你是校园生活小管家，一个友好、热情、专业的校园助手。"
                    + "你的职责是帮助大学生解决校园生活中的各种问题，包括但不限于："
                    + "学习咨询（课表查询、考试安排、图书馆使用等）、"
                    + "生活服务（食堂菜单、快递取件、报修服务、校医等）、"
                    + "校园资讯（校历安排、放假通知、办事指南等）、"
                    + "日常闲聊和情感陪伴。"
                    + userContext  // 【新增】注入用户个性化上下文
                    + "请用简洁友好的语气回复，如果不确定的问题建议用户咨询相关部门。"
                    + "请直接输出纯文本回复，不要使用Markdown格式，直接用自然语言回答即可。";
                String aiReply = qwenService.chatWithHistory(history, content, systemPrompt);
                result.put("data", null);
                result.put("reply", aiReply);
                // 【修复】不要保存AI错误消息到历史记录
                if (!aiReply.startsWith("AI服务调用出错")) {
                    try { chatHistoryService.saveMessage(userId, "assistant", aiReply); } catch (Exception ignored) {}
                }

                // 检测用户情绪，负面情绪时自动记录
                try {
                    emotionRecordService.detectAndRecord(content, userId, aiReply);
                } catch (Exception e) {
                    System.err.println("【情绪检测】检测失败: " + e.getMessage());
                }
                break;
        }
        return result;
    }

    /**
     * 安全加载历史记录（MySQL不可用时返回空列表）
     */
    private List<Map<String, String>> loadHistorySafely(Long userId) {
        try {
            return chatHistoryService.loadHistoryForQwen(userId);
        } catch (Exception e) {
            return new java.util.ArrayList<>();
        }
    }

    /**
     * 构建天气回复文本
     */
    private String buildWeatherReply(Map<String, Object> weatherData) {
        if (weatherData == null || !Boolean.TRUE.equals(weatherData.get("success"))) {
            return "抱歉，暂时无法获取天气信息，请稍后重试～";
        }
        String icon = (String) weatherData.getOrDefault("icon", "🌤️");
        String text = (String) weatherData.getOrDefault("text", "未知");
        String temp = (String) weatherData.getOrDefault("temp", "--");
        String windDir = (String) weatherData.getOrDefault("windDir", "未知");
        String humidity = (String) weatherData.getOrDefault("humidity", "--");
        return icon + " 今日天气\n\n" +
               "天气：" + text + "\n" +
               "温度：" + temp + "°C\n" +
               "风向：" + windDir + "\n" +
               "湿度：" + humidity + "%";
    }

    /**
     * 从用户输入中解析报修信息
     * 【增强】更智能的类型识别和地点提取
     */
    private com.campus.agent.entity.Repair parseRepairFromText(String content, Long userId) {
        com.campus.agent.entity.Repair repair = new com.campus.agent.entity.Repair();
        repair.setUserId(userId);
        repair.setStatus(0); // 待处理

        String lowerContent = content.toLowerCase();

        // 【增强】解析报修类型 - 使用更精确的匹配逻辑
        String type = "other";
        
        // 水电类（优先检查，避免"电脑"误匹配）
        if (lowerContent.contains("水龙头") || lowerContent.contains("水管") || lowerContent.contains("漏水") 
                || lowerContent.contains("下水道") || lowerContent.contains("马桶") || lowerContent.contains("厕所")
                || lowerContent.contains("花洒") || lowerContent.contains("热水器") || lowerContent.contains("水表")
                || (lowerContent.contains("电") && (lowerContent.contains("灯") || lowerContent.contains("插座") || lowerContent.contains("路")))
                || lowerContent.contains("停电") || lowerContent.contains("跳闸") || lowerContent.contains("电路")) {
            type = "water";
        }
        // 家具类
        else if (lowerContent.contains("桌椅") || lowerContent.contains("床") || lowerContent.contains("柜子") 
                || lowerContent.contains("椅子") || lowerContent.contains("桌子") || lowerContent.contains("家具")
                || lowerContent.contains("书架") || lowerContent.contains("门板")
                || (lowerContent.contains("电脑") || lowerContent.contains("笔记本"))) {
            type = "furniture";
        }
        // 网络类
        else if (lowerContent.contains("网络") || lowerContent.contains("wifi") || lowerContent.contains("网线")
                || lowerContent.contains("宽带") || lowerContent.contains("网断") || lowerContent.contains("断网")
                || lowerContent.contains("上网")) {
            type = "network";
        }
        // 门窗类
        else if (lowerContent.contains("门") || lowerContent.contains("窗") || lowerContent.contains("锁")
                || lowerContent.contains("钥匙") || lowerContent.contains("门锁") || lowerContent.contains("窗户")) {
            type = "door";
        }
        repair.setType(type);

        // 【增强】解析报修地点 - 支持更多模式
        String location = "未知地点";
        
        // 模式1：完整地点（栋号+建筑类型+房间号）
        java.util.regex.Pattern fullLocPattern = java.util.regex.Pattern.compile("[0-9]?[0-9]栋.*?(教学楼|宿舍|公寓).*");
        java.util.regex.Matcher fullLocMatcher = fullLocPattern.matcher(content);
        if (fullLocMatcher.find()) {
            location = fullLocMatcher.group();
        } else {
            // 模式2：宿舍/公寓+楼号
            java.util.regex.Pattern dormPattern = java.util.regex.Pattern.compile("[0-9]公寓|[0-9]号楼|[0-9]号楼[0-9]层|[0-9]{1,3}室");
            java.util.regex.Matcher dormMatcher = dormPattern.matcher(content);
            if (dormMatcher.find()) {
                location = dormMatcher.group();
            } else {
                // 模式3：简单地点关键词
                java.util.regex.Pattern locPattern = java.util.regex.Pattern.compile("(宿舍|教室|图书馆|食堂|操场|实验室|办公室|公寓)([\\S]{0,30})?");
                java.util.regex.Matcher locMatcher = locPattern.matcher(content);
                if (locMatcher.find()) {
                    location = locMatcher.group();
                }
            }
        }
        repair.setLocation(location);

        // 【增强】解析故障描述 - 清理冗余信息
        String description = content;
        // 移除地点信息，避免重复
        description = description.replaceAll("[0-9]?[0-9]栋.*?(教学楼|宿舍|公寓).*", "")
                               .replaceAll("[0-9]公寓|[0-9]号楼|[0-9]号楼[0-9]层|[0-9]{1,3}室", "")
                               .replaceAll("(宿舍|教室|图书馆|食堂|操场|实验室|办公室|公寓)[\\S]{0,30}", "")
                               .trim();
        if (description.isEmpty()) {
            description = content; // 如果清理后为空，保留原始内容
        }
        repair.setDescription(description);

        // 尝试提取联系方式
        java.util.regex.Pattern phonePattern = java.util.regex.Pattern.compile("1[3-9]\\d{9}");
        java.util.regex.Matcher phoneMatcher = phonePattern.matcher(content);
        if (phoneMatcher.find()) {
            repair.setContact(phoneMatcher.group());
        }

        return repair;
    }

    /**
     * 从用户输入中解析维修信息（新独立模块）
     * 【增强】更智能的类型识别和地点提取
     */
    private com.campus.agent.entity.Maintenance parseMaintenanceFromText(String content, Long userId) {
        com.campus.agent.entity.Maintenance maintenance = new com.campus.agent.entity.Maintenance();
        maintenance.setUserId(userId);
        maintenance.setStatus(0); // 待处理
        maintenance.setCreateTime(java.time.LocalDateTime.now());

        String lowerContent = content.toLowerCase();

        // 【增强】解析报修类型 - 使用更精确的匹配逻辑
        String type = "other";
        
        // 水电类（优先检查，避免"电脑"误匹配）
        if (lowerContent.contains("水龙头") || lowerContent.contains("水管") || lowerContent.contains("漏水") 
                || lowerContent.contains("下水道") || lowerContent.contains("马桶") || lowerContent.contains("厕所")
                || lowerContent.contains("花洒") || lowerContent.contains("热水器") || lowerContent.contains("水表")
                || (lowerContent.contains("电") && (lowerContent.contains("灯") || lowerContent.contains("插座") || lowerContent.contains("路")))
                || lowerContent.contains("停电") || lowerContent.contains("跳闸") || lowerContent.contains("电路")) {
            type = "water";
        }
        // 家具类
        else if (lowerContent.contains("桌椅") || lowerContent.contains("床") || lowerContent.contains("柜子") 
                || lowerContent.contains("椅子") || lowerContent.contains("桌子") || lowerContent.contains("家具")
                || lowerContent.contains("书架") || lowerContent.contains("门板")
                || (lowerContent.contains("电脑") || lowerContent.contains("笔记本"))) {
            type = "furniture";
        }
        // 网络类
        else if (lowerContent.contains("网络") || lowerContent.contains("wifi") || lowerContent.contains("网线")
                || lowerContent.contains("宽带") || lowerContent.contains("网断") || lowerContent.contains("断网")
                || lowerContent.contains("上网")) {
            type = "network";
        }
        // 门窗类
        else if (lowerContent.contains("门") || lowerContent.contains("窗") || lowerContent.contains("锁")
                || lowerContent.contains("钥匙") || lowerContent.contains("门锁") || lowerContent.contains("窗户")) {
            type = "door";
        }
        maintenance.setCategory(type);

        // 【增强】解析报修地点 - 支持更多模式
        String location = "未知地点";
        
        // 模式1：完整地点（栋号+建筑类型+房间号）
        java.util.regex.Pattern fullLocPattern = java.util.regex.Pattern.compile("[0-9]?[0-9]栋.*?(教学楼|宿舍|公寓).*");
        java.util.regex.Matcher fullLocMatcher = fullLocPattern.matcher(content);
        if (fullLocMatcher.find()) {
            location = fullLocMatcher.group();
        } else {
            // 模式2：宿舍/公寓+楼号
            java.util.regex.Pattern dormPattern = java.util.regex.Pattern.compile("[0-9]公寓|[0-9]号楼|[0-9]号楼[0-9]层|[0-9]{1,3}室");
            java.util.regex.Matcher dormMatcher = dormPattern.matcher(content);
            if (dormMatcher.find()) {
                location = dormMatcher.group();
            } else {
                // 模式3：简单地点关键词
                java.util.regex.Pattern locPattern = java.util.regex.Pattern.compile("(宿舍|教室|图书馆|食堂|操场|实验室|办公室|公寓|楼)([\\S]{0,30})?");
                java.util.regex.Matcher locMatcher = locPattern.matcher(content);
                if (locMatcher.find()) {
                    location = locMatcher.group();
                }
            }
        }
        maintenance.setLocation(location);

        // 【增强】解析故障描述 - 清理冗余信息
        String description = content;
        // 移除地点信息，避免重复
        description = description.replaceAll("[0-9]?[0-9]栋.*?(教学楼|宿舍|公寓).*", "")
                               .replaceAll("[0-9]公寓|[0-9]号楼|[0-9]号楼[0-9]层|[0-9]{1,3}室", "")
                               .replaceAll("(宿舍|教室|图书馆|食堂|操场|实验室|办公室|公寓|楼)[\\S]{0,30}", "")
                               .trim();
        if (description.isEmpty()) {
            description = content; // 如果清理后为空，保留原始内容
        }
        maintenance.setDescription(description);

        // 尝试提取联系方式（手机号）
        String phone = "";
        java.util.regex.Pattern phonePattern = java.util.regex.Pattern.compile("1[3-9]\\d{9}");
        java.util.regex.Matcher phoneMatcher = phonePattern.matcher(content);
        if (phoneMatcher.find()) {
            phone = phoneMatcher.group();
        }
        maintenance.setUserPhone(phone);

        return maintenance;
    }

    /**
     * 获取维修类型中文名称（报修用）
     */
    private String getRepairTypeNameCN(String type) {
        switch (type) {
            case "water": return "💧 水电维修";
            case "furniture": return "🪑 家具维修";
            case "network": return "📶 网络报修";
            case "door": return "🚪 门窗维修";
            default: return "🔧 其他";
        }
    }

    /**
     * 获取维修类型中文名称
     */
    private String getCategoryNameCN(String category) {
        switch (category) {
            case "water": return "💧 水电维修";
            case "furniture": return "🛋️ 家具维修";
            case "network": return "📶 网络报修";
            case "door": return "🚪 门窗维修";
            default: return "📦 其他";
        }
    }

    /**
     * 【新增】构建用户上下文信息（课表、食堂等）
     * 当用户询问课程、食堂相关内容时，提供个性化信息
     */
    private String buildUserContext(Long userId, String content) {
        StringBuilder context = new StringBuilder();
        String lowerContent = content.toLowerCase();

        // 【增强】如果用户询问课程相关问题，提供课表信息
        boolean isCourseRelated = lowerContent.contains("课") || lowerContent.contains("课程")
            || lowerContent.contains("上课") || lowerContent.contains("老师")
            || lowerContent.contains("教室") || lowerContent.contains("下节课")
            || lowerContent.contains("第一节") || lowerContent.contains("第二节")
            || lowerContent.contains("第三节") || lowerContent.contains("第四节")
            || lowerContent.contains("什么时候") || lowerContent.contains("几点");

        if (isCourseRelated) {
            try {
                // 获取今日课表
                Integer weekday = com.campus.agent.utils.DateUtil.getCurrentWeekday();
                List<com.campus.agent.entity.Schedule> todaySchedule = scheduleService.getTodaySchedule(userId, weekday);
                if (todaySchedule != null && !todaySchedule.isEmpty()) {
                    context.append("\n\n【用户今日课表信息】\n");
                    for (com.campus.agent.entity.Schedule s : todaySchedule) {
                        context.append("- ").append(s.getCourseName())
                            .append(" (").append(s.getPlace() != null ? s.getPlace() : "未知地点").append(")")
                            .append(" ").append(s.getTime() != null ? s.getTime() : "");
                        context.append("\n");
                    }
                } else {
                    context.append("\n\n【用户今日课表】暂无课程安排。\n");
                }
            } catch (Exception e) {
                System.err.println("【上下文构建】获取课表失败: " + e.getMessage());
            }
        }

        // 【增强】如果用户询问食堂相关问题，提供食堂信息
        boolean isDiningRelated = lowerContent.contains("食堂") || lowerContent.contains("吃饭")
            || lowerContent.contains("餐厅") || lowerContent.contains("美食")
            || lowerContent.contains("菜品") || lowerContent.contains("推荐菜")
            || lowerContent.contains("午餐") || lowerContent.contains("晚餐")
            || lowerContent.contains("早餐") || lowerContent.contains("几点吃饭")
            || lowerContent.contains("哪里吃");

        if (isDiningRelated) {
            context.append("\n\n【校园食堂参考信息】\n");
            context.append("- 第一食堂：靠近学生宿舍区，菜品种类丰富，早餐供应丰富\n");
            context.append("- 第二食堂：靠近教学楼菜系以川菜、湘菜为主\n");
            context.append("- 第三食堂：靠近图书馆附近，适合自习间隙用餐\n");
            context.append("- 第四食堂：清真餐厅，特色小吃较多\n");
            context.append("💡 建议：根据您当前时间和位置选择合适的食堂，如结合课表安排在课间用餐\n");
        }

        // 【增强】如果用户表达报修意图，自动识别并提示已提交
        // （这部分逻辑已在 REPAIR_ADD 和 MAINTENANCE_ADD case中处理）
        // 这里是额外的意图增强：如果用户只是提到问题但没有明确提交，自动引导
        boolean hasRepairImplication = (lowerContent.contains("坏了") || lowerContent.contains("坏了")
            || lowerContent.contains("报修") || lowerContent.contains("维修")
            || lowerContent.contains("有问题") || lowerContent.contains("东西坏了"))
            && !lowerContent.contains("不需要") && !lowerContent.contains("不用了");

        if (hasRepairImplication) {
            // 检查是否已经被自动提交（通过关键字判断）
            if (!lowerContent.contains("已提交") && !lowerContent.contains("登记成功")) {
                context.append("\n\n💡 温馨提示：您似乎需要报修服务，可以直接告诉我具体哪里坏了（如：宿舍门锁坏了、水龙头漏水等），我可以帮您快速登记报修！\n");
            }
        }

        return context.toString();
    }

    /**
     * 带图片的调度方法（多模态AI对话）
     * @param content 用户输入文本
     * @param userId  用户ID
     * @param imageBase64 图片Base64
     * @return AI回复结果
     */
    public Map<String, Object> dispatchWithImage(String content, Long userId, String imageBase64) {
        return dispatchWithImage(content, userId, imageBase64, null, null);
    }

    /**
     * 带图片和文档的调度方法（多模态AI对话 + 文档解析）
     * @param content 用户输入文本
     * @param userId  用户ID
     * @param imageBase64 图片Base64（可选）
     * @param docBase64   文档Base64（可选）
     * @param docName     文档名称（可选）
     * @return AI回复结果
     */
    public Map<String, Object> dispatchWithImage(String content, Long userId, String imageBase64, String docBase64, String docName) {
        Map<String, Object> result = new HashMap<>();
        result.put("intent", "UNKNOWN");
        result.put("keywords", "");

        // 如果有文档，解析内容
        String docContent = null;
        if (docBase64 != null && !docBase64.isEmpty() && docName != null && !docName.isEmpty()) {
            try {
                byte[] docBytes = Base64.getDecoder().decode(docBase64);
                docContent = documentParserService.parseDocument(docBytes, docName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 加载历史上下文
        List<Map<String, String>> history = loadHistorySafely(userId);

        // 构建系统提示词
        String systemPrompt = "你是校园生活小管家，一个友好、热情、专业的校园助手。";
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            systemPrompt += "用户发送了一张图片，请仔细观察图片内容，并结合你的知识给出友好、准确的回答。";
            systemPrompt += "如果图片是文字截图、表格、课程表、食堂菜单等，请识别并提取信息回答用户。";
        }
        if (docContent != null && !docContent.isEmpty()) {
            systemPrompt += "用户还上传了一个文档，内容如下：\n" + docContent;
        }
        systemPrompt += "请根据上述内容回答用户的问题。请直接输出纯文本回复，不要使用Markdown格式。";

        // 构建用户消息（包含文档内容）
        String userMessage = content != null ? content : "";
        if (docContent != null && !docContent.isEmpty()) {
            userMessage = docContent + "\n\n【用户问题】" + content;
        }

        // 调用AI
        String aiReply;
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            // 有图片，调用VL模型
            if (history != null && !history.isEmpty()) {
                aiReply = qwenService.chatWithImageAndHistory(history, userMessage, imageBase64, systemPrompt);
            } else {
                aiReply = qwenService.chatWithImage(userMessage, imageBase64, systemPrompt);
            }
        } else {
            // 只有文档，调用普通对话
            if (history != null && !history.isEmpty()) {
                aiReply = qwenService.chatWithHistory(history, userMessage, systemPrompt);
            } else {
                aiReply = qwenService.chatWithSystem(userMessage, systemPrompt);
            }
        }

        result.put("data", null);
        result.put("reply", aiReply);

        // 保存本轮对话记录（但不要保存AI错误消息）
        String combinedContent = content != null ? content : "";
        if (docName != null) {
            combinedContent += " [文档:" + docName + "]";
        }
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            combinedContent += " [图片]";
        }
        // 【修复】不要保存AI错误消息到历史记录
        if (!aiReply.startsWith("AI服务调用出错")) {
            try { chatHistoryService.saveMessage(userId, "assistant", aiReply); } catch (Exception ignored) {}
        }

        return result;
    }

    private String buildCampusOnlyPrompt(String userContext, String scene) {
        return "你是广州松田职业学院的校园智能助手。"
                + "请优先依据广州松田职业学院校内信息回答。"
                + "如果知识库没有直接答案，你可以结合已有常识做谨慎回答，但内容必须限制在广州松田职业学院范围内，不能扩展到其他学校。"
                + "如果无法确认，就明确说明暂未在广州松田职业学院知识库中找到确切信息，并建议咨询学校相关部门。"
                + "回答保持自然、简洁、直接，不要使用Markdown。"
                + "当前场景：" + scene + "。"
                + (userContext == null ? "" : userContext);
    }

    private void saveAssistantReplySafely(Long userId, String reply) {
        if (reply == null || reply.startsWith("AI服务调用出错")) {
            return;
        }
        try {
            chatHistoryService.saveMessage(userId, "assistant", reply);
        } catch (Exception ignored) {
        }
    }

    private void recordAutoLearnIfNeeded(String question, String reply) {
        if (reply == null || reply.startsWith("AI服务调用出错")) {
            return;
        }
        campusKnowledgeService.recordMissAndMaybePromote(question, reply);
    }
}

