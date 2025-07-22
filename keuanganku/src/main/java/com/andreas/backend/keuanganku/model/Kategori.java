package com.andreas.backend.keuanganku.model;

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
