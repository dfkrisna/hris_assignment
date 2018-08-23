package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeluargaModel {
    private int id;
    private int idKaryawan;
    private String hubungan;
    private String nama;
    private String nik;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate tanggalLahir;
}