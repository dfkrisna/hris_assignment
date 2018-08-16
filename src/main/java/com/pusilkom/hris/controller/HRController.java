package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.KaryawanBaruModel;
import com.pusilkom.hris.service.KaryawanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
public class HRController {

    @Autowired
    KaryawanService karyawanService;

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
}
