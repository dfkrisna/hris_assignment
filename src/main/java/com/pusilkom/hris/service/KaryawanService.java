package com.pusilkom.hris.service;

import com.pusilkom.hris.model.*;

import org.apache.tomcat.jni.Local;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import javax.xml.crypto.Data;

public interface KaryawanService {

	String cekPenugasanKaryawanById(int idKaryawan);

	int getKaryawanIdPenilai(int idKaryawan, int idProyek);

	KaryawanBaruModel getKaryawanById(int idKaryawan);

	String verifyFeedbackRekan(int idRekan, int idPenilai, int idProyek, LocalDate periode);

	List<KaryawanBaruModel> getKaryawanAll();

	List<KaryawanBaruModel> getKaryawanBaruAll();

	List<KaryawanBaruModel> getKaryawanByKaryawanProyek(List<KaryawanProyekModel> karyawanProyekList);

	List<KaryawanBaruModel> getKaryawanByPeriode(LocalDate periode);

	KaryawanBaruModel selectKaryawanByEmail(String email);

	List<KaryawanBaruModel> getKaryawanByDivisi(int idDivisi);

	List<Integer> getUserProyek(int idKaryawan);

	List<FeedbackRatingModel> getRekanSeproyekFeedback(List<Integer> idProyeks, int idKaryawan, LocalDate periodeNow);

	List<FeedbackRatingModel> getRekanSeproyek(List<Integer> idProyeks, int idKaryawan, LocalDate periodeNow);

	List<Integer> getAllDivisi();

	List<BenefitKaryawanModel> getBenefitKaryawan(int id);

	void addFeedbackRekan(String feedback, int rating, int idRekan, int idPenilai,
						  int idProyek, LocalDate periode, Timestamp tanggal);

	void updateFeedbackRekan(String feedback, int rating, int idRekan, int idPenilai,
							 int idProyek, LocalDate periode, Timestamp tanggal);


	List<KaryawanBaruModel> selectNamaEmployeeAll();

	void addKaryawan(String namaLengkap, String namaPanggilan, String nip, int idDivisi,
					 String emailPusilkom, String emailPribadi);

	void deleteKaryawan(int idKaryawan);

	KaryawanBaruModel getKaryawanBaruById(int idKaryawan);

	String cekKaryawanIsManager(int idKaryawan);
	
	KaryawanBaruModel getKaryawanByUsername(String username);

	void updateKaryawanDivisi(int idKaryawan, int idDivisi);

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

	void updateBenefitKaryawan(int idBenefit, String keteranganBaru);

	void deleteBenefitKaryawan(int idBenefi);

	void addBenefitKaryawan(int idKaryawan, String namaBenefit, String keteranganBenefit);

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


	List<KontrakModel> selectKontrakAll(int idKaryawan);

	void insertKontrak(KontrakModel kontrak);

	void updateKontrak (KontrakModel kontrak);

	void deleteKontrak (int id);

	List<DokumenModel> getAllDokumenKaryawanById(int idKaryawan);

	void insertDokumen(int idKaryawan, String fileName);

	DokumenModel getDokumen(int idDokumen);

	void updateKaryawanBaru(KaryawanBaruModel karyawan);
	
	void deleteDokumen(int idDokumen);

}
