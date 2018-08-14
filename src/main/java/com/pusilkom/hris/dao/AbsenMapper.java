package com.pusilkom.hris.dao;

import com.pusilkom.hris.model.AbsenModel;
import com.pusilkom.hris.model.KaryawanModel;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

@Mapper
public interface AbsenMapper {
    @Insert("INSERT INTO employee.\"ABSEN\" (id_karyawan,time_check_in)  " +
            "VALUES (#{id_karyawan}, now() )")
    void insertAbsen(KaryawanModel karyawan);

    @Select("select distinct id, id_karyawan, time_check_in, time_check_out from " +
            "employee.\"ABSEN\" " +
            "where id_karyawan = #{idKaryawan} and kp.id_proyek = #{idProyek}" +
            "ORDER by time_check_in DESC LIMIT 1")
    AbsenModel selectKaryawanLatestCheckIn(KaryawanModel karyawan);
}
