package com.campus.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.agent.entity.AiModelConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AiModelConfigMapper extends BaseMapper<AiModelConfig> {
    @Update("UPDATE ai_model_config SET is_active = 0")
    void deactivateAll();
}
