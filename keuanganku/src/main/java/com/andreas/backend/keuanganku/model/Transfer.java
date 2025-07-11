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
    private BigDecimal jumlah;
    private LocalDateTime tanggal;
    private String catatan;

    @ManyToOne
    @JoinColumn(name = "id_pengguna")
    private Pengguna pengguna;

    @ManyToOne
    @JoinColumn(name = "dari_akun_id")
    private Akun dariAkun;

    @ManyToOne
    @JoinColumn(name = "ke_akun_id")
    private Akun keAkun;
}

