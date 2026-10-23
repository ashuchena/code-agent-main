package com.campus.agent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class WebConfig {

    @Bean
    public CharacterEncodingFilter characterEncodingFilter() {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();
        filter.setEncoding("UTF-8");
        filter.setForceEncoding(true);
        return filter;
    }

    @Bean
    public OncePerRequestFilter spaIndexNoCacheFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
                filterChain.doFilter(request, response);

                if (!"GET".equalsIgnoreCase(request.getMethod())) {
                    return;
                }

                String path = request.getRequestURI();
                String contentType = response.getContentType();
                boolean isHtmlRequest = "/".equals(path)
                    || "/index.html".equals(path)
                    || (contentType != null && contentType.contains("text/html"));

                if (isHtmlRequest) {
                    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
                    response.setHeader("Pragma", "no-cache");
                    response.setDateHeader("Expires", 0);
                }
            }
        };
    }
}
