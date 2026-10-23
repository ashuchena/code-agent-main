package com.campus.agent.service;

import com.campus.agent.entity.SystemConfig;
import com.campus.agent.mapper.SystemConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Service
public class SystemConfigService {

    public static final String SCHOOL_NAME_KEY = "campus.school.name";
    public static final String AUTO_LEARN_THRESHOLD_KEY = "campus.knowledge.auto.learn.threshold";

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    public String getConfigValue(String key, String defaultValue) {
        SystemConfig config = systemConfigMapper.selectByConfigKey(key);
        if (config == null || !StringUtils.hasText(config.getConfigValue())) {
            return defaultValue;
        }
        return config.getConfigValue().trim();
    }

    public Integer getIntConfigValue(String key, Integer defaultValue) {
        String value = getConfigValue(key, defaultValue == null ? null : String.valueOf(defaultValue));
        if (!StringUtils.hasText(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public void saveOrUpdate(String key, String value, String description) {
        SystemConfig existing = systemConfigMapper.selectByConfigKey(key);
        LocalDateTime now = LocalDateTime.now();
        if (existing == null) {
            SystemConfig config = new SystemConfig();
            config.setConfigKey(key);
            config.setConfigValue(value);
            config.setDescription(description);
            config.setCreateTime(now);
            config.setUpdateTime(now);
            systemConfigMapper.insert(config);
            return;
        }
        existing.setConfigValue(value);
        existing.setDescription(description);
        existing.setUpdateTime(now);
        systemConfigMapper.updateById(existing);
    }
}
