package com.pusilkom.hris.service;

import com.pusilkom.hris.dao.AbsenMapper;
import com.pusilkom.hris.model.AbsenModel;
import com.pusilkom.hris.model.KaryawanModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
@Primary
public class AbsenServiceImpl implements AbsenService{
    @Autowired
    AbsenMapper absenMapper;

    @Override
    public void checkIn(KaryawanModel karyawan) {
        absenMapper.insertAbsen(karyawan);
    }

    @Override
    public AbsenModel getKaryawanLatestCheckIn(KaryawanModel karyawan) {
        AbsenModel absenKaryawan = absenMapper.selectKaryawanLatestCheckIn(karyawan);
        return absenKaryawan;
    }
}
