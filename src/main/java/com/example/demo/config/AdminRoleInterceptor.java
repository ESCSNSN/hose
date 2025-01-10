package com.example.demo.config;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AdminRoleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println(">>> [AdminInterceptor] OPTIONS 요청, 통과");
            return true;
        }

        System.out.println("AdminRoleInterceptor: Request received - URI: " + request.getRequestURI());

        // 역할(Role) 검증
        String role = (String) request.getAttribute("role");
        String username = (String) request.getAttribute("username");
        System.out.println("Username from request: " + username);
        System.out.println("Role from request: " + role);

        if (role == null || username == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Missing role or username");
            return false;
        }

        if (!"admin".equalsIgnoreCase(role)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Access Denied: Admin Role Required");
            return false;
        }

        return true;
    }
}
