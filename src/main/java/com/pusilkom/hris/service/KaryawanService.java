package com.pusilkom.hris.service;


import com.pusilkom.hris.model.FeedbackRatingModel;
import com.pusilkom.hris.model.KaryawanBaruModel;
import com.pusilkom.hris.model.KaryawanModel;
import com.pusilkom.hris.model.KaryawanProyekModel;
import com.pusilkom.hris.model.RiwayatGajiModel;
import com.pusilkom.hris.model.DataDiriModel;
import com.pusilkom.hris.model.KeluargaModel;
import com.pusilkom.hris.model.PendidikanModel;


import com.pusilkom.hris.model.*;

import org.apache.tomcat.jni.Local;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import javax.xml.crypto.Data;

public interface KaryawanService {

	String cekPenugasanKaryawanById(int idKaryawan);

	int getKaryawanIdByUsername(String username);

	int getKaryawanIdPenilai(int idKaryawan, int idProyek);

	KaryawanModel getKaryawanById(int idKaryawan);

	KaryawanModel getKaryawanByIdPengguna(int idPengguna);

	String verifyFeedbackRekan(int idRekan, int idPenilai, int idProyek, LocalDate periode);

	List<KaryawanModel> getKaryawanAll();

	List<KaryawanBaruModel> getKaryawanBaruAll();

	List<KaryawanModel> getKaryawanByKaryawanProyek(List<KaryawanProyekModel> karyawanProyekList);

	List<KaryawanModel> getKaryawanByPeriode(LocalDate periode);

	KaryawanModel selectKaryawanByEmail(String email);

	List<KaryawanModel> getKaryawanByDivisi(int idDivisi);

	List<Integer> getUserProyek(int idKaryawan);

	List<FeedbackRatingModel> getRekanSeproyekFeedback(List<Integer> idProyeks, int idKaryawan, LocalDate periodeNow);

	List<FeedbackRatingModel> getRekanSeproyek(List<Integer> idProyeks, int idKaryawan, LocalDate periodeNow);

	List<Integer> getAllDivisi();

	void addFeedbackRekan(String feedback, int rating, int idRekan, int idPenilai,
						  int idProyek, LocalDate periode, Timestamp tanggal);

	void updateFeedbackRekan(String feedback, int rating, int idRekan, int idPenilai,
							 int idProyek, LocalDate periode, Timestamp tanggal);


	List<KaryawanModel> selectNamaEmployeeAll();

	void addKaryawan(String namaLengkap, String namaPanggilan, String nip, int idDivisi,
					 String emailPusilkom, String emailPribadi);

	void deleteKaryawan(int idKaryawan);

	KaryawanBaruModel getKaryawanBaruById(int idKaryawan);

	String cekKaryawanIsManager(int idKaryawan);
	
	KaryawanBaruModel getKaryawanByUsername(String username);

	DataDiriModel getDataDiriByIdKaryawan(int idKaryawan);

	void insertDataDiri(DataDiriModel dataDiri);

	List<KeluargaModel> selectAnggotaKeluargaAll(int idKaryawan);

	void insertAnggotaKeluarga(KeluargaModel keluarga);

	void updateAnggotaKeluarga (KeluargaModel keluarga);

	void deleteAnggotaKeluarga (int id);

	List<RiwayatGajiModel> selectAllRiwayatGajiById(int idKaryawan);

	void insertGaji(int idKaryawan, int gaji);

	void updateGajiById(int idGaji, int gaji);

	void deleteGajiById(int idGaji);

	List<PendidikanModel> selectPendidikanAll(int idKaryawan);

	void insertPendidikan(PendidikanModel pendidikan);

	void deletePendidikan (int id);

	void updatePendidikan (PendidikanModel pendidikan);

	void activateKaryawan(int idKaryawan);

	void deActivateKaryawan(int idKaryawan);

    List<KontakDaruratModel> getKontakDaruratKaryawan(int idKaryawan);

	void addKontakDarurat(KontakDaruratModel kontak);

	void updateKontakDarurat(KontakDaruratModel kontak);

	void deleteKontakDaruratById(Integer idKontak);

    void updateKaryawanBaru(KaryawanBaruModel karyawan);
}
