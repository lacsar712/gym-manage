package com.gymmanage.auth;

import com.gymmanage.common.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;

    public AuthInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String auth = request.getHeader("Authorization");
        if (auth == null || auth.isBlank()) {
            throw BizException.unauthorized("missing Authorization header");
        }
        if (!auth.startsWith("Bearer ")) {
            throw BizException.unauthorized("invalid Authorization header");
        }
        String token = auth.substring("Bearer ".length()).trim();
        JwtUtil.JwtClaims claims = jwtUtil.verify(token);
        request.setAttribute("auth.username", claims.getSubject());
        return true;
    }
}
