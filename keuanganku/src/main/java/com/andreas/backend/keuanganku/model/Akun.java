package com.andreas.backend.keuanganku.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Akun {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private boolean aktif;

    private String nama;
    private BigDecimal saldo;
    private LocalDateTime dibuatPada;

    @ManyToOne
    @JoinColumn(name = "id_pengguna")
    private Pengguna pengguna;

}
