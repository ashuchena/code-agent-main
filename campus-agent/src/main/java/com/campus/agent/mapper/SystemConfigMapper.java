package com.campus.agent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.agent.entity.SystemConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

    @Select("select * from system_config where config_key = #{configKey} limit 1")
    SystemConfig selectByConfigKey(@Param("configKey") String configKey);
}
