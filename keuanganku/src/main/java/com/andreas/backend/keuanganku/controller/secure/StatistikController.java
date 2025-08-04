package com.andreas.backend.keuanganku.controller.secure;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.CashflowItem;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.dto.response.RingkasanBulanIni;
import com.andreas.backend.keuanganku.dto.response.RingkasanTransaksiKategoriResponse;
import com.andreas.backend.keuanganku.service.TransaksiService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/secure/statistik")
@RequiredArgsConstructor
@Tag(name = "Statistik", description = "Endpoint terkait statistik transaksi pengguna")
public class StatistikController {

    private final TransaksiService transaksiService;

    @GetMapping("/data-cashflow-terhadap-waktu")
    public ResponseEntity<GeneralResponse<List<CashflowItem>>> getGrafikCashflow(
            @Parameter(hidden = true)
            @CurrentUserId UUID idPengguna,
            @Parameter(description = "Periode data: 1=mingguan, 2=bulanan, 3=tahunan", example = "1")
            @RequestParam(name = "periode", defaultValue = "1") int periode
    ) {
        List<CashflowItem> data = transaksiService.getDataGrafikCashflow(idPengguna, periode);
        return ResponseEntity.ok(new GeneralResponse<>("OK", data, true));
    }

    @GetMapping("/data-transaksi-terhadap-kategori")
    public ResponseEntity<GeneralResponse<RingkasanTransaksiKategoriResponse>> getRingkasanKategori(
            @Parameter(hidden = true)
            @CurrentUserId UUID idPengguna,
            @Parameter(description = "Periode data: 1=mingguan, 2=bulanan, 3=tahunan", example = "1")
            @RequestParam(name = "periode", defaultValue = "1") int periode
    ) {
        RingkasanTransaksiKategoriResponse data = transaksiService.getDataTransaksiWaktuTertentu(idPengguna, periode);
        return ResponseEntity.ok(new GeneralResponse<>("OK", data, true));
    }

    @GetMapping("/data-ringkasan-bulan-ini")
    public ResponseEntity<?> getRingkasanBulan(@CurrentUserId UUID idPengguna) {
        RingkasanBulanIni response = transaksiService.getRingkasanBulanIni(idPengguna);
        return ResponseEntity.ok(new GeneralResponse<>("OK", response, true));
    }
}