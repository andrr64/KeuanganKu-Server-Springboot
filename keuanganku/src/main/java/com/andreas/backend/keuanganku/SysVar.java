package com.andreas.backend.keuanganku;

public class SysVar {
    public static int PENGELUARAN = 1;
    public static int PEMASUKAN = 2;

    public static boolean isPengeluaran(int jenis) {
        return jenis == PENGELUARAN;
    }

    public static boolean isPemasukan(int jenis) {
        return jenis == PEMASUKAN;
    }
}
