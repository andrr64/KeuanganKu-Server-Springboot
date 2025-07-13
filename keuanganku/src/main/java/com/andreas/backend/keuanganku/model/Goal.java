package com.andreas.backend.keuanganku.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_pengguna")
    private Pengguna pengguna;

    private String nama;
    private BigDecimal target;
    private BigDecimal terkumpul;
    private LocalDate tanggalTarget;
    private Boolean tercapai;
}
