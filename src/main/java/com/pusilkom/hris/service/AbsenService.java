package com.pusilkom.hris.service;

import com.pusilkom.hris.model.AbsenModel;
import com.pusilkom.hris.model.KaryawanBaruModel;
import com.pusilkom.hris.model.KaryawanModel;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AbsenService {
    void checkIn(KaryawanBaruModel karyawan);

    AbsenModel getKaryawanLatestCheckIn(KaryawanBaruModel karyawan);

    boolean isCheckedIn(KaryawanBaruModel karyawan);

    void updateAbsen(AbsenModel absen);

    void checkOut(KaryawanBaruModel karyawan, String detail);

    List<AbsenModel> getAbsenKaryawan(KaryawanBaruModel karyawan);

    Timestamp modifyTime(Timestamp time);

    Map mapDurasiAbsen(List<AbsenModel> absens);

    List<AbsenModel> getAllAbsen();

    List<AbsenModel> getAbsenByPeriode(LocalDate periode);

    Map mapAbsenKaryawan(List<AbsenModel> absens);
}
