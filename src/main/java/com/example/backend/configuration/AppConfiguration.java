package com.example.backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfiguration  implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS") // 允许跨域的方法，可以单独配置
                .allowCredentials(true)
                .maxAge(3600)
                .allowedHeaders("*");
    }
}
