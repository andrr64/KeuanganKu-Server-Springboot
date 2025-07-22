package com.andreas.backend.keuanganku.dto.request.goal;

import lombok.Data;

@Data
public class PatchGoalRequest {
    private Double uang;
    private Boolean tercapai;
}