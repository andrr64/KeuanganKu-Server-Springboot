package com.andreas.backend.keuanganku.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;

import com.andreas.backend.keuanganku.dto.request.TransaksiRequest;
import com.andreas.backend.keuanganku.dto.response.DashboardResponse;
import com.andreas.backend.keuanganku.dto.response.KategoriStatistikResponse;
import com.andreas.backend.keuanganku.dto.response.TransaksiResponse;

public interface TransaksiService {

    DashboardResponse getDashboardData(UUID idPengguna);

    public List<KategoriStatistikResponse> getPengeluaranPerKategoriBulanIni(UUID idPengguna);

    public List<Map<String, Object>> getDataGrafikCashflow(UUID idPengguna, int periode);

    void tambahTransaksi(UUID idPengguna, TransaksiRequest request);

    List<TransaksiResponse> getFilteredTransaksi(UUID idPengguna, Integer jenis, UUID idAkun);

    void updateTransaksi(UUID idPengguna, UUID idTransaksi, TransaksiRequest request);

    void hapusTransaksi(UUID idPengguna, UUID idTransaksi);

    List<TransaksiResponse> getRecentTransaksi(UUID idPengguna, int jumlah);

    Map<String, List<Map<String, Object>>> getRingkasanKategori(UUID idPengguna, int periode);

    Page<TransaksiResponse> getFilteredTransaksi(
            UUID idPengguna,
            String keyword,
            LocalDate startDate,
            LocalDate endDate,
            Integer jenis,
            UUID idAkun,
            int page,
            int size
    );
}
