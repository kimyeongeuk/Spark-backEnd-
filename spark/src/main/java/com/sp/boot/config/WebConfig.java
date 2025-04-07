package com.sp.boot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class WebConfig implements WebMvcConfigurer{
	
	@Override
    public void addCorsMappings(CorsRegistry registry) {
        // 모든 엔드포인트에 대해 CORS 적용
        registry.addMapping("/**") // 모든 경로로 설정
            .allowedOrigins("http://localhost:3000")  // React 앱의 주소
            .allowedMethods("GET", "POST", "PUT", "DELETE")  // 허용할 HTTP 메서드
            .allowedHeaders("*")  // 모든 헤더 허용
            .allowCredentials(true);  // 자격 증명 (예: 쿠키) 허용
    }
	
	
	

}
