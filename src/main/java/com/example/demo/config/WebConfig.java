package com.example.demo.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenValidationInterceptor)
                .addPathPatterns("/api/rooms/**") // 보호할 엔드포인트
                .addPathPatterns("/api/board/**") // 보호할 엔드포인트
                .addPathPatterns("/api/coding/**") // 보호할 엔드포인트
                .addPathPatterns("/api/competition/**") // 보호할 엔드포인트
                .excludePathPatterns("/api/auth/**","/api/notice/**"); // AUTH 엔드포인트 제외


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