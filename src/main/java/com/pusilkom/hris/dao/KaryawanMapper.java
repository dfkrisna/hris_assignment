package com.pusilkom.hris.dao;

import com.pusilkom.hris.model.*;

import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface KaryawanMapper {

        @Select("select id from mpp.\"KARYAWAN_PROYEK\" where id_karyawan = #{idKaryawan}")
        List<String> cekPenugasanKaryawanById(@Param("idKaryawan") int idKaryawan);

        @Select("SELECT K.* FROM employee.\"KARYAWAN\" as K WHERE K.is_deleted = false")
        @Results(value = { @Result(property = "idKaryawan", column = "id"),
                        @Result(property = "namaLengkap", column = "nama_lengkap"),
                        @Result(property = "namaPanggilan", column = "nama_panggilan"),
                        @Result(property = "nip", column = "nip"), @Result(property = "idDivisi", column = "id_divisi"),
                        @Result(property = "emailPusilkom", column = "email_pusilkom"),
                        @Result(property = "emailPribadi", column = "email_pribadi"),
                        @Result(property = "isActive", column = "is_active") })
        List<KaryawanBaruModel> selectKaryawanBaruAll();

        @Select("SELECT * FROM employee.\"KARYAWAN\" AS K WHERE K.is_active = true AND K.id IN \n"
                        + "(SELECT id_karyawan FROM mpp.\"KARYAWAN_PROYEK\"\n" + "WHERE start_periode <= '${periode}'\n"
                        + "AND (end_periode >= '${periode}' OR end_periode IS NULL) ORDER BY start_periode ASC);")
        @Results(value = { @Result(property = "idKaryawan", column = "id"),
                        @Result(property = "namaLengkap", column = "nama_lengkap"),
                        @Result(property = "namaPanggilan", column = "nama_panggilan"),
                        @Result(property = "nip", column = "nip"), @Result(property = "idDivisi", column = "id_divisi"),
                        @Result(property = "emailPusilkom", column = "email_pusilkom"),
                        @Result(property = "emailPribadi", column = "email_pribadi"),
                        @Result(property = "isActive", column = "is_active") })
        List<KaryawanBaruModel> selectKaryawanByPeriode(@Param("idPeriode") LocalDate periode);

        @Select("SELECT * FROM employee.\"KARYAWAN\" AS K\n"
                        + "WHERE K.id_divisi = #{idDivisi};")
        @Results(value = { @Result(property = "idKaryawan", column = "id"),
                        @Result(property = "namaLengkap", column = "nama_lengkap"),
                        @Result(property = "namaPanggilan", column = "nama_panggilan"),
                        @Result(property = "nip", column = "nip"), @Result(property = "idDivisi", column = "id_divisi"),
                        @Result(property = "emailPusilkom", column = "email_pusilkom"),
                        @Result(property = "emailPribadi", column = "email_pribadi"),
                        @Result(property = "isActive", column = "is_active") })
        List<KaryawanBaruModel> selectKaryawanByDivisi(@Param("idDivisi") int idDivisi);

        @Select("select distinct kp.id from mpp.\"RATING_FEEDBACK\" as rt, mpp.\"KARYAWAN_PROYEK\" as kp "
                        + "where kp.id_karyawan = #{idKaryawan} and kp.id_proyek = #{idProyek}"
                        + "ORDER by kp.id DESC LIMIT 1")
        String getKaryawanIdPenilai(@Param("idKaryawan") int idKaryawan, @Param("idProyek") int idProyek);

        @Select("select id_proyek from mpp.\"KARYAWAN_PROYEK\" where id_karyawan = #{idKaryawan}")
        List<Integer> getUserProyek(@Param("idKaryawan") int idKaryawan);

        @Select("select kr.id_karyawan as idRekan, k.nama_lengkap as namaRekan, rp.nama as roleRekan, "
                        + "pr.kode as kodeProyek, rt.rating as ratingRekan, rt.feedback as feedback " + "from "
                        + "mpp.\"ROLE_PROYEK\" as rp, " + "mpp.\"PROYEK\" as pr, mpp.\"RATING_FEEDBACK\" as rt, "
                        + "employee.\"KARYAWAN\" as k, mpp.\"KARYAWAN_PROYEK\" as kr "
                        + "where kr.id_karyawan != #{idKaryawan} and kr.id_proyek = #{idProyek} and kr.id_karyawan = k.id "
                        + "and kr.id_role = rp.id and kr.id_proyek = pr.id "
                        + "and kr.start_periode <= #{periodeNow} and (not kr.end_periode < #{periodeNow} or kr.end_periode is null) "
                        + "and kr.id = rt.id_karyawan_dinilai "
                        + "and rt.id_penilai = #{idPenilai} and rt.periode = #{periodeNow}")
        List<FeedbackRatingModel> getRekanSeproyekFeedback(@Param("idProyek") int idProyek,
                        @Param("idKaryawan") int idKaryawan, @Param("periodeNow") LocalDate periodeNow,
                        @Param("idPenilai") int idPenilai);

        @Select("select kr.id_karyawan as idRekan, k.nama_lengkap as namaRekan, rp.nama as roleRekan, "
                        + "pr.kode as kodeProyek, 0 as ratingRekan, " + "'Belum memberikan Feedback' as feedback "
                        + "from mpp.\"ROLE_PROYEK\" as rp, mpp.\"PROYEK\" as pr, employee.\"KARYAWAN\" as k, mpp.\"KARYAWAN_PROYEK\" as kr "
                        + "where kr.id_karyawan != #{idKaryawan}\n"
                        + "and kr.id_proyek = #{idProyek} and kr.id_karyawan = k.id "
                        + "and kr.id_role = rp.id and kr.id_proyek = pr.id "
                        + "and kr.start_periode <= #{periodeNow} and (not kr.end_periode < #{periodeNow} or kr.end_periode is null) "
                        + "and not exists"
                        + "(select * from mpp.\"RATING_FEEDBACK\" as rt where kr.id = rt.id_karyawan_dinilai "
                        + "and rt.periode = #{periodeNow})")
        List<FeedbackRatingModel> getRekanSeproyek(@Param("idProyek") int idProyek, @Param("idKaryawan") int idKaryawan,
                        @Param("periodeNow") LocalDate periodeNow);


        @Select("select rating as ratingRekan, feedback as feedback\n" 
                        + "from mpp.\"RATING_FEEDBACK\"\n"
                        + "where id_karyawan_dinilai = #{idKaryawanProyek} and id_penilai = #{idPenilai}\n"
                        + "and id_proyek = #{idProyek} and periode = #{periodeSelected}")
        FeedbackRatingModel verifyFeedbackRekan(@Param("idKaryawanProyek") int idKaryawanProyek,
                        @Param("idPenilai") int idPenilai, @Param("idProyek") int idProyek,
                        @Param("periodeSelected") LocalDate periodeSelected);

        @Insert("insert into mpp.\"RATING_FEEDBACK\" (rating, feedback, tanggal, id_karyawan_dinilai, id_penilai, periode, id_proyek)\n"
                        + "values (#{ratingRekan}, #{feedback}, #{waktuIsi}, #{idKaryawanProyek}, #{idPenilai}, #{periodeSelected}, "
                        + "#{idProyek})")
        void addFeedbackRekan(@Param("feedback") String feedback, @Param("ratingRekan") int ratingRekan,
                        @Param("idKaryawanProyek") int idKaryawanProyek, @Param("idPenilai") int idPenilai,
                        @Param("idProyek") int idProyek, @Param("periodeSelected") LocalDate periodeSelected,
                        @Param("waktuIsi") Timestamp waktuIsi);

        @Update("update mpp.\"RATING_FEEDBACK\" "
                        + "set feedback = #{feedback}, rating = #{ratingRekan}, tanggal = #{waktuIsi} "
                        + "where id_karyawan_dinilai = #{idKaryawanProyek} and id_penilai = #{idPenilai} "
                        + "and id_proyek = #{idProyek} and periode = #{periodeSelected}")
        void updateFeedbackRekan(@Param("feedback") String feedback, @Param("ratingRekan") int ratingRekan,
                        @Param("idKaryawanProyek") int idKaryawanProyek, @Param("idPenilai") int idPenilai,
                        @Param("idProyek") int idProyek, @Param("periodeSelected") LocalDate periodeSelected,
                        @Param("waktuIsi") Timestamp waktuIsi);

        @Select("SELECT DISTINCT id, nama_lengkap\n " + "FROM employee.\"KARYAWAN\"")
        @Results(value = { @Result(property = "id", column = "id"),
                        @Result(property = "nama", column = "nama_lengkap") })
        List<KaryawanBaruModel> selectNamaEmployeeAll();

        @Update("UPDATE employee.\"KARYAWAN\"\n" + "\tSET is_deleted=true\n" + "\tWHERE id=#{id}")
        void deleteKaryawanBaru(@Param("id") int id);

        @Insert("INSERT INTO employee.\"KARYAWAN\""
                        + "(nama_lengkap, nama_panggilan, nip, email_pusilkom, email_pribadi, is_active, id_divisi)\n"
                        + "\tVALUES (#{namaLengkap}, #{namaPanggilan}, #{nip}, #{emailPusilkom}, #{emailPribadi}, #{isActive}, #{idDivisi})")
        void addKaryawan(@Param("namaLengkap") String namaLengkap, @Param("namaPanggilan") String namaPanggilan,
                        @Param("nip") String nip, @Param("emailPusilkom") String emailPusilkom,
                        @Param("emailPribadi") String emailPribadi, @Param("idDivisi") int idDivisi,
                        @Param("isActive") boolean isActive);

        @Select("SELECT id FROM employee.\"KARYAWAN\" where email_pusilkom = #{email} ORDER BY id DESC LIMIT 1;")
        String getKaryawanIdByEmail(@Param("email") String email);

        @Select("SELECT K.* FROM employee.\"KARYAWAN\" as K WHERE K.id = #{idKaryawan}")
        @Results(value = { @Result(property = "idKaryawan", column = "id"),
                        @Result(property = "namaLengkap", column = "nama_lengkap"),
                        @Result(property = "namaPanggilan", column = "nama_panggilan"),
                        @Result(property = "nip", column = "nip"), @Result(property = "idDivisi", column = "id_divisi"),
                        @Result(property = "emailPusilkom", column = "email_pusilkom"),
                        @Result(property = "emailPribadi", column = "email_pribadi"),
                        @Result(property = "isActive", column = "is_active") })
        KaryawanBaruModel getKaryawanBaruById(@Param("idKaryawan") int idKaryawan);

        @Select("SELECT d.id_manager " + "FROM employee.\"DIVISI\" as D " + "WHERE D.id_manager=#{idKaryawan}")
        String cekKaryawanIsManager(@Param("idKaryawan") int idKaryawan);

        @Select("SELECT b.id, b.id_karyawan, b.nama_benefit, b.keterangan " + "FROM employee.\"BENEFIT\" as b "
                        + "WHERE b.id_karyawan=#{idKaryawan}")
        @Results(value = { @Result(property = "namaBenefit", column = "nama_benefit"),
                        @Result(property = "keteranganBenefit", column = "keterangan"),
                        @Result(property = "id", column = "id") })
        List<BenefitKaryawanModel> getBenefitKaryawan(int idKaryawan);

        @Select("select id, nama_lengkap, nama_panggilan, nip, email_pusilkom, email_pribadi, is_active, id_divisi "
                        + "from employee.\"KARYAWAN\" where email_pusilkom=#{email}")
        @Results(value = { @Result(property = "idKaryawan", column = "id"),
                        @Result(property = "namaLengkap", column = "nama_lengkap"),
                        @Result(property = "namaPanggilan", column = "nama_panggilan"),
                        @Result(property = "nip", column = "nip"),
                        @Result(property = "emailPusilkom", column = "email_pusilkom"),
                        @Result(property = "emailPribadi", column = "email_pribadi"),
                        @Result(property = "isActive", column = "is_active"),
                        @Result(property = "idDivisi", column = "id_divisi") })
        KaryawanBaruModel selectKaryawanByUsername(@Param("email") String username);

        @Select("SELECT * FROM employee.\"DATA_DIRI\" WHERE id_karyawan=#{idKaryawan} ORDER BY id DESC LIMIT 1")
        @Results(value = { @Result(property = "id", column = "id"),
                        @Result(property = "idKaryawan", column = "id_karyawan"),
                        @Result(property = "tempatLahir", column = "tempat_lahir"),
                        @Result(property = "tanggalLahir", column = "tanggal_lahir"),
                        @Result(property = "noHp", column = "no_hp"),
                        @Result(property = "alamatTinggal", column = "alamat_tinggal"),
                        @Result(property = "nomorKtp", column = "nomor_ktp"),
                        @Result(property = "npwp", column = "npwp") })
        DataDiriModel getDataDiriByIdKaryawan(int idKaryawan);

        @Insert("INSERT INTO employee.\"DATA_DIRI\" (id_karyawan, tempat_lahir, tanggal_lahir, no_hp, alamat_tinggal, nomor_ktp, npwp) \n"
                        + "VALUES (#{idKaryawan}, #{tempatLahir}, #{tanggalLahir}, #{noHp}, #{alamatTinggal}, #{nomorKtp}, #{npwp})")
        void insertDataDiri(DataDiriModel dataDiri);

        @Select("SELECT * FROM employee.\"KELUARGA\" WHERE id_karyawan=#{idKaryawan} ORDER BY id DESC")
        @Results(value = { @Result(property = "id", column = "id"),
                        @Result(property = "idKaryawan", column = "id_karyawan"),
                        @Result(property = "nama", column = "nama"), @Result(property = "nik", column = "nik"),
                        @Result(property = "hubungan", column = "hubungan"),
                        @Result(property = "tanggalLahir", column = "tanggal_lahir") })
        List<KeluargaModel> selectAnggotaKeluargaAll(int idKaryawan);

        @Insert("INSERT INTO employee.\"KELUARGA\" (id_karyawan, hubungan, nama, tanggal_lahir, nik) "
                        + "VALUES (#{idKaryawan}, #{hubungan}, #{nama}, #{tanggalLahir}, #{nik})")
        void insertAnggotaKeluarga(KeluargaModel keluarga);

        @Update("UPDATE employee.\"KELUARGA\"  SET hubungan = #{hubungan}, nama = #{nama}, nik = #{nik}, tanggal_lahir = #{tanggalLahir}"
                        + " WHERE id = #{id}")
        void updateAnggotaKeluarga(KeluargaModel keluarga);

        @Delete("DELETE FROM employee.\"KELUARGA\" WHERE id = #{id}")
        void deleteAnggotaKeluarga(int id);

        @Select("SELECT * FROM employee.\"GAJI\" WHERE id_karyawan = #{idKaryawan} ORDER BY id DESC")
        @Results(value = { @Result(property = "id", column = "id"),
                        @Result(property = "idKaryawan", column = "id_karyawan"),
                        @Result(property = "tanggalAktif", column = "tanggal_aktif"),
                        @Result(property = "nilaiGaji", column = "nilai_gaji") })
        List<RiwayatGajiModel> selectAllRiwayatGajiById(int idKaryawan);

        @Insert("INSERT INTO employee.\"GAJI\" (id_karyawan, nilai_gaji) VALUES (#{idKaryawan}, #{gaji})")
        void insertGaji(@Param("idKaryawan") int idKaryawan, @Param("gaji") int gaji);

        @Update("UPDATE employee.\"GAJI\" set nilai_gaji=#{gaji} where id=#{idGaji}")
        void updateGajiById(@Param("idGaji") int idGaji, @Param("gaji") int gaji);

        @Insert("INSERT INTO employee.\"BENEFIT\"(\n" + "\tid_karyawan, nama_benefit, keterangan)\n"
                        + "\tVALUES (#{idKaryawan}, #{namaBenefit}, #{keteranganBenefit});")
        void insertBenefit(@Param("idKaryawan") int idKaryawan, @Param("namaBenefit") String namaBenefit,
                        @Param("keteranganBenefit") String keteranganBenefit);

        @Update("UPDATE employee.\"BENEFIT\"\n" + "\tSET keterangan=#{keteranganBaru}" + "\tWHERE id=#{idBenefit}")
        void updateBenefitKaryawanById(@Param("keteranganBaru") String keteranganBaru,
                        @Param("idBenefit") int idBenefit);

        @Delete("DELETE FROM employee.\"GAJI\" WHERE id = #{idGaji}")
        void deleteGajiById(@Param("idGaji") int idGaji);

        @Delete("DELETE FROM employee.\"BENEFIT\" WHERE id = #{idBenefit}")
        void deleteBenefitById(@Param("idBenefit") int idBenefit);

        @Select("SELECT * FROM employee.\"PENDIDIKAN\" WHERE id_karyawan=#{idKaryawan} ORDER BY id DESC")
        @Results(value = { @Result(property = "id", column = "id"),
                        @Result(property = "idKaryawan", column = "id_karyawan"),
                        @Result(property = "institusi", column = "institusi"),
                        @Result(property = "gelar", column = "gelar"),
                        @Result(property = "periodeMulai", column = "periode_mulai"),
                        @Result(property = "periodeSelesai", column = "periode_selesai") })
        List<PendidikanModel> selectPendidikanAll(int idKaryawan);

        @Insert("INSERT INTO employee.\"PENDIDIKAN\" (id_karyawan, gelar, institusi, periode_mulai, periode_selesai) "
                        + "VALUES (#{idKaryawan}, #{gelar}, #{institusi}, #{periodeMulai}, #{periodeSelesai})")
        void insertPendidikan(PendidikanModel pendidikan);

        @Delete("DELETE FROM employee.\"PENDIDIKAN\" WHERE id = #{id}")
        void deletePendidikan(int id);

        @Update("UPDATE employee.\"PENDIDIKAN\"  SET gelar = #{gelar}, institusi = #{institusi}, periode_mulai = #{periodeMulai}, periode_selesai = #{periodeSelesai}"
                        + " WHERE id = #{id}")
        void updatePendidikan(PendidikanModel pendidikan);

        @Update("UPDATE employee.\"KARYAWAN\" set is_active = true")
        void activateKaryawan(int idKaryawan);

        @Update("UPDATE employee.\"KARYAWAN\" set is_active = false ")
        void deActivateKaryawan(int idKaryawan);

        @Select("select * from employee.\"DATA_DARURAT\" as D where D.id_karyawan=#{idKaryawan} ORDER BY id DESC")
        @Results(value = { @Result(property = "id", column = "id"),
                        @Result(property = "idKaryawan", column = "id_karyawan"),
                        @Result(property = "nama", column = "nama"),
                        @Result(property = "hubungan", column = "hubungan"),
                        @Result(property = "kontak", column = "nomor_telepon"),
                        @Result(property = "timestamp", column = "timestamp") })
        List<KontakDaruratModel> selectKontakDaruratKaryawan(int idKaryawan);

        @Insert("INSERT INTO employee.\"DATA_DARURAT\" (id_karyawan, nama, hubungan, nomor_telepon, timestamp) \n"
                        + "VALUES (#{idKaryawan}, #{nama}, #{hubungan}, #{kontak}, now())")
        void insertKontakDarurat(KontakDaruratModel kontak);

        @Update("update employee.\"DATA_DARURAT\" set nama=#{nama}, hubungan=#{hubungan}, nomor_telepon=#{kontak} "
                        + "where id=#{id} and id_karyawan=#{idKaryawan}")
        void updateKontakDarurat(KontakDaruratModel kontak);

        @Delete("delete from employee.\"DATA_DARURAT\" where id=#{idKontak}")
        void deleteKontakDaruratById(Integer idKontak);

        @Select("SELECT * FROM employee.\"DATA_KONTRAK\" WHERE id_karyawan=#{idKaryawan} ORDER BY id DESC")
        @Results(value = { @Result(property = "id", column = "id"),
                        @Result(property = "idKaryawan", column = "id_karyawan"),
                        @Result(property = "tanggalKontrak", column = "tanggal_kontrak"),
                        @Result(property = "durasi", column = "durasi"),
                        @Result(property = "pihakKontraktor", column = "pihak_kontraktor") })
        List<KontrakModel> selectKontrakAll(int idKaryawan);

        @Insert("INSERT INTO employee.\"DATA_KONTRAK\" (id_karyawan, tanggal_kontrak, durasi, pihak_kontraktor) \n"
                        + "VALUES (#{idKaryawan}, #{tanggalKontrak}, #{durasi}, #{pihakKontraktor})")
        void insertKontrak(KontrakModel kontrak);

        @Update("UPDATE employee.\"DATA_KONTRAK\"  SET tanggal_kontrak = #{tanggalKontrak}, durasi = #{durasi}, pihak_kontraktor = #{pihakKontraktor}"
                        + " WHERE id = #{id}")
        void updateKontrak(KontrakModel kontrak);

        @Delete("DELETE FROM employee.\"DATA_KONTRAK\" WHERE id = #{id}")
        void deleteKontrak(int id);

        @Select("SELECT * FROM employee.\"DOKUMEN_PENDUKUNG\" WHERE id_karyawan = #{idKaryawan}")
        @Results(value = { @Result(property = "id", column = "id"),
                        @Result(property = "idKaryawan", column = "id_karyawan"),
                        @Result(property = "fileName", column = "nama_dokumen") })
        List<DokumenModel> getAllDokumenKaryawanById(@Param("idKaryawan") int idKaryawan);

        @Insert("INSERT INTO employee.\"DOKUMEN_PENDUKUNG\" (id_karyawan, nama_dokumen) VALUES (#{idKaryawan}, #{fileName})")
        void insertDokumen(@Param("idKaryawan") int idKaryawan, @Param("fileName") String fileName);

        @Select("SELECT * FROM employee.\"DOKUMEN_PENDUKUNG\" WHERE id = #{idDokumen}")
        @Results(value = { @Result(property = "id", column = "id"),
                        @Result(property = "idKaryawan", column = "id_karyawan"),
                        @Result(property = "fileName", column = "nama_dokumen") })
        DokumenModel getDokumen(@Param("idDokumen") int idDokumen);

        @Update("update employee.\"KARYAWAN\" set nama_lengkap=#{namaLengkap}, nama_panggilan=#{namaPanggilan}, nip=#{nip}, "
                        + "email_pusilkom=#{emailPusilkom}, email_pribadi=#{emailPribadi}, id_divisi=#{idDivisi} "
                        + "where id=#{idKaryawan}")
        void updateKaryawanBaru(KaryawanBaruModel karyawan);

        @Delete("DELETE FROM employee.\"DOKUMEN_PENDUKUNG\" WHERE id = #{idDokumen}")
        void deleteDokumen(@Param("idDokumen") int idDokumen);

        @Select("SELECT d.id " + "FROM employee.\"DIVISI\" as d")
        List<Integer> getAllDivisi();

        @Update("update employee.\"KARYAWAN\" set id_divisi=#{idDivisi} " +
            "where id=#{idKaryawan}")
        void updateKaryawanDivisi(@Param("idKaryawan")int idKaryawan, @Param("idDivisi")int idDivisi);
}
