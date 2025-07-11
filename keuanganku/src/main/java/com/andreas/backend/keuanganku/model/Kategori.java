package com.andreas.backend.keuanganku.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class Kategori {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    private String nama;
    private Integer jenis; // "pemasukan" atau "pengeluaran"

    @ManyToOne
    @JoinColumn(name = "id_pengguna")
    private Pengguna pengguna;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public Integer getJenis() {
        return jenis;
    }

    public void setJenis(Integer jenis) {
        this.jenis = jenis;
    }

    public Pengguna getPengguna() {
        return pengguna;
    }

    public void setPengguna(Pengguna pengguna) {
        this.pengguna = pengguna;
    }
}