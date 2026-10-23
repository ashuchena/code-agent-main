package com.campus.agent.controller;

import com.campus.agent.entity.User;
import com.campus.agent.mapper.UserMapper;
import com.campus.agent.utils.Result;
import com.campus.agent.util.PushPlusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用户控制器：登录、Token管理
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PushPlusUtil pushPlusUtil;

    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestParam String username,
                                                @RequestParam String password) {
        try {
            String trimmedUsername = username == null ? "" : username.trim();
            if (trimmedUsername.isEmpty()) {
                return Result.error("请输入学号");
            }
            if (password == null || password.length() < 6) {
                return Result.error("密码至少需要 6 位");
            }

            User existedUser = userMapper.selectByUsername(trimmedUsername);
            if (existedUser != null) {
                return Result.error("该学号已注册");
            }

            User user = new User();
            user.setUsername(trimmedUsername);
            user.setPassword(password);
            user.setRole("user");
            userMapper.insert(user);

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("nickname", user.getUsername());
            data.put("role", user.getRole());
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("注册失败：" + e.getMessage());
        }
    }

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果（包含userId）
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestParam String username,
                                              @RequestParam String password) {
        try {
            // 查询用户
            User user = userMapper.selectByUsername(username);
            if (user == null) {
                return Result.error("用户不存在");
            }
            // 验证密码
            if (!user.getPassword().equals(password)) {
                return Result.error("密码错误");
            }
            // 返回用户ID、用户名和角色
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("nickname", user.getUsername());
            data.put("role", user.getRole());
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("登录失败：" + e.getMessage());
        }
    }

    /**
     * 保存用户 PushPlus Token
     */
    @PostMapping("/save-token")
    public Result<Void> saveToken(@RequestParam Long userId,
                                   @RequestParam String token) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            user.setPushplusToken(token);
            userMapper.updateById(user);
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("保存失败：" + e.getMessage());
        }
    }

    /**
     * 测试推送
     */
    @GetMapping("/test-push")
    public Result<Void> testPush(@RequestParam Long userId) {
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            if (user.getPushplusToken() == null || user.getPushplusToken().isEmpty()) {
                return Result.error("请先配置 PushPlus Token");
            }
            String title = "⏰ 校园小管家 · 推送测试";
            String content = "<h3>✅ 推送配置成功！</h3>"
                    + "<p>你的校园小管家推送通知已成功接入。</p>"
                    + "<p>今后提醒将自动推送到你的微信 ❤️</p>";
            boolean ok = pushPlusUtil.send(user, title, content);
            if (ok) {
                return Result.success();
            } else {
                return Result.error("推送失败，请检查 Token 是否正确");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("推送异常：" + e.getMessage());
        }
    }
}
