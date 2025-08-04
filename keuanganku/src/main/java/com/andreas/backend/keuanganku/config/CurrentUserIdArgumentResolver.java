package com.andreas.backend.keuanganku.config;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.context.CurrentRequestContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.UUID;

/**
 * Resolver ini bertugas untuk menyuntikkan UUID user yang sedang login
 * (diambil dari token JWT) ke parameter method controller yang
 * menggunakan anotasi @CurrentUserId.
 *
 * Misalnya:
 *   public ResponseEntity<?> getData(@CurrentUserId UUID idPengguna) { ... }
 *
 * UUID diambil dari ThreadLocal yang sebelumnya diset di JwtAuthenticationFilter.
 */
@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * Mengecek apakah parameter method memenuhi syarat untuk di-resolve:
     * - Memiliki anotasi @CurrentUserId
     * - Bertipe UUID
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUserId.class)
                && parameter.getParameterType().equals(UUID.class);
    }

    /**
     * Mengambil user ID dari ThreadLocal (CurrentRequestContext)
     * yang sebelumnya diset oleh JwtAuthenticationFilter.
     */
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            org.springframework.web.bind.support.WebDataBinderFactory binderFactory
    ) {
        return CurrentRequestContext.getUserId();
    }
}
