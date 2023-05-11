package model;

public class Pertemuan {
    private String kode_pertemuan;
    private String tanggal_pertemuan;

    public Pertemuan() {
    }

    public Pertemuan(String kode_pertemuan, String tanggal_pertemuan) {
        this.kode_pertemuan = kode_pertemuan;
        this.tanggal_pertemuan = tanggal_pertemuan;
    }

    public String getKode_pertemuan() {
        return kode_pertemuan;
    }

    public void setKode_pertemuan(String kode_pertemuan) {
        this.kode_pertemuan = kode_pertemuan;
    }

    public String getTanggal_pertemuan() {
        return tanggal_pertemuan;
    }

    public void setTanggal_pertemuan(String tanggal_pertemuan) {
        this.tanggal_pertemuan = tanggal_pertemuan;
    }
}

