package com.campus.agent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        String[] spaRoutes = {
            "/",
            "/login",
            "/register",
            "/chat",
            "/home",
            "/student-service",
            "/schedule",
            "/schedule-list",
            "/remind",
            "/dining",
            "/repair",
            "/repair-phone",
            "/psych-consult",
            "/psych",
            "/leave",
            "/lostfound",
            "/express",
            "/volunteer",
            "/parttime",
            "/maintenance",
            "/feedback",
            "/announcement",
            "/admin"
        };

        for (String route : spaRoutes) {
            registry.addViewController(route).setViewName("forward:/index.html");
        }

        registry.addViewController("/order/{id}").setViewName("forward:/index.html");
        registry.addViewController("/feedback/{type}").setViewName("forward:/index.html");
    }
}
