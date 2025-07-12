package com.andreas.backend.keuanganku.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_pengguna")
    private Pengguna pengguna;

    @ManyToOne
    @JoinColumn(name = "id_dari_akun")
    private Akun dariAkun;

    @ManyToOne
    @JoinColumn(name = "id_ke_akun")
    private Akun keAkun;

    private BigDecimal jumlah;
    private LocalDateTime tanggal;
    private String catatan;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Pengguna getPengguna() {
        return pengguna;
    }

    public void setPengguna(Pengguna pengguna) {
        this.pengguna = pengguna;
    }

    public Akun getDariAkun() {
        return dariAkun;
    }

    public void setDariAkun(Akun dariAkun) {
        this.dariAkun = dariAkun;
    }

    public Akun getKeAkun() {
        return keAkun;
    }

    public void setKeAkun(Akun keAkun) {
        this.keAkun = keAkun;
    }

    public BigDecimal getJumlah() {
        return jumlah;
    }

    public void setJumlah(BigDecimal jumlah) {
        this.jumlah = jumlah;
    }

    public LocalDateTime getTanggal() {
        return tanggal;
    }

    public void setTanggal(LocalDateTime tanggal) {
        this.tanggal = tanggal;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    // Getter dan Setter
}
