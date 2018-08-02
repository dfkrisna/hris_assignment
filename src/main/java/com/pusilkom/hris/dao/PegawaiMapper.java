package com.pusilkom.hris.dao;

import com.pusilkom.hris.model.PegawaiModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

@Mapper
public interface PegawaiMapper {

    @Select("select id, nama from mpp.\"PEGAWAI\" as pg where not exists (select * from mpp.\"PENGGUNA\" as pe where pg.id = pe.id_pegawai);")
    List<PegawaiModel> getAllAvailablePegawai();

    @Select("select alamat from mpp.\"PEGAWAI\" where id = #{id}")
    String getAlamatPengguna(@Param("id") int id);

    @Select("select nomor_telepon from mpp.\"PEGAWAI\" where id = #{id}")
    String getTelpPengguna(@Param("id") int id);

    @Select("select jenis_kelamin from mpp.\"PEGAWAI\" where id = #{id}")
    String getGenderPengguna(@Param("id") int id);

    @Insert("insert into mpp.\"PEGAWAI\" (nama) values (#{nama})")
    void addPegawai(@Param("nama") String namaPegawai);

    @Select("select id from mpp.\"PEGAWAI\" where nama = #{nama}")
    String getIdByName(@Param("nama") String namaPegawai);

}
