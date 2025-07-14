package com.andreas.backend.keuanganku.security;

import java.io.IOException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.andreas.backend.keuanganku.context.CurrentRequestContext;
import com.andreas.backend.keuanganku.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * Tentukan endpoint yang TIDAK perlu dilewati filter ini. Biasanya endpoint
     * login, register, dan public lainnya. Contoh di sini: semua path yang
     * diawali "/api/auth/" tidak difilter.
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/auth/");
    }

    /**
     * Proses utama filter yang berjalan setiap request kecuali yang
     * dikecualikan di shouldNotFilter.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String token = null;

        // 1. Coba ambil token JWT dari header Authorization dengan format "Bearer <token>"
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // ambil substring tokennya saja
        }

        // 2. Jika header Authorization tidak ada atau tidak lengkap,
        //    coba ambil token dari cookie bernama "accessToken"
        if (token == null && request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 3. Jika token tetap tidak ditemukan (null atau kosong),
        //    langsung kembalikan response HTTP 401 Unauthorized dengan pesan JSON
        if (token == null || token.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Unauthorized\"}");
            return; // hentikan proses filter
        }

        try {
            // 4. Validasi token dan ekstrak userId dari token JWT
            UUID userId = jwtService.extractUserId(token);

            // Simpan userId di context thread local agar bisa diakses di bagian lain aplikasi
            CurrentRequestContext.setUserId(userId);

            // Buat dummy user Spring Security dari userId yang sudah valid
            var dummyUser = new org.springframework.security.core.userdetails.User(
                    userId.toString(),
                    "", // password tidak diperlukan di sini
                    Collections.emptyList() // tanpa role/authority
            );

            // Buat Authentication token berdasarkan dummyUser
            var authToken = new UsernamePasswordAuthenticationToken(
                    dummyUser, null, dummyUser.getAuthorities());

            // Tambahkan detail request ke Authentication token
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Simpan Authentication token ke context security agar bisa diakses oleh controller dan service
            SecurityContextHolder.getContext().setAuthentication(authToken);

        } catch (Exception e) {
            // Jika terjadi error saat validasi token (misal token expired, rusak, atau tidak valid)
            logger.warn("JWT error: {}" + e.getMessage());

            // Hapus konteks authentication yang mungkin sudah ada
            SecurityContextHolder.clearContext();

            // Kirim response 401 Unauthorized dengan pesan JSON
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\": \"Token tidak valid atau expired\"}");
            return; // hentikan proses filter
        }

        try {
            // Jika token valid, teruskan request ke filter berikutnya / controller
            filterChain.doFilter(request, response);
        } finally {
            // Bersihkan context thread local userId agar tidak bocor ke request lain
            CurrentRequestContext.clear();
        }
    }
}
