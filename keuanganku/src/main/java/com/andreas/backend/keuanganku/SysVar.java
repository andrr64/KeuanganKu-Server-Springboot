package com.andreas.backend.keuanganku;

import java.util.Objects;

public class SysVar {

    public static final int PENGELUARAN = 1;
    public static final int PEMASUKAN = 2;

    public static boolean isPengeluaran(Integer jenis) {
        return Objects.equals(jenis, PENGELUARAN);
    }

    public static boolean isPemasukan(Integer jenis) {
        return Objects.equals(jenis, PEMASUKAN);
    }
}
