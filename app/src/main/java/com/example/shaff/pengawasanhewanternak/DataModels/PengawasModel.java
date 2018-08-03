package com.example.shaff.pengawasanhewanternak.DataModels;

public class PengawasModel {
    private String id_pengawas;
    private String user_name;
    private String password;
    private String email;

    public PengawasModel() {
    }

    public String getId_pengawas() {
        return id_pengawas;
    }

    public void setId_pengawas(String id_pengawas) {
        this.id_pengawas = id_pengawas;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
