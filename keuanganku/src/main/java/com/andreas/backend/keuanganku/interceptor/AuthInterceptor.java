package com.andreas.backend.keuanganku.interceptor;

import com.andreas.backend.keuanganku.context.CurrentRequestContext;
import com.andreas.backend.keuanganku.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JWTUtil jwtUtil;

    public AuthInterceptor(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        String token = null;
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("token".equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }

        if (token == null) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token tidak ditemukan");
            return false;
        }

        try {
            UUID userId = UUID.fromString(jwtUtil.ambilUserIdDariToken(token));
            CurrentRequestContext.setUserId(userId);
            return true;
        } catch (Exception e) {
            res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token tidak valid");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        CurrentRequestContext.clear(); // bersihkan setelah request selesai
    }
}
