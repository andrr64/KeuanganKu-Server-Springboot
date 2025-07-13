package com.andreas.backend.keuanganku.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
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
}
