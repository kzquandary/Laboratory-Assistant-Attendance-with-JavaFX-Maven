package Model;

public class Keaktifan {
    private String kodeKeaktifan;
    private String kodePertemuan;
    private String nim;
    private String keterangan;
    private String nama;
    public Keaktifan(){

    }
    public Keaktifan(String kodeKeaktifan, String kodePertemuan, String nim, String keterangan, String nama) {
        this.kodeKeaktifan = kodeKeaktifan;
        this.kodePertemuan = kodePertemuan;
        this.nim = nim;
        this.keterangan = keterangan;
        this.nama = nama;
    }

    public Keaktifan(String kodeKeaktifan, String kodePertemuan, String nim, String keterangan) {
        this.kodeKeaktifan = kodeKeaktifan;
        this.kodePertemuan = kodePertemuan;
        this.nim = nim;
        this.keterangan = keterangan;
    }

    public String getKodeKeaktifan() {
        return kodeKeaktifan;
    }

    public void setKodeKeaktifan(String kodeKeaktifan) {
        this.kodeKeaktifan = kodeKeaktifan;
    }
    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKodePertemuan() {
        return kodePertemuan;
    }

    public void setKodePertemuan(String kodePertemuan) {
        this.kodePertemuan = kodePertemuan;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
