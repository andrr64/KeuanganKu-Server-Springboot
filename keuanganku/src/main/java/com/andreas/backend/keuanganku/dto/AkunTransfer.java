    package com.andreas.backend.keuanganku.dto;

    import java.util.UUID;

    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.Setter;

    @Getter
    @Setter
    @AllArgsConstructor
    public class AkunTransfer {
        private UUID id;
        private String nama;
    }
