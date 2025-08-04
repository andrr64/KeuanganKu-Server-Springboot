package com.andreas.backend.keuanganku.service;

import com.andreas.backend.keuanganku.dto.CashflowItem;
import com.andreas.backend.keuanganku.dto.request.TransaksiRequest;
import com.andreas.backend.keuanganku.dto.response.KategoriStatistikResponse;
import com.andreas.backend.keuanganku.dto.response.RingkasanBulanIni;
import com.andreas.backend.keuanganku.dto.response.RingkasanTransaksiKategoriResponse;
import com.andreas.backend.keuanganku.dto.response.TransaksiResponse;
import org.springframework.data.domain.Page;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface TransaksiService {

    RingkasanBulanIni getRingkasanBulanIni(UUID idPengguna);

    public List<KategoriStatistikResponse> getPengeluaranPerKategoriBulanIni(UUID idPengguna);

    public List<CashflowItem> getDataGrafikCashflow(UUID idPengguna, int periode);

    void tambahTransaksi(UUID idPengguna, TransaksiRequest request);

    List<TransaksiResponse> getFilteredTransaksi(UUID idPengguna, Integer jenis, UUID idAkun);

    void updateTransaksi(UUID idPengguna, UUID idTransaksi, TransaksiRequest request);

    void hapusTransaksi(UUID idPengguna, UUID idTransaksi);

    List<TransaksiResponse> getRecentTransaksi(UUID idPengguna, int jumlah);

    RingkasanTransaksiKategoriResponse getDataTransaksiWaktuTertentu(UUID idPengguna, int periode);

    Page<TransaksiResponse> getFilteredTransaksi(
            UUID idPengguna,
            String keyword,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            Integer jenis,
            UUID idAkun,
            int page,
            int size
    );
}
