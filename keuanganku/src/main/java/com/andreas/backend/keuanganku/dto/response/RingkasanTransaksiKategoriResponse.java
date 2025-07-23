package com.andreas.backend.keuanganku.dto.response;

import java.util.ArrayList;
import java.util.List;

import com.andreas.backend.keuanganku.dto.SumTransaksiKategori;

import lombok.Data;


@Data
public class RingkasanTransaksiKategoriResponse {
    private List<SumTransaksiKategori> pengeluaran = new ArrayList<>();
    private List<SumTransaksiKategori> pemasukan = new ArrayList<>();
}
