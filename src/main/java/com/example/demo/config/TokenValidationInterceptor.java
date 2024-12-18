package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class TokenValidationInterceptor implements HandlerInterceptor {

    @Value("${auth.service.url}") // AUTH 서비스 URL
    private String authServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing or invalid Authorization header");
            return false;
        }

        String token = authHeader.substring(7);

        // AUTH 서비스에 토큰 검증 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


        // token 파라미터를 폼 형식으로 전달
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("token", token);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(formData, headers);

        ResponseEntity<Map> authResponse;
        try {
            authResponse = restTemplate.postForEntity(
                    authServiceUrl + "/api/auth/validate",
                    requestEntity,
                    Map.class
            );
        } catch (HttpClientErrorException e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid or expired token");
            return false;
        }

        if (authResponse.getStatusCode() == HttpStatus.OK && Boolean.TRUE.equals(authResponse.getBody().get("isValid"))) {
            // 검증된 사용자 정보 저장
            request.setAttribute("username", authResponse.getBody().get("username"));
            request.setAttribute("role", authResponse.getBody().get("role"));
            return true;
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Invalid or expired token");
            return false;
        }
    }
}
