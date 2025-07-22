package com.andreas.backend.keuanganku.dto.request.kategori;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TambahKategoriRequest {
    @NotBlank(message = "Nama kategori tidak boleh kosong")
    private String nama;

    @Min(value = 1, message = "Jenis harus 1 (pengeluaran) atau 2 (pemasukan)")
    @Max(value = 2, message = "Jenis harus 1 (pengeluaran) atau 2 (pemasukan)")
    private int jenis;
}
