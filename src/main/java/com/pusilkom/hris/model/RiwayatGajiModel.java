package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RiwayatGajiModel {
    int id;
    int idKaryawan;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate tanggalAktif;
    int nilaiGaji;
}

