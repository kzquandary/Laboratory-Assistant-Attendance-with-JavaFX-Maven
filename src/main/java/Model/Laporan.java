package Model;

public class Laporan {
    private String kodeLaporan;
    private String kodePertemuan;
    private String nim;
    private String status;

    public Laporan(){

    }
    public Laporan(String kodeLaporan, String kodePertemuan, String nim, String status) {
        this.kodeLaporan = kodeLaporan;
        this.kodePertemuan = kodePertemuan;
        this.nim = nim;
        this.status = status;
    }

    public String getKodeLaporan() {
        return kodeLaporan;
    }
    public void setKodeLaporan(String kodeLaporan) {
        this.kodeLaporan = kodeLaporan;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

