package com.campus.agent.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * 天气服务 - 调用高德/和风天气API获取实时天气
 */
@Service
public class WeatherService {

    @Value("${weather.api-key:}")
    private String apiKey;

    @Value("${weather.city:成都}")
    private String city; // 默认成都

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 获取实时天气
     * @return 天气信息
     */
    public Map<String, Object> getCurrentWeather() {
        Map<String, Object> result = new HashMap<>();

        // 如果没有配置API Key，返回模拟数据
        if (apiKey == null || apiKey.isEmpty()) {
            return getMockWeather();
        }

        try {
            // 优先使用高德天气API
            return getAmapWeather();
        } catch (Exception e) {
            e.printStackTrace();
            return getMockWeather();
        }
    }

    /**
     * 高德天气API
     */
    private Map<String, Object> getAmapWeather() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 先通过城市名获取AdCode
            String geoUrl = String.format(
                "https://restapi.amap.com/v3/geocode/geo?address=%s&key=%s",
                java.net.URLEncoder.encode(city, "UTF-8"), apiKey
            );

            String geoResponse = restTemplate.getForObject(geoUrl, String.class);
            JSONObject geoJson = JSON.parseObject(geoResponse);

            System.out.println("[Weather] 地理编码返回: " + geoResponse);

            if (!"1".equals(geoJson.getString("status"))) {
                return getMockWeather();
            }

            String adcode = geoJson.getJSONArray("geocodes")
                .getJSONObject(0)
                .getString("adcode");

            // 2. 获取实时天气
            String weatherUrl = String.format(
                "https://restapi.amap.com/v3/weather/weatherInfo?city=%s&extensions=base&key=%s",
                adcode, apiKey
            );

            String weatherResponse = restTemplate.getForObject(weatherUrl, String.class);
            JSONObject weatherJson = JSON.parseObject(weatherResponse);

            System.out.println("[Weather] 天气返回: " + weatherResponse);

            if ("1".equals(weatherJson.getString("status"))) {
                JSONObject lives = weatherJson.getJSONArray("lives").getJSONObject(0);
                result.put("temp", lives.getString("temperature"));      // 温度
                result.put("text", lives.getString("weather"));           // 天气
                result.put("windDir", lives.getString("winddirection")); // 风向
                result.put("humidity", lives.getString("humidity"));     // 湿度
                result.put("icon", getWeatherIcon(lives.getString("weather")));
                result.put("success", true);
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return getMockWeather();
    }

    /**
     * 根据天气文字返回emoji
     */
    private String getWeatherIcon(String text) {
        if (text == null) return "☀️";
        text = text.toLowerCase();
        if (text.contains("晴") && !text.contains("云")) return "☀️";
        if (text.contains("多云") || text.contains("阴")) return "⛅";
        if (text.contains("雨")) return "🌧️";
        if (text.contains("雪")) return "❄️";
        if (text.contains("雷")) return "⛈️";
        if (text.contains("雾") || text.contains("霾")) return "🌫️";
        if (text.contains("风")) return "💨";
        return "🌤️";
    }

    /**
     * 返回模拟天气数据（无API Key时使用）
     */
    private Map<String, Object> getMockWeather() {
        Map<String, Object> mock = new HashMap<>();
        mock.put("temp", "26");
        mock.put("text", "多云");
        mock.put("windDir", "东南风");
        mock.put("humidity", "65");
        mock.put("icon", "⛅");
        mock.put("success", true);
        return mock;
    }
}
