package com.andreas.backend.keuanganku.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
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

    public Pengguna getPengguna() {
        return pengguna;
    }

    public void setPengguna(Pengguna pengguna) {
        this.pengguna = pengguna;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public BigDecimal getTarget() {
        return target;
    }

    public void setTarget(BigDecimal target) {
        this.target = target;
    }

    public BigDecimal getTerkumpul() {
        return terkumpul;
    }

    public void setTerkumpul(BigDecimal terkumpul) {
        this.terkumpul = terkumpul;
    }

    public LocalDate getTanggalTarget() {
        return tanggalTarget;
    }

    public void setTanggalTarget(LocalDate tanggalTarget) {
        this.tanggalTarget = tanggalTarget;
    }

    public Boolean getTercapai() {
        return tercapai;
    }

    public void setTercapai(Boolean tercapai) {
        this.tercapai = tercapai;
    }

    // Getter dan Setter
}
