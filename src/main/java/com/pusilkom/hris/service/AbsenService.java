package com.pusilkom.hris.service;

import com.pusilkom.hris.model.AbsenModel;
import com.pusilkom.hris.model.KaryawanModel;

import java.time.LocalDate;

public interface AbsenService {
    void checkIn(KaryawanModel karyawan);

    AbsenModel getKaryawanLatestCheckIn(KaryawanModel karyawan);
}
