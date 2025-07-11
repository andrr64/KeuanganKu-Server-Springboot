package com.andreas.backend.keuanganku.config;

import com.andreas.backend.keuanganku.interceptor.AuthInterceptor;
import com.andreas.backend.keuanganku.resolver.CurrentUserIdResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final CurrentUserIdResolver currentUserIdResolver;

    public WebConfig(AuthInterceptor authInterceptor, CurrentUserIdResolver resolver) {
        this.authInterceptor = authInterceptor;
        this.currentUserIdResolver = resolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/secure/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentUserIdResolver);
    }
}
