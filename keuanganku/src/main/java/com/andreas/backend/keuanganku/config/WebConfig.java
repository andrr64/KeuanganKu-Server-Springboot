package com.andreas.backend.keuanganku.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Konfigurasi Web MVC Spring.
 * 
 * Class ini digunakan untuk mendaftarkan custom argument resolver ke Spring MVC.
 * Dalam hal ini, kita menambahkan resolver untuk anotasi @CurrentUserId,
 * agar bisa langsung menyuntikkan UUID dari pengguna yang sedang login
 * ke parameter controller.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CurrentUserIdArgumentResolver currentUserIdResolver;

    /**
     * Constructor injection dari CurrentUserIdArgumentResolver
     */
    @Autowired
    public WebConfig(CurrentUserIdArgumentResolver currentUserIdResolver) {
        this.currentUserIdResolver = currentUserIdResolver;
    }

    /**
     * Menambahkan custom argument resolver ke daftar resolver yang dikenali Spring.
     * Dengan ini, Spring tahu bagaimana menangani parameter dengan anotasi @CurrentUserId.
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserIdResolver);
    }
}
