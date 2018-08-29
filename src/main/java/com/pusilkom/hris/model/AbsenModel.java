package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbsenModel {
    private int id;
    private int idKaryawan;
    private Timestamp checkInTime;
    private Timestamp checkOutTime;
    private String detail;
    public String getAbsenInterval() {
        Timestamp tmpCheckOut;
        if(checkOutTime == null) {
            tmpCheckOut = Timestamp.valueOf(LocalDateTime.now());
        } else {
            tmpCheckOut = checkOutTime;
        }
        long different = tmpCheckOut.getTime() - checkInTime.getTime();
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;
        String absenInterval = "";
        if(elapsedHours > 0) {
            absenInterval += elapsedHours + " jam ";
        }
        if(elapsedMinutes > 0) {
            absenInterval += elapsedMinutes + " menit ";
        }
        if(elapsedSeconds > 0) {
            absenInterval += elapsedSeconds + " detik";
        }
        return absenInterval;
    }
}
