package com.example.demo.config;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AdminRoleInterceptor implements HandlerInterceptor{
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String role = (String) request.getAttribute("role");
        if(!"admin".equalsIgnoreCase(role)){
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("Access Denied: Admin Role Required");
            return false;
        }
        return true;
    }
}
