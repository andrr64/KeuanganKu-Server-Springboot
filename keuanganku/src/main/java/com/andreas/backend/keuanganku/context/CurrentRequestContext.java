package com.andreas.backend.keuanganku.context;

import java.util.UUID;

public class CurrentRequestContext {

    private static final ThreadLocal<UUID> currentUserId = new ThreadLocal<>();

    public static void setUserId(UUID userId) {
        currentUserId.set(userId);
    }

    public static UUID getUserId() {
        return currentUserId.get();
    }

    public static void clear() {
        currentUserId.remove();
    }
}
