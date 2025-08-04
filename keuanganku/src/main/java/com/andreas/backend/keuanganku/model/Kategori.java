package com.andreas.backend.keuanganku.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
public class Kategori {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String nama;
    private Integer jenis; // 1 = pemasukan, 2 = pengeluaran

    @ManyToOne
    @JoinColumn(name = "id_pengguna", nullable=true)
    private Pengguna pengguna;
}
