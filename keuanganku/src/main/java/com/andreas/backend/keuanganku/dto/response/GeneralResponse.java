package com.andreas.backend.keuanganku.dto.response;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralResponse<T> {

    private String message;
    private T data;
    private boolean success;

    // Constructor tanpa data
    public GeneralResponse(String message) {
        this.message = message;
    }

    // Static factory method untuk Page<T>
    public static <T> GeneralResponse<Map<String, Object>> fromPage(Page<T> page, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("currentPage", page.getNumber());
        response.put("totalItems", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());

        return new GeneralResponse<>(message, response, true);
    }

    public static <T> GeneralResponse<Map<String, Object>> fromPage(Page<T> page) {
        Map<String, Object> response = new HashMap<>();
        response.put("content", page.getContent());
        response.put("currentPage", page.getNumber());
        response.put("totalItems", page.getTotalElements());
        response.put("totalPages", page.getTotalPages());

        return new GeneralResponse<>("Ok", response, true);
    }
}
