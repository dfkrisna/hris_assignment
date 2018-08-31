package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KaryawanBaruModel {
    int idKaryawan;
    String namaLengkap;
    String namaPanggilan;
    String nip;
    int idDivisi;
    String emailPusilkom;
    String emailPribadi;
    boolean isActive;
    String tempatLahir;
    LocalDate tanggalLahir;
    String noHandPhone;
    String alamatTinggal;
    String noKTP;
    String npwp;
    private int ratingKaryawan;
}

