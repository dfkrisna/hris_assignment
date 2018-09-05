package com.pusilkom.hris.dao;

import com.pusilkom.hris.model.DivisiModel;
import com.pusilkom.hris.model.DivisibaruModel;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DivisiMapper {

    @Select("SELECT *\n" +
            "\tFROM employee.\"DIVISI\"\n" +
            "\tWHERE id_manager=#{idManager};")
    @Results(value = {
            @Result(property="id", column="id"),
            @Result(property="kodeDivisi", column="kode_divisi"),
            @Result(property="nama", column="nama"),
            @Result(property="idManager", column="id_manager"),
            @Result(property="keterangan", column="keterangann")
    })
    DivisibaruModel selectDivisiByManajer(@Param("idManager") int idManager);


//     @Select("SELECT id, kode, nama_divisi, id_manager, id_divisi_induk\n" +
//             "\tFROM mpp.\"DIVISI\"\n" +
//             "\tWHERE id=#{id_divisi};")
//     @Results(value = {
//             @Result(property="id", column="id"),
//             @Result(property="kode", column="kode"),
//             @Result(property="namaDivisi", column="nama_divisi"),
//             @Result(property="idManajer", column="id_manager"),
//             @Result(property="idDivisiInduk", column="id_divisi_induk")
//     })
//     DivisiModel selectDivisiByID(@Param("id_divisi") int idDivisi);

//     @Delete("delete from mpp.\"DIVISI\" where id_manager = #{idManager}")
//     void deleteManajerDivisi(@Param("idManager") int idManager);

    @Select("SELECT D.id, D.kode_divisi, D.nama, D.keterangan, K.nama_lengkap,D.id_manager\n" +
            "\tFROM employee.\"DIVISI\" as D FULL OUTER JOIN employee.\"KARYAWAN\" as K on D.id_manager = K.id\n" +
            "\tWHERE D.is_aktif=true;")
    @Results(value = {
            @Result(property="id", column="id"),
            @Result(property="kodeDivisi", column="kode_divisi"),
            @Result(property="nama", column="nama"),
            @Result(property="idManager", column="id_manager"),
            @Result(property="namaManager", column="nama_lengkap"),
            @Result(property="keterangan", column="keterangan")
    })
    List<DivisibaruModel> selectAllDivisiAktif();

    @Insert("INSERT INTO employee.\"DIVISI\" (nama, kode_divisi, keterangan, id_manager)"
            + " VALUES (#{nama}, #{kodeDivisi}, #{keterangan}, #{idManager})")
    void addDivisi (DivisibaruModel divisi);


    @Select("SELECT D.id, D.kode_divisi, D.nama, D.keterangan, K.nama_lengkap,D.id_manager\n" +
            "\tFROM employee.\"DIVISI\" as D INNER JOIN employee.\"KARYAWAN\" as K on D.id_manager = K.id\n" +
            "\tWHERE D.id=#{id} ORDER BY id DESC;")

    @Results(value = {
            @Result(property="id", column="id"),
            @Result(property="kodeDivisi", column="kode_divisi"),
            @Result(property="nama", column="nama"),
            @Result(property="idManager", column="id_manager"),
            @Result(property="namaManager", column="nama_lengkap"),
            @Result(property="keterangan", column="keterangan")
    })
    DivisibaruModel selectDivisiBaruByID(@Param("id") int id);

    @Update("UPDATE employee.\"DIVISI\"  SET nama = #{nama}, kode_divisi = #{kodeDivisi}, id_manager = #{idManager}, " +
            "keterangan = #{keterangan} WHERE id = #{id}")
    void updateDivisi (DivisibaruModel divisi);

    @Select("SELECT D.id, D.kode_divisi, D.nama, D.keterangan, K.nama_lengkap,D.id_manager\n" +
            "\tFROM employee.\"DIVISI\" as D INNER JOIN employee.\"KARYAWAN\" as K on D.id_manager = K.id\n" +
            "\tWHERE D.is_aktif=false ORDER BY id DESC;")
    @Results(value = {
            @Result(property="id", column="id"),
            @Result(property="kodeDivisi", column="kode_divisi"),
            @Result(property="nama", column="nama"),
            @Result(property="idManager", column="id_manager"),
            @Result(property="namaManager", column="nama_lengkap"),
            @Result(property="keterangan", column="keterangan")
    })
    List<DivisibaruModel> selectAllDivisiNonAktif();

    @Update("UPDATE employee.\"DIVISI\"  SET is_aktif=false WHERE id = #{id}")
    void nonAktifkanDivisi (@Param("id") int id);

    @Update("UPDATE employee.\"DIVISI\"  SET is_aktif=true WHERE id = #{id}")
    void aktifkanDivisi (@Param("id") int id);

    @Delete("DELETE FROM employee.\"DIVISI\" WHERE id = #{id}")
    void hapusDivisi (@Param("id") int id);
}
