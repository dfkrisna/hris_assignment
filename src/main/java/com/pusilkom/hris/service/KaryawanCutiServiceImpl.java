package com.pusilkom.hris.service;

import com.pusilkom.hris.dao.KaryawanMapper;
import com.pusilkom.hris.dao.KaryawanCutiMapper;
import com.pusilkom.hris.model.KaryawanCutiModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Primary
public class KaryawanCutiServiceImpl implements KaryawanCutiService{
	@Autowired
	KaryawanCutiMapper karyawanCutiMapper;

	@Autowired
	KaryawanMapper karyawanMapper;

	@Override
	public List<KaryawanCutiModel> getAll(String email){
		String id = karyawanMapper.getKaryawanIdByEmail(email);
		if(id != null){
			return karyawanCutiMapper.selectKaryawanByManager(Integer.parseInt(id));
		}
		return new ArrayList<KaryawanCutiModel>();
	}

	@Override
	public List<KaryawanCutiModel> getHistoryByKaryawanId(int idKaryawan){
		for(KaryawanCutiModel k : karyawanCutiMapper.getHistoryByKaryawanId(idKaryawan)){
			log.info(k.getNamaLengkap());
		}
		return karyawanCutiMapper.getHistoryByKaryawanId(idKaryawan);
	}

	@Override
	public boolean approve(int idKaryawanCuti){
		KaryawanCutiModel karyawanCuti = karyawanCutiMapper.getKaryawanCutiById(idKaryawanCuti);
		log.info(karyawanCuti.isTolak()+"");
		if(!karyawanCuti.isTolak()){
			karyawanCutiMapper.approve(idKaryawanCuti);
			return true;
		}
		return false;
	}

	@Override
	public void cancel(int idKaryawanCuti){
		karyawanCutiMapper.cancel(idKaryawanCuti);
	}

	@Override
	public void tolak(int idKaryawanCuti){
		karyawanCutiMapper.tolak(idKaryawanCuti);
	}

	@Override
	public void cancelTolak(int idKaryawanCuti){
		karyawanCutiMapper.cancelTolak(idKaryawanCuti);
	}
}
