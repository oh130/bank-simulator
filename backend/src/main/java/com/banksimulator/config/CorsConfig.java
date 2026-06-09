package com.banksimulator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * CORS 설정 - React 개발 서버(3000번 포트)에서의 요청을 허용
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 자격증명(쿠키, Authorization 헤더) 포함 요청 허용
        config.setAllowCredentials(true);

        // 허용할 오리진 (React 개발 서버)
        config.setAllowedOrigins(List.of("http://localhost:3000"));

        // 허용할 HTTP 헤더
        config.setAllowedHeaders(List.of("*"));

        // 허용할 HTTP 메서드
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 클라이언트에 노출할 헤더
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
