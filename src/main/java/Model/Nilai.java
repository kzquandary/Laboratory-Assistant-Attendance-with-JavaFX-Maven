package Model;

public class Nilai {
    private String kodeNilai;
    private String kodeLaporan;
    private String kodePertemuan;
    private String nim;
    private String nilai;

    public Nilai(){

    }
    public Nilai(String kodeNilai, String kodeLaporan, String kodePertemuan, String nim, String nilai) {
        this.kodeNilai = kodeNilai;
        this.kodeLaporan = kodeLaporan;
        this.kodePertemuan = kodePertemuan;
        this.nim = nim;
        this.nilai = nilai;
    }
    public String getKodeNilai() {
        return kodeNilai;
    }

    public void setKodeNilai(String kodeNilai) {
        this.kodeNilai = kodeNilai;
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

    public String getNilai() {
        return nilai;
    }

    public void setNilai(String nilai) {
        this.nilai = nilai;
    }
}

