package com.warehouse.config;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Cấu hình Spring MVC và tài nguyên giao diện tĩnh. */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Bean
    Clock warehouseClock(@Value("${warehouse.time-zone:Asia/Ho_Chi_Minh}") String zone) {
        return Clock.system(ZoneId.of(zone));
    }
}
