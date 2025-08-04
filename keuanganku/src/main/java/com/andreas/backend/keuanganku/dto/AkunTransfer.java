    package com.andreas.backend.keuanganku.dto;

    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.Setter;

    import java.util.UUID;

    @Getter
    @Setter
    @AllArgsConstructor
    public class AkunTransfer {
        private UUID id;
        private String nama;
    }
