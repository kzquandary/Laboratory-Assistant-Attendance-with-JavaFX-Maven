package Model;


public class Mahasiswa {
    private String nim;
    private String nama;
    private String nohp;

    public Mahasiswa(String nim, String nama, String nohp) {
        this.nim = nim;
        this.nama = nama;
        this.nohp = nohp;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNohp() {
        return nohp;
    }

    public void setNohp(String nohp) {
        this.nohp = nohp;
    }
}
