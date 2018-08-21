package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.AbsenModel;
import com.pusilkom.hris.model.KaryawanBaruModel;
import com.pusilkom.hris.service.AbsenService;
import com.pusilkom.hris.service.KaryawanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class HRController {

    @Autowired
    KaryawanService karyawanService;

    @Autowired
    AbsenService absenService;

    /**
     * method untuk menampilkan beranda hr yang berisi daftar karyawan
     * @param model
     * @return
     */
    @GetMapping("/employee/hr")
    public String indexhr(Model model){
        List<KaryawanBaruModel> karyawanList = karyawanService.getKaryawanBaruAll();
        List<Integer> listDivisi = karyawanService.getAllDivisi();
        model.addAttribute("karyawanList",karyawanList);
        model.addAttribute("listDivisi", listDivisi);
        return "index-hr";
    }

    /**
     * menthod untuk menambah karyawan
     * @param model
     * @param redirectAttributes
     */
    @PostMapping("/employee/hr/tambah-karyawan")
    public String tambahKaryawan(Model model, @RequestParam(value = "nama-lengkap") String namaLengkap, @RequestParam(value = "nama-panggilan") String namaPanggilan,
                                @RequestParam(value = "nip") String nip, @RequestParam(value = "id-divisi") String idDivisi,
                                 @RequestParam(value = "email-pusilkom") String emailPusilkom, @RequestParam(value = "email-pribadi") String emailPribadi){

        karyawanService.addKaryawan(namaLengkap, namaPanggilan, nip, Integer.parseInt(idDivisi), emailPusilkom, emailPribadi);

        return "redirect:/employee/hr";
    }

    @GetMapping("/employee/hr/hapus/{idKaryawan}")
    public String hapusKaryawan(RedirectAttributes ra, @PathVariable(value = "idKaryawan") int idKaryawan){
        System.out.println("masuk controller");
        log.info("masuk controller");
        if(karyawanService.cekKaryawanIsManager(idKaryawan) == null){
            karyawanService.deleteKaryawan(idKaryawan);
            ra.addFlashAttribute("notification", "Berhasil menghapus karyawan");
        } else {
            ra.addFlashAttribute("notificationGagal", "Gagal menghapus, karyawan adalah manager");
        }
        return "redirect:/employee/hr";
    }

    @GetMapping("/employee/hr/rekap-absen")
    public String showRiwayatAbsen(Model model,
                                   @RequestParam(value = "periode", required = false) String periode,
                                   @RequestParam(value = "idKaryawan", required = false) Integer idKaryawan) {

        LocalDate periodeNow = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1);

        LocalDate periodeDate;
        if(periode != null) {
            String[] split = periode.split(" ");
            periodeDate = LocalDate.of(Integer.parseInt(split[1]), Month.valueOf(split[0].toUpperCase()), 1);
            if(periodeDate.isAfter(periodeNow)){
                periodeDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1);
            }
            LocalDate prevPeriode = periodeDate.minusMonths(1);
        } else { periodeDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1); }

        LocalDate next = periodeDate.plusMonths(1);

        if(periodeNow.plusMonths(1).isAfter(next)){
            model.addAttribute("next", periodeDate.plusMonths(1));
        }

        model.addAttribute("current", periodeDate);
        model.addAttribute("previous", periodeDate.minusMonths(1));
        model.addAttribute("periodeNow", periodeNow);
        model.addAttribute("invalidMonth", next);

        List<AbsenModel> absens = null;

        if(idKaryawan != null) {
            KaryawanBaruModel k = karyawanService.getKaryawanBaruById(idKaryawan);
            absens = absenService.getAbsenKaryawan(k);

            model.addAttribute("karyawan", k);
            model.addAttribute("absens", absens);
        } else {
            absens = absenService.getAbsenByPeriode(periodeDate);

            Map absenKaryawan = absenService.mapAbsenKaryawan(absens);

            model.addAttribute("absens", absens);
            model.addAttribute("absenKaryawan", absenKaryawan);
        }

        Map mapDurasi = absenService.mapDurasiAbsen(absens);
        model.addAttribute("mapDurasi", mapDurasi);

        List<KaryawanBaruModel> karyawans = karyawanService.getKaryawanBaruAll();
        model.addAttribute("karyawans", karyawans);

        return "hr-rekap-absen";
    }
}
