package com.andreas.backend.keuanganku.controller.secure;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.andreas.backend.keuanganku.annotation.CurrentUserId;
import com.andreas.backend.keuanganku.dto.request.TransaksiRequest;
import com.andreas.backend.keuanganku.dto.response.GeneralResponse;
import com.andreas.backend.keuanganku.dto.response.TransaksiResponse;
import com.andreas.backend.keuanganku.service.TransaksiService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/secure/transaksi")
@RequiredArgsConstructor
@Tag(name = "Transaksi", description = "Operasi terkait transaksi pengguna")
public class TransaksiController {

    private final TransaksiService transaksiService;

    @PostMapping
    @Operation(summary = "Tambah transaksi", description = "Menambahkan transaksi baru ke akun pengguna.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaksi berhasil ditambahkan")
    })
    public ResponseEntity<?> tambahTransaksi(
            @Parameter(hidden = true) @CurrentUserId UUID idPengguna,
            @Valid @RequestBody TransaksiRequest request
    ) {
        transaksiService.tambahTransaksi(idPengguna, request);
        return ResponseEntity.ok(new GeneralResponse<>("Transaksi berhasil ditambahkan", null, true));
    }

    @GetMapping
    @Operation(summary = "Ambil daftar transaksi", description = "Mengambil daftar transaksi berdasarkan filter tanggal, jenis, akun, keyword, dan pagination.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Berhasil mengambil daftar transaksi")
    })
    public ResponseEntity<?> getTransaksi(
            @Parameter(hidden = true) @CurrentUserId UUID idPengguna,

            @Parameter(description = "Tanggal awal (format: dd/MM/yyyy)", example = "01/07/2025")
            @RequestParam(name = "startDate", required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate startDate,

            @Parameter(description = "Tanggal akhir (format: dd/MM/yyyy)", example = "31/07/2025")
            @RequestParam(name = "endDate", required = false)
            @DateTimeFormat(pattern = "dd/MM/yyyy") LocalDate endDate,

            @Parameter(description = "Jenis transaksi (1 = pengeluaran, 2 = pemasukan)", example = "1")
            @RequestParam(name = "jenis", required = false) Integer jenis,

            @Parameter(description = "ID akun pengguna") 
            @RequestParam(name = "idAkun", required = false) UUID idAkun,

            @Parameter(description = "Nomor halaman (mulai dari 0)", example = "0")
            @RequestParam(name = "page", defaultValue = "0") int page,

            @Parameter(description = "Keyword untuk pencarian deskripsi transaksi")
            @RequestParam(name = "keyword", required = false) String keyword,

            @Parameter(description = "Jumlah item per halaman", example = "10")
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        Page<TransaksiResponse> daftar = transaksiService.getFilteredTransaksi(
                idPengguna, keyword, startDate, endDate, jenis, idAkun, page, size
        );
        return ResponseEntity.ok(GeneralResponse.fromPage(daftar));
    }

    @GetMapping("/data-terbaru")
    @Operation(summary = "Ambil transaksi terbaru", description = "Mengambil transaksi terbaru dari pengguna dengan jumlah maksimum tertentu.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Berhasil mengambil transaksi terbaru")
    })
    public ResponseEntity<?> getRecentTransaksi(
            @Parameter(hidden = true) @CurrentUserId UUID idPengguna,

            @Parameter(description = "Jumlah maksimum transaksi yang ditampilkan", example = "5")
            @RequestParam(name = "limit", defaultValue = "5") int limit
    ) {
        List<TransaksiResponse> recentTransaksi = transaksiService.getRecentTransaksi(idPengguna, limit);
        return ResponseEntity.ok(new GeneralResponse<>("Ok", recentTransaksi, true));
    }

    @PutMapping("/{id_transaksi}")
    @Operation(summary = "Update transaksi", description = "Memperbarui data transaksi yang sudah ada berdasarkan ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaksi berhasil diperbarui")
    })
    public ResponseEntity<?> updateTransaksi(
            @Parameter(hidden = true) @CurrentUserId UUID idPengguna,

            @Parameter(description = "ID transaksi yang akan diperbarui", example = "e4fbb111-5ab1-4e4b-87a0-64f54f9c2df0")
            @PathVariable("id_transaksi") UUID idTransaksi,

            @Valid @RequestBody TransaksiRequest request
    ) {
        transaksiService.updateTransaksi(idPengguna, idTransaksi, request);
        return ResponseEntity.ok(new GeneralResponse<>("Transaksi berhasil diperbarui", null, true));
    }

    @DeleteMapping("/{id_transaksi}")
    @Operation(summary = "Hapus transaksi", description = "Menghapus transaksi berdasarkan ID milik pengguna.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Transaksi berhasil dihapus")
    })
    public ResponseEntity<?> hapusTransaksi(
            @Parameter(hidden = true) @CurrentUserId UUID idPengguna,

            @Parameter(description = "ID transaksi yang akan dihapus", example = "e4fbb111-5ab1-4e4b-87a0-64f54f9c2df0")
            @PathVariable("id_transaksi") UUID idTransaksi
    ) {
        transaksiService.hapusTransaksi(idPengguna, idTransaksi);
        return ResponseEntity.ok(new GeneralResponse<>("Transaksi berhasil dihapus", null, true));
    }
}
