package com.pusilkom.hris.service;

import com.pusilkom.hris.model.AbsenModel;
import com.pusilkom.hris.model.KaryawanBaruModel;
import com.pusilkom.hris.model.KaryawanModel;

import java.time.LocalDate;

public interface AbsenService {
    void checkIn(KaryawanBaruModel karyawan);

    AbsenModel getKaryawanLatestCheckIn(KaryawanBaruModel karyawan);

    boolean isCheckedIn(KaryawanBaruModel karyawan);

    void updateAbsen(AbsenModel absen);

    void checkOut(KaryawanBaruModel karyawan, String detail);
}
