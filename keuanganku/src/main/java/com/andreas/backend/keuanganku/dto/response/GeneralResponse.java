package com.andreas.backend.keuanganku.dto.response;

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
}
