package com.campus.agent.controller;

import com.campus.agent.service.WeatherService;
import com.campus.agent.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 天气控制器
 */
@RestController
@RequestMapping("/weather")
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    /**
     * 获取当前天气
     * @return 天气信息
     */
    @GetMapping("/current")
    public Result<Map<String, Object>> getCurrentWeather() {
        try {
            Map<String, Object> weather = weatherService.getCurrentWeather();
            return Result.success(weather);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取天气失败");
        }
    }
}
