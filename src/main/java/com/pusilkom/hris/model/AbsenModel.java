package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AbsenModel {
    private int id;
    private int idKaryawan;
    private Timestamp checkInTime;
    private Timestamp checkOutTime;
    private String detail;
}
