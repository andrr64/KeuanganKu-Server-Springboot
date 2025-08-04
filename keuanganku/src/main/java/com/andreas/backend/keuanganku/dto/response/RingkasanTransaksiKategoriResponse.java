package com.andreas.backend.keuanganku.dto.response;

import com.andreas.backend.keuanganku.dto.SumTransaksiKategori;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class RingkasanTransaksiKategoriResponse {
    private List<SumTransaksiKategori> pengeluaran = new ArrayList<>();
    private List<SumTransaksiKategori> pemasukan = new ArrayList<>();
}
