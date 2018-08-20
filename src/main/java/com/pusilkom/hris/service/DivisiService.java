package com.pusilkom.hris.service;

import com.pusilkom.hris.model.DivisiModel;
import com.pusilkom.hris.model.DivisibaruModel;

import java.util.List;

public interface DivisiService {
    DivisiModel getDivisiByManajer(int idManajer);

    DivisiModel getDivisiByID(int idDivisi);

    List<DivisibaruModel> selectAllDivisiAktif();

    void addDivisi (DivisibaruModel divisi);

    DivisibaruModel selectDivisiBaruByID(int id);

    void updateDivisi (DivisibaruModel divisi);

    void nonAktifkanDivisi (int id);

    List<DivisibaruModel> selectAllDivisiNonAktif();

    void aktifkanDivisi (int id);

    void hapusDivisi (int id);
}
