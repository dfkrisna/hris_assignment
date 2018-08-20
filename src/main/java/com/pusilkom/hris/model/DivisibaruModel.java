package com.pusilkom.hris.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class DivisibaruModel {
    private int id;
    private String kodeDivisi;
    private String nama;
    private String keterangan;
    private Integer idManager;
    private String namaManager;
}
