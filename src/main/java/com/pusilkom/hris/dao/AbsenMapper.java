package com.pusilkom.hris.dao;

import com.pusilkom.hris.model.AbsenModel;
import com.pusilkom.hris.model.KaryawanBaruModel;
import com.pusilkom.hris.model.KaryawanModel;
import org.apache.ibatis.annotations.*;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Mapper
public interface AbsenMapper {
    @Insert("INSERT INTO employee.\"ABSEN\" (id_karyawan,time_check_in)  " +
            "VALUES (#{idKaryawan}, now() )")
    void insertAbsen(KaryawanBaruModel karyawan);

    @Select("select distinct id, id_karyawan, time_check_in, time_check_out, detail from " +
            "employee.\"ABSEN\" " +
            "where id_karyawan = #{idKaryawan} " +
            "ORDER by time_check_in DESC LIMIT 1")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "idKaryawan", column = "id_karyawan"),
            @Result(property = "checkInTime", column = "time_check_in"),
            @Result(property = "checkOutTime", column = "time_check_out"),
            @Result(property = "detail", column = "detail")
    })
    AbsenModel selectKaryawanLatestCheckIn(KaryawanBaruModel karyawan);

    @Update("update employee.\"ABSEN\" set time_check_in = #{checkInTime}, " +
            "time_check_out = #{checkOutTime}, " +
            "detail = #{detail} " +
            "where id = #{id}")
    void updateAbsen(AbsenModel absen);

    @Select("select distinct id, id_karyawan, time_check_in, time_check_out, detail from " +
            "employee.\"ABSEN\" " +
            "where id_karyawan = #{idKaryawan} " +
            "ORDER by time_check_in DESC")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "idKaryawan", column = "id_karyawan"),
            @Result(property = "checkInTime", column = "time_check_in"),
            @Result(property = "checkOutTime", column = "time_check_out"),
            @Result(property = "detail", column = "detail")
    })
    List<AbsenModel> selectAbsenKaryawan(KaryawanBaruModel karyawan);

    @Select("select id, id_karyawan, time_check_in, time_check_out, detail from " +
            "employee.\"ABSEN\" " +
            "ORDER by time_check_in DESC")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "idKaryawan", column = "id_karyawan"),
            @Result(property = "checkInTime", column = "time_check_in"),
            @Result(property = "checkOutTime", column = "time_check_out"),
            @Result(property = "detail", column = "detail")
    })
    List<AbsenModel> selectAllAbsen();

    @Select("select id, id_karyawan, time_check_in, time_check_out, detail from " +
            "employee.\"ABSEN\" where time_check_in between '${tanggalAwal}'::timestamp " +
            "and '${tanggalAkhir}'::timestamp " +
            "ORDER by time_check_in DESC")
    @Results(value = {
            @Result(property = "id", column = "id"),
            @Result(property = "idKaryawan", column = "id_karyawan"),
            @Result(property = "checkInTime", column = "time_check_in"),
            @Result(property = "checkOutTime", column = "time_check_out"),
            @Result(property = "detail", column = "detail")
    })
    List<AbsenModel> selectAbsenByPeriode(@Param("tanggalAwal") Timestamp tanggalAwal, @Param("tanggalAkhir") Timestamp tanggalAkhir);
}
