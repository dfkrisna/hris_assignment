package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KontakDaruratModel {
    private int id;
    private int idKaryawan;
    private String nama;
    private String hubungan;
    private String kontak;
    private Timestamp timestamp;
}
