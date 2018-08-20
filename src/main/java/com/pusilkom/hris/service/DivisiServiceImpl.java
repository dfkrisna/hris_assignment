package com.pusilkom.hris.service;

import com.pusilkom.hris.dao.DivisiMapper;
import com.pusilkom.hris.model.DivisiModel;
import com.pusilkom.hris.model.DivisibaruModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Primary
public class DivisiServiceImpl implements DivisiService {
    @Autowired
    DivisiMapper divisiMapper;

    @Override
    public DivisiModel getDivisiByManajer(int idManajer) {
        return divisiMapper.selectDivisiByManajer(idManajer);
    }

    @Override
    public DivisiModel getDivisiByID(int idDivisi) {
        return divisiMapper.selectDivisiByID(idDivisi);
    }

    @Override
    public List<DivisibaruModel> selectAllDivisiAktif(){return divisiMapper.selectAllDivisiAktif();}

    @Override
    public void addDivisi (DivisibaruModel divisi){divisiMapper.addDivisi(divisi);}

    @Override
    public DivisibaruModel selectDivisiBaruByID(int id){return divisiMapper.selectDivisiBaruByID(id);}

    @Override
    public void updateDivisi (DivisibaruModel divisi){divisiMapper.updateDivisi(divisi);}

    @Override
    public void nonAktifkanDivisi (int id){divisiMapper.nonAktifkanDivisi(id);}

    @Override
    public List<DivisibaruModel> selectAllDivisiNonAktif(){return divisiMapper.selectAllDivisiNonAktif();}

    @Override
    public void aktifkanDivisi (int id){divisiMapper.aktifkanDivisi(id);}

    @Override
    public void hapusDivisi (int id){divisiMapper.hapusDivisi(id);}
}
