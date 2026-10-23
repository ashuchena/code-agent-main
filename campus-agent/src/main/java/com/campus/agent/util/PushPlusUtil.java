package com.campus.agent.util;

import com.campus.agent.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * PushPlus 微信推送工具
 * 文档：http://www.pushplus.plus/doc/
 */
@Component
public class PushPlusUtil {

    private static final String PUSH_URL = "http://www.pushplus.plus/send";

    @Value("${pushplus.default-token:}")
    private String defaultToken;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 发送推送（使用用户自己的 Token）
     * @param user 用户实体（包含 pushplusToken）
     * @param title 标题
     * @param content 内容（支持 HTML）
     * @return 是否成功
     */
    public boolean send(User user, String title, String content) {
        String token = user.getPushplusToken();
        if (token == null || token.isEmpty()) {
            System.out.println("用户未配置 PushPlus Token，跳过推送");
            return false;
        }
        return sendRaw(token, title, content, "HTML");
    }

    /**
     * 发送推送（使用默认 Token）
     */
    public boolean send(String title, String content) {
        if (defaultToken == null || defaultToken.isEmpty()) {
            System.out.println("未配置默认 PushPlus Token");
            return false;
        }
        return sendRaw(defaultToken, title, content, "HTML");
    }

    /**
     * 底层发送方法
     */
    private boolean sendRaw(String token, String title, String content, String contentType) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("title", title);
            body.put("content", content);
            body.put("contentType", contentType);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(PUSH_URL, entity, Map.class);
            Map<String, Object> respBody = response.getBody();

            if (respBody != null && "200".equals(String.valueOf(respBody.get("code")))) {
                System.out.println("PushPlus 推送成功：" + title);
                return true;
            } else {
                System.out.println("PushPlus 推送失败：" + (respBody != null ? respBody.get("msg") : "未知错误"));
                return false;
            }
        } catch (Exception e) {
            System.out.println("PushPlus 推送异常：" + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
