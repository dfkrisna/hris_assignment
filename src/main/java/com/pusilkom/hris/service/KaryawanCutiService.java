package com.pusilkom.hris.service;

import com.pusilkom.hris.model.FeedbackRatingModel;
import com.pusilkom.hris.model.KaryawanBaruModel;
import com.pusilkom.hris.model.KaryawanCutiModel;
import com.pusilkom.hris.model.KaryawanModel;
import com.pusilkom.hris.model.KaryawanProyekModel;
import org.apache.tomcat.jni.Local;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public interface KaryawanCutiService {

	List<KaryawanCutiModel> getAll(String email);

	List<KaryawanCutiModel> getHistoryByKaryawanId(int idKaryawan);

	boolean approve(int idKaryawanCuti);

	void cancel(int idKaryawanCuti);

	void tolak(int idKaryawanCuti);

	void cancelTolak(int idKaryawanCuti);

    void submitCutiKaryawan(KaryawanCutiModel kc);

	KaryawanCutiModel getCutiById(Integer id);

	void updateCuti(KaryawanCutiModel cutiBaru);
}
