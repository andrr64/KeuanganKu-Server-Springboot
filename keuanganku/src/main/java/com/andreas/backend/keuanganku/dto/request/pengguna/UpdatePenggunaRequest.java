package com.andreas.backend.keuanganku.dto.request.pengguna;

public class UpdatePenggunaRequest {
    private String nama;
    private String email;
    private String passwordKonfirmasi;
    private String passwordBaru;

    // Getters dan setters
    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPasswordKonfirmasi() {
        return passwordKonfirmasi;
    }
    public void setPasswordKonfirmasi(String passwordKonfirmasi) {
        this.passwordKonfirmasi = passwordKonfirmasi;
    }
    public String getPasswordBaru() {
        return passwordBaru;
    }
    public void setPasswordBaru(String passwordBaru) {
        this.passwordBaru = passwordBaru;
    }
}
