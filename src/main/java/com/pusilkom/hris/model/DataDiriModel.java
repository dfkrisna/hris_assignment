package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataDiriModel {
    int id;
    int idKaryawan;
    String tempatLahir;
    LocalDate tanggalLahir;
    String noHp;
    String alamatTinggal;
    String nomorKtp;
    String npwp;
}

