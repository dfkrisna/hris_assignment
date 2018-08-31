package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KontrakModel {
    private int id;
    private int idKaryawan;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate tanggalKontrak;
    private Integer durasi;
    private String pihakKontraktor;

}
