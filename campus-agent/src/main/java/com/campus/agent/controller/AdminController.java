package com.campus.agent.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.agent.entity.*;
import com.campus.agent.mapper.*;
import com.campus.agent.service.QwenService;
import com.campus.agent.utils.Result;
import com.campus.agent.util.PushPlusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 管理员后台接口
 * 所有接口均需要管理员角色（前端校验 + 后端校验）
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AiModelConfigMapper aiModelConfigMapper;

    @Autowired
    private ServicePhoneMapper servicePhoneMapper;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Autowired
    private MaintenanceMapper maintenanceMapper;

    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private VolunteerMapper volunteerMapper;

    @Autowired
    private ExpressMapper expressMapper;

    @Autowired
    private PartTimeMapper partTimeMapper;

    @Autowired
    private LostFoundMapper lostFoundMapper;

    @Autowired
    private QwenService qwenService;

    @Autowired
    private PushPlusUtil pushPlusUtil;

    @Autowired
    private HttpServletRequest request;

    // ==================== 权限校验 ====================

    /**
     * 检查当前请求是否为管理员
     */
    private boolean isAdmin(Long userId) {
        if (userId != null) {
            User user = userMapper.selectById(userId);
            if (user != null && "admin".equals(user.getRole())) {
                return true;
            }
        }

        Cookie[] cookies = request.getCookies();
        if (cookies == null) return false;

        String cookieRole = null;
        String cookieUsername = null;
        for (Cookie cookie : cookies) {
            if ("role".equals(cookie.getName())) {
                cookieRole = cookie.getValue();
            }
            if ("username".equals(cookie.getName())) {
                cookieUsername = cookie.getValue();
            }
        }

        return "admin".equals(cookieRole) || "admin".equals(cookieUsername);
    }

    // ==================== AI 模型管理 ====================

    /**
     * 获取所有模型配置
     */
    @GetMapping("/models")
    public Result<List<AiModelConfig>> listModels(@RequestParam Long userId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        List<AiModelConfig> list = aiModelConfigMapper.selectList(
                new QueryWrapper<AiModelConfig>().orderByAsc("id")
        );
        return Result.success(list);
    }

    /**
     * 新增模型配置
     */
    @PostMapping("/models/add")
    public Result<Void> addModel(@RequestParam Long userId,
                                  @RequestParam String name,
                                  @RequestParam String modelKey,
                                  @RequestParam(required = false) String description) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        AiModelConfig config = new AiModelConfig();
        config.setName(name);
        config.setModelKey(modelKey);
        config.setDescription(description);
        config.setIsActive(0);
        config.setEnabled(1);
        aiModelConfigMapper.insert(config);
        return Result.success();
    }

    /**
     * 切换当前激活模型（同时生效到 QwenService）
     */
    @PostMapping("/models/activate")
    public Result<Void> activateModel(@RequestParam Long userId,
                                       @RequestParam Integer modelId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        AiModelConfig config = aiModelConfigMapper.selectById(modelId);
        if (config == null) return Result.error("模型不存在");
        if (config.getEnabled() == 0) return Result.error("该模型已禁用，无法激活");

        // 先全部取消激活
        aiModelConfigMapper.deactivateAll();
        // 激活指定模型
        config.setIsActive(1);
        aiModelConfigMapper.updateById(config);
        // 运行时动态切换 QwenService 使用的模型
        qwenService.setModel(config.getModelKey());

        return Result.success();
    }

    /**
     * 启用/禁用模型
     */
    @PostMapping("/models/toggle")
    public Result<Void> toggleModel(@RequestParam Long userId,
                                     @RequestParam Integer modelId,
                                     @RequestParam Integer enabled) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        AiModelConfig config = aiModelConfigMapper.selectById(modelId);
        if (config == null) return Result.error("模型不存在");
        config.setEnabled(enabled);
        if (enabled == 0) config.setIsActive(0); // 禁用时同步取消激活
        aiModelConfigMapper.updateById(config);
        return Result.success();
    }

    /**
     * 删除模型配置
     */
    @PostMapping("/models/delete")
    public Result<Void> deleteModel(@RequestParam Long userId,
                                     @RequestParam Integer modelId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        aiModelConfigMapper.deleteById(modelId);
        return Result.success();
    }

    // ==================== 服务热线管理 ====================

    /**
     * 获取所有服务热线（管理员视角，包含禁用项）
     */
    @GetMapping("/phones")
    public Result<List<ServicePhone>> listPhones(@RequestParam Long userId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        List<ServicePhone> list = servicePhoneMapper.selectList(
                new QueryWrapper<ServicePhone>().orderByAsc("sort_order")
        );
        return Result.success(list);
    }

    /**
     * 新增/更新服务热线
     */
    @PostMapping("/phones/save")
    public Result<Void> savePhone(@RequestParam Long userId,
                                   @RequestBody ServicePhone phone) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        if (phone.getId() != null) {
            servicePhoneMapper.updateById(phone);
        } else {
            phone.setEnabled(1);
            servicePhoneMapper.insert(phone);
        }
        return Result.success();
    }

    /**
     * 删除服务热线
     */
    @PostMapping("/phones/delete")
    public Result<Void> deletePhone(@RequestParam Long userId,
                                     @RequestParam Integer phoneId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        servicePhoneMapper.deleteById(phoneId);
        return Result.success();
    }

    // ==================== 公告管理 ====================

    /**
     * 获取所有公告（管理员视角）
     */
    @GetMapping("/announcements")
    public Result<List<Announcement>> listAnnouncements(@RequestParam Long userId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        List<Announcement> list = announcementMapper.selectList(
                new QueryWrapper<Announcement>().orderByDesc("is_top").orderByDesc("create_time")
        );
        return Result.success(list);
    }

    /**
     * 新增/更新公告
     */
    @PostMapping("/announcements/save")
    public Result<Void> saveAnnouncement(@RequestParam Long userId,
                                          @RequestBody Announcement announcement) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        if (announcement.getCreateBy() == null) announcement.setCreateBy(userId);
        if (announcement.getEnabled() == null) announcement.setEnabled(1);
        if (announcement.getIsTop() == null) announcement.setIsTop(0);
        if (announcement.getId() != null) {
            announcementMapper.updateById(announcement);
        } else {
            announcementMapper.insert(announcement);
        }
        return Result.success();
    }

    /**
     * 删除公告
     */
    @PostMapping("/announcements/delete")
    public Result<Void> deleteAnnouncement(@RequestParam Long userId,
                                            @RequestParam Integer announcementId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        announcementMapper.deleteById(announcementId);
        return Result.success();
    }

    // ==================== 报修工单管理 ====================

    /**
     * 获取所有用户的报修工单
     */
    @GetMapping("/maintenance/list")
    public Result<List<Maintenance>> listAllMaintenance(@RequestParam Long userId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        List<Maintenance> list = maintenanceMapper.selectList(
                new QueryWrapper<Maintenance>().orderByDesc("create_time")
        );
        return Result.success(list);
    }

    /**
     * 更新工单状态（0=待处理，1=处理中，2=已完成）
     */
    @PostMapping("/maintenance/status")
    public Result<Void> updateMaintenanceStatus(@RequestParam Long userId,
                                                 @RequestParam Long maintenanceId,
                                                 @RequestParam Integer status) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        Maintenance m = maintenanceMapper.selectById(maintenanceId);
        if (m == null) return Result.error("工单不存在");
        m.setStatus(status);
        maintenanceMapper.updateById(m);
        pushToUser(
                m.getUserId(),
                "统一报修状态通知",
                "<h3>你的报修工单状态已更新</h3>"
                        + "<p><b>工单ID：</b>#" + m.getId() + "</p>"
                        + "<p><b>报修位置：</b>" + escapeHtml(m.getLocation()) + "</p>"
                        + "<p><b>问题描述：</b>" + escapeHtml(m.getDescription()) + "</p>"
                        + "<p><b>当前状态：</b>" + maintenanceStatusText(status) + "</p>"
        );
        return Result.success();
    }

    // ==================== 问题反馈管理 ====================

    /**
     * 获取所有反馈（管理员视角）
     */
    @GetMapping("/feedback/list")
    public Result<List<Feedback>> listAllFeedback(@RequestParam Long userId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        List<Feedback> list = feedbackMapper.selectList(
                new QueryWrapper<Feedback>().orderByDesc("create_time")
        );
        return Result.success(list);
    }

    /**
     * 回复反馈，并推送通知给提交反馈的用户
     */
    @PostMapping("/feedback/reply")
    public Result<Void> replyFeedback(@RequestParam Long userId,
                                        @RequestParam Long feedbackId,
                                        @RequestParam String reply,
                                        @RequestParam String replierName) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        Feedback feedback = feedbackMapper.selectById(feedbackId);
        if (feedback == null) return Result.error("反馈不存在");
        feedback.setReply(reply);
        feedback.setReplierName(replierName);
        feedback.setReplyTime(java.time.LocalDateTime.now());
        feedback.setStatus(2); // 已回复
        feedbackMapper.updateById(feedback);

        // 推送通知给提交反馈的用户
        User feedbackUser = userMapper.selectById(feedback.getUserId());
        if (feedbackUser != null) {
            String title = "📬 问题反馈回复通知";
            String content = "<h3>✅ 你的问题反馈已收到回复</h3>"
                    + "<p><b>原反馈：</b>" + escapeHtml(feedback.getTitle()) + "</p>"
                    + "<p><b>管理员回复：</b>" + escapeHtml(reply) + "</p>"
                    + "<p style='color:#666;font-size:12px;'>回复人：" + escapeHtml(replierName) + "</p>";
            boolean pushed = pushPlusUtil.send(feedbackUser, title, content);
            System.out.println("回复推送结果：" + (pushed ? "成功" : "失败（用户未配置Token）"));
        }

        return Result.success();
    }

    /**
     * 简单HTML转义，防止XSS
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private void pushToUser(Long targetUserId, String title, String content) {
        if (targetUserId == null) return;
        User user = userMapper.selectById(targetUserId);
        if (user == null) return;
        boolean pushed = pushPlusUtil.send(user, title, content);
        System.out.println("PushPlus通知结果：" + (pushed ? "成功" : "失败或未配置Token") + "，用户ID=" + targetUserId + "，标题=" + title);
    }

    private String maintenanceStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0:
                return "待处理";
            case 1:
                return "处理中";
            case 2:
                return "已完成";
            default:
                return "未知";
        }
    }

    private String approvalStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0:
                return "待审核";
            case 1:
                return "已通过";
            case 2:
                return "已拒绝";
            default:
                return "未知";
        }
    }

    // ==================== 志愿活动审批管理 ====================

    /**
     * 获取所有志愿活动（管理员视角，包含待审批的）
     */
    @GetMapping("/volunteer/list")
    public Result<List<Volunteer>> listAllVolunteer(@RequestParam Long userId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        List<Volunteer> list = volunteerMapper.selectList(
                new QueryWrapper<Volunteer>().orderByDesc("create_time")
        );
        return Result.success(list);
    }

    /**
     * 审批志愿活动
     */
    @PostMapping("/volunteer/approve")
    public Result<Void> approveVolunteer(@RequestParam Long userId,
                                           @RequestParam Long activityId,
                                           @RequestParam Integer approvalStatus,
                                           @RequestParam(required = false) String approvalRemark) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        Volunteer activity = volunteerMapper.selectById(activityId);
        if (activity == null) return Result.error("活动不存在");
        activity.setApprovalStatus(approvalStatus);
        activity.setApprovalRemark(approvalRemark);
        activity.setApprovalTime(java.time.LocalDateTime.now());
        activity.setApprovalUserId(userId);
        volunteerMapper.updateById(activity);
        if (activity.getPublisherId() == null) {
            System.out.println("[VolunteerApproval] publisherId is null, activityId=" + activityId + ", approvalStatus=" + approvalStatus);
        } else {
            User publishUser = userMapper.selectById(activity.getPublisherId());
            if (publishUser == null) {
                System.out.println("[VolunteerApproval] publish user not found, publisherId=" + activity.getPublisherId());
            } else if (publishUser.getPushplusToken() == null || publishUser.getPushplusToken().trim().isEmpty()) {
                System.out.println("[VolunteerApproval] publish user has no pushplusToken, publisherId=" + activity.getPublisherId());
            }
        }
        pushToUser(
                activity.getPublisherId(),
                "志愿活动审核通知",
                "<h3>你的志愿活动审核结果已更新</h3>"
                        + "<p><b>活动标题：</b>" + escapeHtml(activity.getTitle()) + "</p>"
                        + "<p><b>活动地点：</b>" + escapeHtml(activity.getLocation()) + "</p>"
                        + "<p><b>审核结果：</b>" + approvalStatusText(approvalStatus) + "</p>"
                        + "<p><b>审核备注：</b>" + escapeHtml(approvalRemark == null ? "" : approvalRemark) + "</p>"
        );
        return Result.success();
    }

    // ==================== 学生服务管理（快递代取/兼职/失物招领） ====================

    /**
     * 获取所有快递代取记录（管理员视角）
     */
    @GetMapping("/express/list")
    public Result<List<Express>> listAllExpress(@RequestParam Long userId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        List<Express> list = expressMapper.selectList(
                new QueryWrapper<Express>().orderByAsc("approval_status").orderByDesc("create_time")
        );
        return Result.success(list);
    }

    /**
     * 删除快递代取记录（管理员）
     */
    @PostMapping("/express/delete")
    public Result<Void> deleteExpress(@RequestParam Long userId, @RequestParam Long id) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        expressMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 审核快递代取记录
     */
    @PostMapping("/express/approve")
    public Result<Void> approveExpress(@RequestParam Long userId,
                                        @RequestParam Long id,
                                        @RequestParam Integer approvalStatus,
                                        @RequestParam(required = false) String approvalRemark) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        Express express = expressMapper.selectById(id);
        if (express == null) return Result.error("记录不存在");
        express.setApprovalStatus(approvalStatus);
        express.setApprovalRemark(approvalRemark);
        express.setApprovalTime(java.time.LocalDateTime.now());
        express.setApprovalUserId(userId);
        expressMapper.updateById(express);
        pushToUser(
                express.getPublisherId(),
                "快递代取审核通知",
                "<h3>你的快递代取发布审核结果已更新</h3>"
                        + "<p><b>快递公司：</b>" + escapeHtml(express.getExpressCompany()) + "</p>"
                        + "<p><b>取件地点：</b>" + escapeHtml(express.getLocation()) + "</p>"
                        + "<p><b>审核结果：</b>" + approvalStatusText(approvalStatus) + "</p>"
                        + "<p><b>审核备注：</b>" + escapeHtml(approvalRemark == null ? "" : approvalRemark) + "</p>"
        );
        return Result.success();
    }

    /**
     * 获取所有兼职信息（管理员视角）
     */
    @GetMapping("/parttime/list")
    public Result<List<PartTime>> listAllPartTime(@RequestParam Long userId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        List<PartTime> list = partTimeMapper.selectList(
                new QueryWrapper<PartTime>().orderByAsc("approval_status").orderByDesc("create_time")
        );
        return Result.success(list);
    }

    /**
     * 删除兼职信息（管理员）
     */
    @PostMapping("/parttime/delete")
    public Result<Void> deletePartTime(@RequestParam Long userId, @RequestParam Long id) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        partTimeMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 审核兼职信息
     */
    @PostMapping("/parttime/approve")
    public Result<Void> approvePartTime(@RequestParam Long userId,
                                         @RequestParam Long id,
                                         @RequestParam Integer approvalStatus,
                                         @RequestParam(required = false) String approvalRemark) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        PartTime partTime = partTimeMapper.selectById(id);
        if (partTime == null) return Result.error("记录不存在");
        partTime.setApprovalStatus(approvalStatus);
        partTime.setApprovalRemark(approvalRemark);
        partTime.setApprovalTime(java.time.LocalDateTime.now());
        partTime.setApprovalUserId(userId);
        partTimeMapper.updateById(partTime);
        pushToUser(
                partTime.getPublisherId(),
                "兼职信息审核通知",
                "<h3>你的兼职信息审核结果已更新</h3>"
                        + "<p><b>兼职标题：</b>" + escapeHtml(partTime.getTitle()) + "</p>"
                        + "<p><b>工作地点：</b>" + escapeHtml(partTime.getLocation()) + "</p>"
                        + "<p><b>审核结果：</b>" + approvalStatusText(approvalStatus) + "</p>"
                        + "<p><b>审核备注：</b>" + escapeHtml(approvalRemark == null ? "" : approvalRemark) + "</p>"
        );
        return Result.success();
    }

    /**
     * 获取所有失物招领记录（管理员视角）
     */
    @GetMapping("/lostfound/list")
    public Result<List<LostFound>> listAllLostFound(@RequestParam Long userId) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        List<LostFound> list = lostFoundMapper.selectList(
                new QueryWrapper<LostFound>().orderByAsc("approval_status").orderByDesc("create_time")
        );
        return Result.success(list);
    }

    /**
     * 删除失物招领记录（管理员）
     */
    @PostMapping("/lostfound/delete")
    public Result<Void> deleteLostFound(@RequestParam Long userId, @RequestParam Long id) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        lostFoundMapper.deleteById(id);
        return Result.success();
    }

    /**
     * 审核失物招领记录
     */
    @PostMapping("/lostfound/approve")
    public Result<Void> approveLostFound(@RequestParam Long userId,
                                          @RequestParam Long id,
                                          @RequestParam Integer approvalStatus,
                                          @RequestParam(required = false) String approvalRemark) {
        if (!isAdmin(userId)) return Result.error("无权限，需要管理员账号");
        LostFound lostFound = lostFoundMapper.selectById(id);
        if (lostFound == null) return Result.error("记录不存在");
        lostFound.setApprovalStatus(approvalStatus);
        lostFound.setApprovalRemark(approvalRemark);
        lostFound.setApprovalTime(java.time.LocalDateTime.now());
        lostFound.setApprovalUserId(userId);
        lostFoundMapper.updateById(lostFound);
        pushToUser(
                lostFound.getUserId(),
                "失物招领审核通知",
                "<h3>你的失物招领信息审核结果已更新</h3>"
                        + "<p><b>标题：</b>" + escapeHtml(lostFound.getTitle()) + "</p>"
                        + "<p><b>地点：</b>" + escapeHtml(lostFound.getLocation()) + "</p>"
                        + "<p><b>审核结果：</b>" + approvalStatusText(approvalStatus) + "</p>"
                        + "<p><b>审核备注：</b>" + escapeHtml(approvalRemark == null ? "" : approvalRemark) + "</p>"
        );
        return Result.success();
    }

    // ==================== 公开接口（无需管理员） ====================

    /**
     * 获取启用的服务热线（普通用户访问，用于 RepairPhone.vue）
     */
    @GetMapping("/phones/public")
    public Result<List<ServicePhone>> publicPhones() {
        List<ServicePhone> list = servicePhoneMapper.selectList(
                new QueryWrapper<ServicePhone>()
                        .eq("enabled", 1)
                        .orderByAsc("sort_order")
        );
        return Result.success(list);
    }

    /**
     * 获取启用的公告（普通用户访问，用于首页展示）
     */
    @GetMapping("/announcements/public")
    public Result<List<Announcement>> publicAnnouncements() {
        List<Announcement> list = announcementMapper.selectList(
                new QueryWrapper<Announcement>()
                        .eq("enabled", 1)
                        .orderByDesc("is_top")
                        .orderByDesc("create_time")
                        .last("LIMIT 10")
        );
        return Result.success(list);
    }
}
