package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KaryawanCutiModel {
    private int id;
    private int idKaryawan;
    private String namaLengkap;
    private int jumlahHari;
    private boolean isDisetujui;
    private boolean isTolak;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startPeriode;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endPeriode;
    
    public String getPeriode() {
        if (startPeriode == null && endPeriode == null) {
            return "-";
        } else if (endPeriode == null) {
            return startPeriode.getMonth() + " " + startPeriode.getYear() + " - Present";
        } else {
            return (startPeriode.getMonth() + " " + startPeriode.getYear() + " - " + endPeriode.getMonth() + " " + endPeriode.getYear());
        }
    }
}
