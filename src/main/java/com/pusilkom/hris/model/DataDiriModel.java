package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDiriModel {
    int id;
    int idKaryawan;
    String tempatLahir;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate tanggalLahir;
    String noHp;
    String alamatTinggal;
    String nomorKtp;
    String npwp;
}

