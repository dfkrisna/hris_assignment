package com.pusilkom.hris.service;

import com.pusilkom.hris.dao.AbsenMapper;
import com.pusilkom.hris.model.AbsenModel;
import com.pusilkom.hris.model.KaryawanBaruModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Primary
public class AbsenServiceImpl implements AbsenService{
    @Autowired
    AbsenMapper absenMapper;

    @Override
    public void checkIn(KaryawanBaruModel karyawan) {
        absenMapper.insertAbsen(karyawan);
    }

    @Override
    public AbsenModel getKaryawanLatestCheckIn(KaryawanBaruModel karyawan) {
        AbsenModel absenKaryawan = absenMapper.selectKaryawanLatestCheckIn(karyawan);
        return absenKaryawan;
    }

    @Override
    public boolean isCheckedIn(KaryawanBaruModel karyawan) {
        AbsenModel absen = getKaryawanLatestCheckIn(karyawan);
        if(absen != null) {
            if(absen.getCheckOutTime() != null) {
                return false;
            } else {
                if(absen.getCheckInTime().getDate() == LocalDate.now().getDayOfMonth()){
                    return true;
                } else {
                    LocalDateTime checkInDate = absen.getCheckInTime().toLocalDateTime();
                    if(checkInDate.getYear() == LocalDateTime.now().getYear()
                            && checkInDate.getMonth().equals(LocalDateTime.now().getMonth())
                            && checkInDate.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                        return true;
                    } else {
                        Timestamp checkOutTime = Timestamp.valueOf(LocalDateTime.of(LocalDate.from(checkInDate), LocalTime.MIDNIGHT.minusSeconds(1)));
                        absen.setCheckOutTime(checkOutTime);
                        updateAbsen(absen);
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
    }

    @Override
    public void updateAbsen(AbsenModel absen) {
        absenMapper.updateAbsen(absen);
    }

    @Override
    public void checkOut(KaryawanBaruModel karyawan, String detail) {
        AbsenModel absen = getKaryawanLatestCheckIn(karyawan);
        absen.setDetail(detail);
        absen.setCheckOutTime(Timestamp.valueOf(LocalDateTime.now()));
        updateAbsen(absen);
    }

    @Override
    public List<AbsenModel> getAbsenKaryawan(KaryawanBaruModel karyawan) {
        List<AbsenModel> absens = absenMapper.selectAbsenKaryawan(karyawan);

        for(AbsenModel absen: absens) {
            Timestamp checkIn = modifyTime(absen.getCheckInTime());
            absen.setCheckInTime(checkIn);
            if(absen.getCheckOutTime() != null) {
                Timestamp checkOut = modifyTime(absen.getCheckOutTime());
                absen.setCheckOutTime(checkOut);
            }
        }
        return absens;
    }

    @Override
    public Timestamp modifyTime(Timestamp time) {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String modif = sdfDate.format(time);
        Timestamp formattedTime = Timestamp.valueOf(modif);
        return formattedTime;
    }

    @Override
    public Map mapDurasiAbsen(List<AbsenModel> absens) {
        Map mapDurasi = new HashMap();

        for(AbsenModel absen : absens) {
            double durasi = 0;
            if(absen.getCheckOutTime() != null) {
                durasi = absen.getCheckOutTime().getTime() - absen.getCheckInTime().getTime();
            } else {
                durasi = Timestamp.valueOf(LocalDateTime.now()).getTime() - absen.getCheckInTime().getTime();
            }
            durasi = durasi/3600000;
            DecimalFormat formatter = new DecimalFormat("0.##");
            String durasiStr = formatter.format(durasi);
            mapDurasi.put(absen.getId(), durasiStr);
        }
        return mapDurasi;
    }
}
