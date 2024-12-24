package com.example.demo.config;


import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private String resourcePath = "/upload/**"; // view 에서 접근할 경로
    private String savePath = "file:///C:/springboot_img/"; // 실제 파일 저장 경로(win)
//    private String savePath = "file:///Users/사용자이름/springboot_img/"; // 맥 용

    private final TokenValidationInterceptor tokenValidationInterceptor;
    private final AdminRoleInterceptor adminRoleInterceptor;

    public WebConfig(TokenValidationInterceptor tokenValidationInterceptor, AdminRoleInterceptor adminRoleInterceptor) {
        this.tokenValidationInterceptor = tokenValidationInterceptor;
        this.adminRoleInterceptor = adminRoleInterceptor;
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://463665571ca4.ngrok.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Refreshtoken"));
        configuration.setAllowCredentials(true); // 인증 정보를 포함한 요청 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterRegistration() {
        CorsConfigurationSource source = corsConfigurationSource(); // 명시적으로 호출
        FilterRegistrationBean<CorsFilter> registration = new FilterRegistrationBean<>(new CorsFilter(source));
        registration.setOrder(0); // 필터 우선순위를 최상단으로 설정
        return registration;
    }


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidationInterceptor)
                .addPathPatterns("/api/rooms/**") // 보호할 엔드포인트
                .addPathPatterns("/api/coding/**") // 보호할 엔드포인트
                .addPathPatterns("/api/competition/**") // 보호할 엔드포인트
                .addPathPatterns("/api/lecture/**") // 보호할 엔드포인트
                .excludePathPatterns("/api/auth/**","/api/notice/**", "api/board/**", "api/board/main/**"); // AUTH 엔드포인트 제외


        // 관리자 역할 검증 인터셉터 등록
        registry.addInterceptor(adminRoleInterceptor)
                .addPathPatterns("/api/admin/**"); // 관리자 엔드포인트에만 적용
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(resourcePath)
                .addResourceLocations(savePath);
    }

}