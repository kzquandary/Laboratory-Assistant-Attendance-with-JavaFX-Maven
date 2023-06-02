package Model;

public class Absensi {
    private String kode_absen;
    private String kode_pertemuan;
    private String nim;
    private String status;
    private String nama;
    public Absensi(){

    }
    public Absensi(String kode_absen, String kode_pertemuan, String nim, String status, String nama) {
        this.kode_absen = kode_absen;
        this.kode_pertemuan = kode_pertemuan;
        this.nim = nim;
        this.status = status;
        this.nama = nama;
    }
    public Absensi(String kode_absen, String kode_pertemuan, String nim, String status) {
        this.kode_absen = kode_absen;
        this.kode_pertemuan = kode_pertemuan;
        this.nim = nim;
        this.status = status;
    }

    public String getKodeAbsen() {
        return kode_absen;
    }

    public void setKodeAbsen(String kode_absen) {
        this.kode_absen = kode_absen;
    }

    public String getKodePertemuan() {
        return kode_pertemuan;
    }

    public void setKodePertemuan(String kode_pertemuan) {
        this.kode_pertemuan = kode_pertemuan;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public void setNama(String nama){
        this.nama = nama;
    }
    public String getNama(){
        return nama;
    }

}
