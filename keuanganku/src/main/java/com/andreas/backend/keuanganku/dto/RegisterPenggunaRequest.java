package com.andreas.backend.keuanganku.dto;

public class RegisterPenggunaRequest {
    private String nama;
    private String email;
    private String password;

    public RegisterPenggunaRequest(){};

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
