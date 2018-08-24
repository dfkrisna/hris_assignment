package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PendidikanModel {
    private int id;
    private int idKaryawan;
    private String institusi;
    private String gelar;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate periodeMulai;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate periodeSelesai;
}