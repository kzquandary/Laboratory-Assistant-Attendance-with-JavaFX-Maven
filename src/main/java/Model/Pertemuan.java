package Model;

import java.time.LocalDate;

public class Pertemuan {
    private String kode_pertemuan;
    private LocalDate tanggal_pertemuan;

    public Pertemuan() {
    }

    public Pertemuan(String kode_pertemuan, LocalDate tanggal_pertemuan) {
        this.kode_pertemuan = kode_pertemuan;
        this.tanggal_pertemuan = tanggal_pertemuan;
    }

    public String getKode_pertemuan() {
        return kode_pertemuan;
    }

    public void setKode_pertemuan(String kode_pertemuan) {
        this.kode_pertemuan = kode_pertemuan;
    }

    public LocalDate getTanggal_pertemuan() {
        return tanggal_pertemuan;
    }

    public void setTanggal_pertemuan(LocalDate tanggal_pertemuan) {
        this.tanggal_pertemuan = tanggal_pertemuan;
    }
}
