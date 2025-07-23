package com.andreas.backend.keuanganku.controller.secure;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.CashflowItem;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.dto.response.RingkasanTransaksiKategoriResponse;
import com.andreas.backend.keuanganku.service.TransaksiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secure/statistik")
@RequiredArgsConstructor
@Tag(name = "Statistik", description = "Endpoint terkait statistik transaksi pengguna")
public class StatistikController {

    private final TransaksiService transaksiService;

    @Operation(
        summary = "Ambil data grafik cashflow",
        description = "Mengambil data cashflow (pemasukan & pengeluaran) berdasarkan periode: 1=mingguan, 2=bulanan, 3=tahunan"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Berhasil mengambil data cashflow")
    })
    @GetMapping("/data-cashflow-tiap-waktu")
    public ResponseEntity<GeneralResponse<List<CashflowItem>>> getGrafikCashflow(
        @Parameter(hidden = true)
        @CurrentUserId UUID idPengguna,

        @Parameter(description = "Periode data: 1=mingguan, 2=bulanan, 3=tahunan", example = "1")
        @RequestParam(name = "periode", defaultValue = "1") int periode
    ) {
        List<CashflowItem> data = transaksiService.getDataGrafikCashflow(idPengguna, periode);
        return ResponseEntity.ok(new GeneralResponse<>("OK", data, true));
    }

    @Operation(
        summary = "Ambil ringkasan transaksi per kategori",
        description = "Mengambil ringkasan transaksi berdasarkan kategori untuk periode waktu tertentu: 1=mingguan, 2=bulanan, 3=tahunan"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Berhasil mengambil ringkasan transaksi")
    })
    @GetMapping("/data-transaksi-tiap-kategori")
    public ResponseEntity<GeneralResponse<RingkasanTransaksiKategoriResponse>> getRingkasanKategori(
        @Parameter(hidden = true)
        @CurrentUserId UUID idPengguna,

        @Parameter(description = "Periode data: 1=mingguan, 2=bulanan, 3=tahunan", example = "1")
        @RequestParam(name = "periode", defaultValue = "1") int periode
    ) {
        RingkasanTransaksiKategoriResponse data = transaksiService.getDataTransaksiWaktuTertentu(idPengguna, periode);
        return ResponseEntity.ok(new GeneralResponse<>("OK", data, true));
    }
}
