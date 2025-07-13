package com.andreas.backend.keuanganku.model;

import jakarta.persistence.*;

import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

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
    @JoinColumn(name = "id_pengguna")
    private Pengguna pengguna;
}
