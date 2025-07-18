package com.andreas.backend.keuanganku.middleware;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    public static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        // Bungkus request biar bisa baca body berkali-kali
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        // Lanjut ke filter berikutnya dulu
        filterChain.doFilter(wrappedRequest, response);

        // Logging setelah request selesai diproses
        String requestBody = new String(wrappedRequest.getContentAsByteArray(), StandardCharsets.UTF_8);
        String method = request.getMethod();
        String path = request.getRequestURI();
        String clientIp = getClientIpAddress(request);

        log.info("📥 Incoming Request:");
        log.info("📌 IP       : {}", clientIp);
        log.info("🔗 Path     : {} {}", method, path);
        log.info("📦 Body     : {}", requestBody);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isEmpty()) {
            return forwarded.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}
