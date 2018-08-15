package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.*;
import com.pusilkom.hris.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ManajerDivisiController {

    @Autowired
    KaryawanService karyawanService;

    @Autowired
    DivisiService divisiService;

    @Autowired
    RekapMappingService rekapMappingService;

    @Autowired
    ProyekService proyekService;

    @Autowired
    RekapService rekapService;

    @Autowired
    RatingFeedbackService ratingFeedbackService;

    @Autowired
    KaryawanProyekService karyawanProyekService;

    @Autowired
    RoleProyekService roleProyekService;

    @Autowired
    KaryawanCutiService karyawanCutiService;

    /**
     * method ini digunakan untuk menampilkan detail karyawan yang merupakan bawahan manajer divisi
     * @param model
     * @param periode
     * @param idKaryawan
     * @param notification
     * @return
     */
    @GetMapping(value="/assignment/mngdivisi/rekap/{idKar}")
    @PreAuthorize("hasAuthority('GET_MNGDIVISI')")
    public String showDetailKaryawan(Model model,
                                     RedirectAttributes ra,
                                     @RequestParam(value = "periode", required = false) String periode,
                                     @PathVariable(value = "idKar") int idKaryawan,
                                     @ModelAttribute("notification") String notification,
                                     @ModelAttribute("warning") String warning) {
        LocalDate periodeDate;
        if(periode != null) {
            String[] split = periode.split(" ");
            periodeDate = LocalDate.of(Integer.parseInt(split[1]), Month.valueOf(split[0].toUpperCase()), 1);
        } else { periodeDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1); }

        LocalDate periodeNow = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonth(), 1);

        if(periodeDate.compareTo(periodeNow) > 0) {
            warning = "Periode " + periodeDate.getMonth() + " " + periodeDate.getYear() + " belum berjalan";

            ra.addFlashAttribute("warning", warning);
            return "redirect:/assignment/mngdivisi/rekap/" + idKaryawan;
        }

        KaryawanModel karyawan = karyawanService.getKaryawanById(idKaryawan);

        DivisiModel divisi = divisiService.getDivisiByID(karyawan.getIdDivisi());

        KaryawanRekapModel karyawanRekap = rekapMappingService.getRekapBulananKaryawan(periodeDate, idKaryawan);

        Map mapRekapProyek = new HashMap();
        Map karyawanProyekRole = new HashMap();

        List<RekapModel> rekapList = karyawanRekap.getRekapList();

        if(!rekapList.isEmpty()) {
            for(RekapModel rekap : rekapList) {
                ProyekModel proyek = proyekService.getProyekById(rekap.getIdProyek());
                mapRekapProyek.put(rekap.getId(), proyek);

                String roleName =  roleProyekService.getRoleNameByID(karyawanProyekService.getKaryawanProyekById(rekap.getIdKaryawanProyek()).getIdRole());
                karyawanProyekRole.put(rekap.getId(), roleName);
            }
        }

        if(notification != null) {
            model.addAttribute("notification", notification);
        }

        boolean isNow = false;
        if(periodeDate.getYear() == periodeNow.getYear() && periodeDate.getMonth().equals(periodeNow.getMonth())) {
            isNow = true;
        }

        int persentaseKontribusi = (int) (rekapService.getKaryawanKontribusi(karyawan.getId(), LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1)) * 100);
        int ratingKaryawan = ratingFeedbackService.getAvgRatingKaryawan(karyawan.getId());

        model.addAttribute("isNow", isNow);

        model.addAttribute("nilai", ratingKaryawan);
        model.addAttribute("persenKontribusi", persentaseKontribusi);
        model.addAttribute("role", karyawanProyekRole);
        model.addAttribute("isEmpty", rekapList.isEmpty());
        model.addAttribute("karyawanRekap", karyawanRekap);
        model.addAttribute("mapRekapProyek", mapRekapProyek);
        model.addAttribute("divisi", divisi);
        model.addAttribute("next", periodeDate.plusMonths(1));
        model.addAttribute("current", periodeDate);
        model.addAttribute("previous", periodeDate.minusMonths(1));

        //ambil dulu id karpro
        List<KaryawanProyekModel> dicari = karyawanProyekService.selectKaryawanProyekByKaryawanPeriode(idKaryawan,periodeDate);
        boolean isEmptyRF = false;
        if(dicari.isEmpty()) {
            isEmptyRF = true;
        } else {

            List<RatingFeedbackModel> listRF=null;

            for(KaryawanProyekModel karpro : dicari) {
                listRF = ratingFeedbackService.selectRatingFeedbackPer(karpro.getId(), periodeDate);
            }

            if (listRF.isEmpty() || listRF==null) {
                isEmptyRF = true;
            }

            model.addAttribute("listRF", listRF);
        }
        model.addAttribute("isEmptyRF", isEmptyRF);
        return "mngdivisi-penilaian";
    }

    /**
     * method ini digunakan untuk memproses finalisasi evaluasi diri anggota divisi
     * @param model
     * @param ra
     * @param periode
     * @param idKarProy
     * @return
     */
    @PostMapping(value = "/assignment/mngdivisi/rekap/finalisasi")
    @PreAuthorize("hasAuthority('POST_MNGDIVISI_REKAP_FINALISASI')")
    public String finalisasi(Model model,
                             RedirectAttributes ra,
                             @RequestParam(value = "periode") String periode,
                             @RequestParam(value = "idKarProy") int idKarProy) {
        LocalDate periodeDate = LocalDate.parse(periode, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        KaryawanProyekModel karyawanProyek = karyawanProyekService.getKaryawanProyekById(idKarProy);

        RekapModel rekap = rekapService.getRekapBulanan(idKarProy, periodeDate);
        rekap.setApproved(true);
        rekapService.updateRekap(rekap);

        String notification = "Evaluasi mandiri berhasil difinalisasi";

        ra.addFlashAttribute("notification", notification);

        System.out.println("nyampe sini lho");

        return "redirect:/assignment/mngdivisi/rekap/" + karyawanProyek.getIdKaryawan();
    }

    @GetMapping(value= "/employee/detail-karyawan/{idKaryawan}")
    public String showCutiKaryawan(Model model, 
                                   @PathVariable(value = "idKaryawan") int idKaryawan){
        List<KaryawanCutiModel> listOfRiwayatCuti = karyawanCutiService.getHistoryByKaryawanId(idKaryawan);
        KaryawanBaruModel karyawanBaru = karyawanService.getKaryawanBaruById(idKaryawan);
        model.addAttribute("listOfKaryawanCuti", listOfRiwayatCuti);
        model.addAttribute("namaLengkap", karyawanBaru.getNamaLengkap());
        return "detail-cuti";
    }

    @GetMapping(value= "/employee/detail-karyawan/{idKaryawan}/approve/{idKaryawanCuti}")
    public String approveCutiKaryawan(RedirectAttributes ra,
                                    @PathVariable(value = "idKaryawan") int idKaryawan, 
                                    @PathVariable(value = "idKaryawanCuti") int idKaryawanCuti){
        KaryawanBaruModel karyawanBaru = karyawanService.getKaryawanBaruById(idKaryawan);
        if(karyawanCutiService.approve(idKaryawanCuti)){
            ra.addFlashAttribute("notification", "Berhasil menyetujui permohonan cuti " + karyawanBaru.getNamaLengkap());    
        }else{
            ra.addFlashAttribute("notification", "Tidak berhasil, karena sudah ditolak ");    
        }

        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @GetMapping(value= "/employee/detail-karyawan/{idKaryawan}/cancel/{idKaryawanCuti}")
    public String cancelCutiKaryawan(RedirectAttributes ra,
                                    @PathVariable(value = "idKaryawan") int idKaryawan, 
                                    @PathVariable(value = "idKaryawanCuti") int idKaryawanCuti){
        KaryawanBaruModel karyawanBaru = karyawanService.getKaryawanBaruById(idKaryawan);
        karyawanCutiService.cancel(idKaryawanCuti);

        ra.addFlashAttribute("notification", "Berhasil membatalkan permohonan cuti " + karyawanBaru.getNamaLengkap());
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @GetMapping(value= "/employee/detail-karyawan/{idKaryawan}/tolak/{idKaryawanCuti}")
    public String tolak(RedirectAttributes ra,
                                    @PathVariable(value = "idKaryawan") int idKaryawan, 
                                    @PathVariable(value = "idKaryawanCuti") int idKaryawanCuti){
        KaryawanBaruModel karyawanBaru = karyawanService.getKaryawanBaruById(idKaryawan);
        karyawanCutiService.tolak(idKaryawanCuti);

        ra.addFlashAttribute("notification", "Berhasil menolak permohonan cuti " + karyawanBaru.getNamaLengkap());
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @GetMapping(value= "/employee/detail-karyawan/{idKaryawan}/cancel-tolak/{idKaryawanCuti}")
    public String cancelTolak(RedirectAttributes ra,
                                    @PathVariable(value = "idKaryawan") int idKaryawan, 
                                    @PathVariable(value = "idKaryawanCuti") int idKaryawanCuti){
        KaryawanBaruModel karyawanBaru = karyawanService.getKaryawanBaruById(idKaryawan);
        karyawanCutiService.cancelTolak(idKaryawanCuti);

        ra.addFlashAttribute("notification", "Berhasil membatalkan penolakan cuti " + karyawanBaru.getNamaLengkap());
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

}
