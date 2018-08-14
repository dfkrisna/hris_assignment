package com.pusilkom.hris.dao;

import com.pusilkom.hris.model.KaryawanProyekModel;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface KaryawanCutiMapper {

    @Select("SELECT CK.id_karyawan, CK.nama_lengkap, CK.periode_mulai, CK.periode_selesai, CK.is_disetjui\n" +
            "FROM \n" +
                "(SELECT DISTICT * FROM employee.\"CUTI\" as C INNER JOIN employee.\"KARYAWAN\" as K on C.id_karyawan = K.id\n" +
                "WHERE C.id IN (SELECT MAX(id) FROM employee.\"CUTI\") GROUP BY id_karyawan\n" +
                ") as CK \n" +
            "INNER JOIN employee.\"DIVISI\" as D on D.id = CK.id_divisi \n"+
            ";")
    @Results(value = {
            @Result(property="idKaryawan", column="id_karyawan"),
            @Result(property="namaLengkap", column="nama_lengkap"),
            @Result(property="jumlahHari", column="id_proyek"),
            @Result(property="startPeriode", column="periode_mulai"),
            @Result(property="endPeriode", column="periode_selesai"),
            @Result(property="isDisetujui", column="is_active")
    })
    List<KaryawanProyekModel> selectKaryawanByManager(@Param("idProyek") int idManager);
}
