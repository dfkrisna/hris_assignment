package com.pusilkom.hris.dao;

import com.pusilkom.hris.model.KaryawanCutiModel;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface KaryawanCutiMapper {

    @Select("SELECT CK.id, CK.id_karyawan, CK.nama_lengkap, CK.jumlah_hari, CK.periode_mulai, CK.periode_selesai, CK.is_disetujui, CK.is_tolak, CK.detail\n" +
            "FROM \n" +
                "(SELECT DISTINCT K.id_divisi, C.id_karyawan, K.nama_lengkap, C.jumlah_hari, C.periode_mulai, C.periode_selesai, C.is_disetujui, C.is_tolak, C.id FROM employee.\"CUTI\" as C INNER JOIN employee.\"KARYAWAN\" as K on C.id_karyawan = K.id\n" +
                "WHERE C.id IN (SELECT MAX(id) FROM employee.\"CUTI\" GROUP BY id_karyawan) \n" +
                ") as CK \n" +
            "INNER JOIN employee.\"DIVISI\" as D on D.id = CK.id_divisi \n"+
            "WHERE D.id_manager = #{idManager}\n" +
            "ORDER BY CK.id DESC;")
    @Results(value = {
            @Result(property="id", column="id"),
            @Result(property="idKaryawan", column="id_karyawan"),
            @Result(property="namaLengkap", column="nama_lengkap"),
            @Result(property="jumlahHari", column="jumlah_hari"),
            @Result(property="startPeriode", column="periode_mulai"),
            @Result(property="endPeriode", column="periode_selesai"),
            @Result(property="isDisetujui", column="is_disetujui"),
            @Result(property="isTolak", column="is_tolak"),
            @Result(property="detail", column="detail")
    })
    List<KaryawanCutiModel> selectKaryawanByManager(@Param("idManager") int idManager);


    @Select("SELECT C.id, K.nama_lengkap, C.id_karyawan, C.jumlah_hari, C.periode_mulai, C.periode_selesai, C.is_disetujui, C.is_tolak, C.detail\n" +
            "FROM employee.\"CUTI\" as C INNER JOIN employee.\"KARYAWAN\" as K on C.id_karyawan = K.id \n" +
            "WHERE K.id = #{idKaryawan} ORDER BY C.id DESC;")
    @Results(value= {
            @Result(property="id", column="id"),
            @Result(property="idKaryawan", column="id_karyawan"),
            @Result(property="namaLengkap", column="nama_lengkap"),
            @Result(property="jumlahHari", column="jumlah_hari"),
            @Result(property="startPeriode", column="periode_mulai"),
            @Result(property="endPeriode", column="periode_selesai"),
            @Result(property="isDisetujui", column="is_disetujui"),
            @Result(property="isTolak", column="is_tolak"),
            @Result(property="detail", column="detail")
    })
    List<KaryawanCutiModel> getHistoryByKaryawanId(@Param("idKaryawan") int idKaryawan);

    @Update("UPDATE employee.\"CUTI\" SET is_disetujui = true WHERE id = #{idKaryawanCuti}")
    void approve(@Param("idKaryawanCuti") int idKaryawanCuti);

    @Update("UPDATE employee.\"CUTI\" SET is_disetujui = false WHERE id = #{idKaryawanCuti}")
    void cancel(@Param("idKaryawanCuti") int idKaryawanCuti);
    
    @Update("UPDATE employee.\"CUTI\" SET is_tolak = true, is_disetujui = false WHERE id = #{idKaryawanCuti}")
    void tolak(@Param("idKaryawanCuti") int idKaryawanCuti);

    @Update("UPDATE employee.\"CUTI\" SET is_tolak = false WHERE id = #{idKaryawanCuti}")
    void cancelTolak(@Param("idKaryawanCuti") int idKaryawanCuti);

    @Select("SELECT * FROM employee.\"CUTI\" WHERE id = #{idCuti}")
    @Results(value={
        @Result(property="id", column="id"),
        @Result(property="idKaryawan", column="id_karyawan"),
        @Result(property="jumlahHari", column="jumlah_hari"),
        @Result(property="startPeriode", column="periode_mulai"),
        @Result(property="endPeriode", column="periode_selesai"),
        @Result(property="isDisetujui", column="is_disetujui"),
        @Result(property="isTolak", column="is_tolak"),
        @Result(property="detail", column="detail")
    })
    KaryawanCutiModel getKaryawanCutiById(@Param("idCuti") int idCuti);

    @Insert("INSERT INTO employee.\"CUTI\" (id_karyawan, jumlah_hari, is_disetujui, periode_mulai, periode_selesai, is_tolak, detail) " +
            "VALUES (#{idKaryawan}, #{jumlahHari}, 'f', #{startPeriode}, #{endPeriode}, 'f', #{detail})")
    void insertCutiKaryawan(KaryawanCutiModel kc);

    @Update("UPDATE employee.\"CUTI\" SET jumlah_hari=#{jumlahHari}, periode_mulai=#{startPeriode}, " +
            "periode_selesai=#{endPeriode}, is_disetujui=#{isDisetujui}, is_tolak=#{isTolak}, detail=#{detail} WHERE id = #{id}")
    void update(KaryawanCutiModel cutiBaru);
}
