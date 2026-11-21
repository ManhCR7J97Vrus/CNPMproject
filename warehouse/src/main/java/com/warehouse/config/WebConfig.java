package com.warehouse.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Cấu hình Spring MVC và tài nguyên giao diện tĩnh. */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    // Spring Boot phục vụ HTML, CSS và JS từ classpath:/static/.
}
