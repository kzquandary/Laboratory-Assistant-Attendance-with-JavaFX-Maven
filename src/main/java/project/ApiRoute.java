package project;

public class ApiRoute {
    public static String Login = Route.URL + "login";
    public static String LoginToken = Route.URL + "login/logintoken";
    public static String CheckToken = Route.URL + "login/token/{token}";
    public static String Logout = Route.URL + "login/logout";
    public static String GetMahasiswa = Route.URL + "mahasiswa";
    public static String StoreMahasiswa = Route.URL + "mahasiswa/store";
    public static String BatchStoreMahasiswa = Route.URL + "mahasiswa/batchstore";
    public static String UpdateMahasiswa = Route.URL + "mahasiswa/update/{id}";
    public static String DeleteMahasiswa = Route.URL + "mahasiswa/{id}";
    public static String GetMahasiswaByNim = Route.URL + "mahasiswa/nim/{nim}";
    public static String GetMahasiswaByNama = Route.URL + "mahasiswa/nama/{nama}";
    public static String GetMahasiswaByNohp = Route.URL + "mahasiswa/nohp/{nohp}";
    public static String BackupMahasiswa = Route.URL + "mahasiswa/backup";
    public static String GetPertemuan = Route.URL + "pertemuan";
    public static String StorePertemuan = Route.URL + "pertemuan/store";
    public static String UpdatePertemuan = Route.URL + "pertemuan/{id}";
    public static String DeletePertemuan = Route.URL + "pertemuan/delete/{id}";
    public static String GetAbsensi = Route.URL + "absensi";
    public static String GetAbsensiById = Route.URL + "absensi/{id}";
    public static String UpdateAbsensi = Route.URL + "absensi/update";
    public static String GetAbsensiByNim = Route.URL + "absensi/nim/{nim}";
    public static String GetLaporan = Route.URL + "laporan";
    public static String GetLaporanByNim = Route.URL + "laporan/nim/{nim}";
    public static String GetLaporanById = Route.URL + "laporan/{id}";
    public static String UpdateLaporan = Route.URL + "laporan/update";
    public static String DeleteLaporan = Route.URL + "laporan/{id}";
    public static String GetNilai = Route.URL + "nilai";
    public static String UpdateNilai = Route.URL + "nilai/update";
    public static String GetNilaiByNim = Route.URL + "nilai/nim/{nim}";
    public static String GetNilaiById = Route.URL + "nilai/{id}";
    public static String GetKeaktifan = Route.URL + "keaktifan";
    public static String GetKeaktifanByNim = Route.URL + "keaktifan/nim/{nim}";
    public static String StoreKeaktifan = Route.URL + "keaktifan/store";
    public static String UpdateKeaktifan = Route.URL + "keaktifan/update/{id}";
    public static String DeleteKeaktifan = Route.URL + "keaktifan/{id}";

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

    public static String setDeleteLaporan(String id) {
        return DeleteLaporan.replace("{id}", id);
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
