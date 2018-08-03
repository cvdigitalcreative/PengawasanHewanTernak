package com.example.shaff.pengawasanhewanternak.DataModels;

public class LaporanModel {
    private String id_laporan;
    private String tanggal_laporan;
    private String lokasi_kandang;
    private String kode_hewan;
    private String jenis_sapi;
    private int umur;
    private int berat;
    private String jenis_pakan;
    private String catatan_penting;
    private String QR_Code;

    public LaporanModel() {
    }

    public String getId_laporan() {
        return id_laporan;
    }

    public void setId_laporan(String id_laporan) {
        this.id_laporan = id_laporan;
    }

    public String getTanggal_laporan() {
        return tanggal_laporan;
    }

    public void setTanggal_laporan(String tanggal_laporan) {
        this.tanggal_laporan = tanggal_laporan;
    }

    public String getLokasi_kandang() {
        return lokasi_kandang;
    }

    public void setLokasi_kandang(String lokasi_kandang) {
        this.lokasi_kandang = lokasi_kandang;
    }

    public String getKode_hewan() {
        return kode_hewan;
    }

    public void setKode_hewan(String kode_hewan) {
        this.kode_hewan = kode_hewan;
    }

    public String getJenis_sapi() {
        return jenis_sapi;
    }

    public void setJenis_sapi(String jenis_sapi) {
        this.jenis_sapi = jenis_sapi;
    }

    public int getUmur() {
        return umur;
    }

    public void setUmur(int umur) {
        this.umur = umur;
    }

    public int getBerat() {
        return berat;
    }

    public void setBerat(int berat) {
        this.berat = berat;
    }

    public String getJenis_pakan() {
        return jenis_pakan;
    }

    public void setJenis_pakan(String jenis_pakan) {
        this.jenis_pakan = jenis_pakan;
    }

    public String getCatatan_penting() {
        return catatan_penting;
    }

    public void setCatatan_penting(String catatan_penting) {
        this.catatan_penting = catatan_penting;
    }

    public String getQR_Code() {
        return QR_Code;
    }

    public void setQR_Code(String QR_Code) {
        this.QR_Code = QR_Code;
    }
}
