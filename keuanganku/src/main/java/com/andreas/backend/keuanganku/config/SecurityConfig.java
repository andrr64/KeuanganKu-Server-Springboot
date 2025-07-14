package com.andreas.backend.keuanganku.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.andreas.backend.keuanganku.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Mengaktifkan security pada level method (misal @PreAuthorize)
@RequiredArgsConstructor  // Lombok akan generate constructor dengan parameter final fields
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    /**
     * Konfigurasi utama Spring Security filter chain. Di sini mengatur: -
     * Nonaktifkan CSRF karena menggunakan JWT dan stateless - Session tidak
     * dibuat, karena API stateless pakai JWT - Endpoint dengan prefix
     * /api/secure/** harus authenticated - Endpoint lain boleh diakses semua
     * (permitAll) - Memasukkan JwtAuthenticationFilter sebelum filter standar
     * UsernamePasswordAuthenticationFilter
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors
                .configurationSource(request -> {
                    var corsConfig = new org.springframework.web.cors.CorsConfiguration();
                    corsConfig.setAllowedOrigins(List.of("http://localhost:3000")); // asal frontend kamu
                    corsConfig.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    corsConfig.setAllowedHeaders(List.of("*"));
                    corsConfig.setAllowCredentials(true); // penting untuk cookie
                    return corsConfig;
                })
                )
                // Nonaktifkan CSRF karena kita pakai JWT (bukan session form login)
                .csrf(csrf -> csrf.disable())
                // Jangan buat session, karena API kita stateless dan pakai JWT
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Atur rules akses endpoint
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/secure/**").authenticated() // hanya boleh akses jika sudah login (token valid)
                .anyRequest().permitAll() // selain itu boleh akses tanpa token
                )
                // Pasang filter JWT kita sebelum filter default Spring Security yang handle login form
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // Build konfigurasi security-nya
                .build();
    }

    /**
     * Bean PasswordEncoder untuk enkripsi password dengan BCrypt. Digunakan di
     * service ketika menyimpan password user dan memvalidasi saat login.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
