package com.pusilkom.hris.service;

import com.pusilkom.hris.dao.KaryawanMapper;

import com.pusilkom.hris.model.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.Data;

@Slf4j
@Service
@Primary
public class KaryawanServiceImpl implements KaryawanService{
	@Override
	public String cekPenugasanKaryawanById(int idKaryawan) {
		String hasil = "";
		List<String> listId = karyawanMapper.cekPenugasanKaryawanById(idKaryawan);

		if(listId.isEmpty()) {
			hasil= "no";
		}else {
			hasil= "yes";
		}

		return hasil;
	}

	@Autowired
    KaryawanMapper karyawanMapper;

	public List<KaryawanBaruModel> getKaryawanAll() {
		return karyawanMapper.selectKaryawanBaruAll();
	}

	public List<KaryawanBaruModel> getKaryawanBaruAll(){ return karyawanMapper.selectKaryawanBaruAll();}

	public KaryawanBaruModel getKaryawanById(int idKaryawan) {
		return karyawanMapper.getKaryawanBaruById(idKaryawan);
	}

	@Override
	public String verifyFeedbackRekan(int idRekan, int idPenilai, int idProyek, LocalDate periode) {
		String hasil = "";
		FeedbackRatingModel feedbackRekan = karyawanMapper.verifyFeedbackRekan(idRekan, idPenilai, idProyek, periode);

		if(feedbackRekan == null) {
			hasil = "belum ada";
		}else {
			hasil = "sudah ada";
		}
		return hasil;
	}

	public List<KaryawanBaruModel> getKaryawanByKaryawanProyek(List<KaryawanProyekModel> karyawanProyekList) {
		List<KaryawanBaruModel> karyawanList = new ArrayList<KaryawanBaruModel>();

		for(KaryawanProyekModel karyawanProyek : karyawanProyekList) {
			KaryawanBaruModel karyawan = getKaryawanBaruById(karyawanProyek.getIdKaryawan());
			if(!karyawanList.contains(karyawan)) {
				karyawanList.add(karyawanMapper.getKaryawanBaruById(karyawanProyek.getIdKaryawan()));
			}
		}

		return karyawanList;
	}

	public List<KaryawanBaruModel> getKaryawanByPeriode(LocalDate periode) {
		return karyawanMapper.selectKaryawanByPeriode(periode);
	}

	public KaryawanBaruModel selectKaryawanByEmail(String email){
		return karyawanMapper.selectKaryawanByUsername(email);
	}

	@Override
	public List<KaryawanBaruModel> getKaryawanByDivisi(int idDivisi) {
		return karyawanMapper.selectKaryawanByDivisi(idDivisi);
	}

	@Override
	public List<FeedbackRatingModel> getRekanSeproyekFeedback(List<Integer> idProyek, int idKaryawan, LocalDate periodeNow) {
		ArrayList<FeedbackRatingModel> hasil = new ArrayList<>();
		int idPenilai = 0;
		List<FeedbackRatingModel> rekan;

		for (int i = 0; i < idProyek.size(); i++){
			String temp = karyawanMapper.getKaryawanIdPenilai(idKaryawan, idProyek.get(i));
			if(temp != null){
				idPenilai = Integer.parseInt(temp);
				rekan = karyawanMapper.getRekanSeproyekFeedback(idProyek.get(i), idKaryawan, periodeNow, idPenilai);
			}else{
				rekan = new ArrayList();
			}

			if(rekan.size() >= 1) {
				for(int j = 0; j < rekan.size(); j++) {

					hasil.add(rekan.get(j));
				}
			}
		}
		return hasil;
	}

	@Override
	public List<FeedbackRatingModel> getRekanSeproyek(List<Integer> idProyek, int idKaryawan, LocalDate periodeNow) {

		ArrayList<FeedbackRatingModel> hasil = new ArrayList<>();

		for (int i = 0; i < idProyek.size(); i++){
			List<FeedbackRatingModel> rekan = karyawanMapper.getRekanSeproyek(idProyek.get(i), idKaryawan, periodeNow);

			if(rekan.size() >= 1) {
				for(int j = 0; j < rekan.size(); j++) {
					hasil.add(rekan.get(j));
				}
			}
		}
		return hasil;
	}

	@Override
	public List<BenefitKaryawanModel> getBenefitKaryawan(int id){
		List<BenefitKaryawanModel> listBenefitKaryawan = karyawanMapper.getBenefitKaryawan(id);
		return listBenefitKaryawan;
	}

	@Override
	public void addFeedbackRekan(String feedback, int rating, int idRekan, int idPenilai, int idProyek, LocalDate periode, Timestamp tanggal)
	{
		karyawanMapper.addFeedbackRekan(feedback, rating, idRekan, idPenilai, idProyek, periode, tanggal);
	}

	@Override
	public void updateFeedbackRekan(String feedback, int rating, int idRekan, int idPenilai, int idProyek, LocalDate periode, Timestamp tanggal)
	{
		karyawanMapper.updateFeedbackRekan(feedback, rating, idRekan, idPenilai, idProyek, periode, tanggal);
	}

	@Override
	public KaryawanBaruModel getKaryawanByUsername(String username) {
		KaryawanBaruModel karyawan = karyawanMapper.selectKaryawanByUsername(username);
		return karyawan;
	}

	@Override
	public int getKaryawanIdPenilai(int idKaryawan, int idProyek) {
		String idPenilai = karyawanMapper.getKaryawanIdPenilai(idKaryawan, idProyek);
		if(idPenilai == null){
			return 0;
		}
		else{
			return Integer.parseInt(idPenilai);
		}
	}

	@Override
	public List<Integer> getUserProyek(int idKaryawan) {
		List<Integer> listProyek = karyawanMapper.getUserProyek(idKaryawan);
		return listProyek;
	}

	@Override
	public List<KaryawanBaruModel> selectNamaEmployeeAll(){
		return karyawanMapper.selectKaryawanBaruAll();
	}

	public List<Integer> getAllDivisi(){
		List<Integer> listDivisi = karyawanMapper.getAllDivisi();
		return listDivisi;
	}

	@Override
	public void addKaryawan(String namaLengkap, String namaPanggilan, String nip, int idDivisi,
					 String emailPusilkom, String emailPribadi){
		karyawanMapper.addKaryawan(namaLengkap, namaPanggilan, nip, emailPusilkom, emailPribadi, idDivisi, true);
	}
	@Override
	public KaryawanBaruModel getKaryawanBaruById(int idKaryawan){
		return karyawanMapper.getKaryawanBaruById(idKaryawan);
	}

	@Override
	public void deleteKaryawan(int idKaryawan){
		karyawanMapper.deleteKaryawanBaru(idKaryawan);
	}

	@Override
	public String cekKaryawanIsManager(int idKaryawan){
		return karyawanMapper.cekKaryawanIsManager(idKaryawan);
	}

	@Override
	public DataDiriModel getDataDiriByIdKaryawan(int idKaryawan){
		if(karyawanMapper.getDataDiriByIdKaryawan(idKaryawan) == null){
			System.out.println("masuk sini");
		}
		return karyawanMapper.getDataDiriByIdKaryawan(idKaryawan);
	}

	@Override
	public void insertDataDiri(DataDiriModel dataDiri){
		karyawanMapper.insertDataDiri(dataDiri);
	}

	@Override
	public void updateKaryawanDivisi(int idKaryawan, int idDivisi){karyawanMapper.updateKaryawanDivisi(idKaryawan, idDivisi);}

	@Override
	public List<KeluargaModel> selectAnggotaKeluargaAll(int idKaryawan){return karyawanMapper.selectAnggotaKeluargaAll(idKaryawan);}

	@Override
	public void insertAnggotaKeluarga(KeluargaModel keluarga){karyawanMapper.insertAnggotaKeluarga(keluarga);}

	@Override
	public void updateAnggotaKeluarga (KeluargaModel keluarga){karyawanMapper.updateAnggotaKeluarga(keluarga);}

	@Override
	public void deleteAnggotaKeluarga (int id){karyawanMapper.deleteAnggotaKeluarga(id);}

	public List<RiwayatGajiModel> selectAllRiwayatGajiById(int idKaryawan){
		return karyawanMapper.selectAllRiwayatGajiById(idKaryawan);
	}

	@Override
	public void insertGaji(int idKaryawan, int gaji){
		karyawanMapper.insertGaji(idKaryawan, gaji);
	}

	@Override
	public void updateGajiById(int idGaji, int gaji){
		karyawanMapper.updateGajiById(idGaji, gaji);
	}

	@Override
	public void deleteGajiById(int idGaji){
		karyawanMapper.deleteGajiById(idGaji);
	}

	@Override
	public void addBenefitKaryawan(int idKaryawan, String namaBenefit, String keteranganBenefit){ karyawanMapper.insertBenefit(idKaryawan, namaBenefit, keteranganBenefit);}

	@Override
	public void updateBenefitKaryawan(int idBenefit, String keteranganBaru){ karyawanMapper.updateBenefitKaryawanById(keteranganBaru, idBenefit);}

	@Override
	public void deleteBenefitKaryawan(int idBenefit){karyawanMapper.deleteBenefitById(idBenefit);}

  @Override
	public List<PendidikanModel> selectPendidikanAll(int idKaryawan){return karyawanMapper.selectPendidikanAll(idKaryawan);}

	@Override
	public void insertPendidikan(PendidikanModel pendidikan){karyawanMapper.insertPendidikan(pendidikan);}

	@Override
	public void deletePendidikan (int id){karyawanMapper.deletePendidikan(id);}

	@Override
	public void updatePendidikan (PendidikanModel pendidikan){karyawanMapper.updatePendidikan(pendidikan);}
  
  @Override
	public void activateKaryawan(int idKaryawan){
		karyawanMapper.activateKaryawan(idKaryawan);
	}

	@Override
	public void deActivateKaryawan(int idKaryawan){
		karyawanMapper.deActivateKaryawan(idKaryawan);
	}

	@Override
	public List<KontakDaruratModel> getKontakDaruratKaryawan(int idKaryawan) {
		return karyawanMapper.selectKontakDaruratKaryawan(idKaryawan);
	}

	@Override
	public void addKontakDarurat(KontakDaruratModel kontak) {
		KaryawanBaruModel k = karyawanMapper.getKaryawanBaruById(kontak.getIdKaryawan());
		if(k != null) {
			karyawanMapper.insertKontakDarurat(kontak);
		}
	}

	@Override
	public void updateKontakDarurat(KontakDaruratModel kontak) {
		karyawanMapper.updateKontakDarurat(kontak);
	}

	@Override
	public void deleteKontakDaruratById(Integer idKontak) {
		karyawanMapper.deleteKontakDaruratById(idKontak);
	}

	@Override
	public List<KontrakModel> selectKontrakAll(int idKaryawan){return karyawanMapper.selectKontrakAll(idKaryawan);}

	@Override
	public void insertKontrak(KontrakModel kontrak){karyawanMapper.insertKontrak(kontrak);}

	@Override
	public void updateKontrak (KontrakModel kontrak){karyawanMapper.updateKontrak(kontrak);}

	@Override
	public void deleteKontrak (int id){karyawanMapper.deleteKontrak(id);}

  @Override
	public List<DokumenModel> getAllDokumenKaryawanById(int idKaryawan){
		return karyawanMapper.getAllDokumenKaryawanById(idKaryawan);
	}

	@Override
	public void insertDokumen(int idKaryawan, String fileName){
		karyawanMapper.insertDokumen(idKaryawan, fileName);
	}

	@Override
	public DokumenModel getDokumen(int idDokumen){
		return karyawanMapper.getDokumen(idDokumen);
	}

	@Override
	public void updateKaryawanBaru(KaryawanBaruModel karyawan) {
		karyawanMapper.updateKaryawanBaru(karyawan);
	}

	@Override
	public void deleteDokumen(int idDokumen){
		karyawanMapper.deleteDokumen(idDokumen);
	}

}

