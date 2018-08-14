package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbsenModel {
    private int id;
    private int idKaryawan;
    private LocalDate checkInTime;
    private LocalDate checkOutTime;
    private String detail;
}
