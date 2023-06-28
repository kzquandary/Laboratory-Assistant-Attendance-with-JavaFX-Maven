package Project;

public class ApiRoute {
    public static final String URL = "http://127.0.0.1:8000/api/";
    public static String Login = URL + "login";
    public static String LoginToken = URL + "login/logintoken";
    public static String CheckToken = URL + "login/token/{token}";
    public static String Logout = URL + "login/logout";
    public static String updateUsername = URL + "login/gantiusername";
    public static String updatePassword = URL + "login/gantipassword";
    public static String GetMahasiswa = URL + "mahasiswa";
    public static String StoreMahasiswa = URL + "mahasiswa/store";
    public static String BatchStoreMahasiswa = URL + "mahasiswa/batchstore";
    public static String UpdateMahasiswa = URL + "mahasiswa/update/{id}";
    public static String DeleteMahasiswa = URL + "mahasiswa/{id}";
    public static String GetMahasiswaByNim = URL + "mahasiswa/nim/{nim}";
    public static String GetMahasiswaByNama = URL + "mahasiswa/nama/{nama}";
    public static String GetMahasiswaByNohp = URL + "mahasiswa/nohp/{nohp}";
    public static String CheckMhs = URL + "mahasiswa/checkmhs/";
    public static String GetPertemuan = URL + "pertemuan";
    public static String StorePertemuan = URL + "pertemuan/store";
    public static String UpdatePertemuan = URL + "pertemuan/{id}";
    public static String DeletePertemuan = URL + "pertemuan/delete/{id}";
    public static String GetAbsensiById = URL + "absensi/{id}";
    public static String UpdateAbsensi = URL + "absensi/update";
    public static String GetAbsensiByNim = URL + "absensi/nim/{nim}";
    public static String GetLaporan = URL + "laporan";
    public static String GetLaporanByNim = URL + "laporan/nim/{nim}";
    public static String GetLaporanById = URL + "laporan/{id}";
    public static String UpdateLaporan = URL + "laporan/update";
    public static String UpdateNilai = URL + "nilai/update";
    public static String GetNilaiByNim = URL + "nilai/nim/{nim}";
    public static String GetNilaiById = URL + "nilai/{id}";
    public static String GetKeaktifan = URL + "keaktifan";
    public static String GetKeaktifanByNim = URL + "keaktifan/nim/{nim}";
    public static String StoreKeaktifan = URL + "keaktifan/store";
    public static String UpdateKeaktifan = URL + "keaktifan/update/{id}";
    public static String DeleteKeaktifan = URL + "keaktifan/{id}";
    public static String BackupMahasiswa = URL + "backup";
    public static String TruncateData = URL + "truncate";
    public static String Import = URL + "import";
    public static String Report = URL + "report";
    public static String Rekap = URL + "rekap";
    public static String DownloadReport = URL + "downloadreport";
    // Setter untuk rute yang memerlukan parameter
    public static String setCheckToken(String token) {
        return CheckToken.replace("{token}", token);
    }

    public static String setUpdateMahasiswa(String id) {
        return UpdateMahasiswa.replace("{id}", id);
    }

    public static String setDeleteMahasiswa(String id) {
        return DeleteMahasiswa.replace("{id}", id);
    }

    public static String setGetMahasiswaByNim(String nim) {
        return GetMahasiswaByNim.replace("{nim}", nim);
    }

    public static String setGetMahasiswaByNama(String nama) {
        return GetMahasiswaByNama.replace("{nama}", nama);
    }

    public static String setGetMahasiswaByNohp(String nohp) {
        return GetMahasiswaByNohp.replace("{nohp}", nohp);
    }

    public static String setUpdatePertemuan(String id) {
        return UpdatePertemuan.replace("{id}", id);
    }

    public static String setDeletePertemuan(String id) {
        return DeletePertemuan.replace("{id}", id);
    }

    public static String setGetAbsensiById(String id) {
        return GetAbsensiById.replace("{id}", id);
    }

    public static String setGetAbsensiByNim(String nim) {
        return GetAbsensiByNim.replace("{nim}", nim);
    }

    public static String setGetLaporanByNim(String nim) {
        return GetLaporanByNim.replace("{nim}", nim);
    }

    public static String setGetLaporanById(String id) {
        return GetLaporanById.replace("{id}", id);
    }


    public static String setGetNilaiByNim(String nim) {
        return GetNilaiByNim.replace("{nim}", nim);
    }

    public static String setGetNilaiById(String id) {
        return GetNilaiById.replace("{id}", id);
    }

    public static String setGetKeaktifanByNim(String nim) {
        return GetKeaktifanByNim.replace("{nim}", nim);
    }

    public static String setUpdateKeaktifan(String id) {
        return UpdateKeaktifan.replace("{id}", id);
    }

    public static String setDeleteKeaktifan(String id) {
        return DeleteKeaktifan.replace("{id}", id);
    }
}
