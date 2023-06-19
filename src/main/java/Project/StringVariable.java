package Project;

public class StringVariable {
    public static String Mahasiswa = "Mahasiswa";
    public static String Pertemuan = "Pertemuan";
    public static String Absensi = "Absensi";
    public static String Laporan = "Laporan";
    public static String Nilai = "Nilai";
    public static String Keaktifan = "Keaktifan";
    public static String GET = "GET";
    public static String POST = "POST";
    public static String ContentType = "Content-Type";
    public static String ApiError = "API Tidak Merespon, Harap konfigurasi API terlebih dahulu";
    public static String FormatError = "Harap isi form sesuai dengan format";
    public static String DataFormatError = "Pilih data terlebih dahulu dan isi form sesuai dengan format";
    public static String EmptyForm = "Harap isi Form terebih dahulu";
    public static String DeleteData = "Apakah Anda yakin ingin menghapus data?";
    public static String Confirmation = "Apakah Anda yakin ingin melakukan aksi ini? Aksi ini tidak bisa di batalkan";
    public static String Error = "Telah Terjadi Error, Harap Hubungi Administrator";
    public static String Cancel = "Batal Melakukan Action";
    public static final String RegNama = "^(\\S+\\s){0,9}\\S+$";
    public static final String RegNIM = "^3411[0-9][0-9]1\\d+$";
    public static final String RegNoHP = "^(62|0)\\d+$";

    public static String EmptyData(String name) {
        return ("Pilih " + name + " Terlebih Dahulu");
    }

    public static String ExceptionE(String e) {
        return ("Telah Terjadi Error: " + e);
    }

    public static String PilihData(String nama) {
        return ("Harap Pilih " + nama + " Yang Ingin Dihapus");
    }

    public static String BerhasilTambah(String nama) {
        return (nama + " Berhasil Ditambahkan");
    }

    public static String BerhasilHapus(String nama) {
        return (nama + " Berhasil Dihapus");
    }

    public static String BerhasilUpdate(String nama) {
        return (nama + " Berhasil Diupdate");
    }

    public static String GagalTambah(String nama) {
        return ("Gagal Menambahkan " + nama);
    }

    public static String GagalHapus(String nama) {
        return ("Gagal Menghapus " + nama);
    }

    public static String GagalUpdate(String nama) {
        return ("Gagal Mengupdate " + nama);
    }

    public static String ErrorTambah(String nama) {
        return ("Error Saat Menambahkan " + nama);
    }

    public static String ErrorHapus(String nama) {
        return ("Error Saat Menghapus " + nama);
    }

    public static String ErrorUpdate(String nama) {
        return ("Error Saat Menghapus " + nama);
    }
}
