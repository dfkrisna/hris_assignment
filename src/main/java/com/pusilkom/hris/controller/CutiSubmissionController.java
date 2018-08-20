package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.KaryawanBaruModel;
import com.pusilkom.hris.model.KaryawanCutiModel;
import com.pusilkom.hris.model.KaryawanModel;
import com.pusilkom.hris.model.UserWeb;
import com.pusilkom.hris.service.KaryawanCutiService;
import com.pusilkom.hris.service.KaryawanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Controller
@ControllerAdvice
public class CutiSubmissionController {
    @Autowired
    KaryawanCutiService karyawanCutiService;

    @Autowired
    KaryawanService karyawanService;

    @GetMapping("/employee/riwayat-cuti")
    public String viewRiwayatCutiKaryawan(Model model,
                                          @NotNull Authentication auth,
                                          @ModelAttribute("submitSuccessNotif") String submitSuccessNotif,
                                          @ModelAttribute("updSuccessNotif") String updSuccessNotif) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        KaryawanBaruModel karyawan = karyawanService.getKaryawanByUsername(user.getUsername());
        List<KaryawanCutiModel> cutis = karyawanCutiService.getHistoryByKaryawanId(karyawan.getIdKaryawan());

        LocalDate now = LocalDate.now();

        model.addAttribute("now", now);
        model.addAttribute("cutis",cutis);
        model.addAttribute("submitSuccessNotif",submitSuccessNotif);
        model.addAttribute("updSuccessNotif",updSuccessNotif);
        return "riwayat-cuti";
    }

    @PostMapping(value = "/employee/ajukan-cuti")
    public String ajukanCuti(RedirectAttributes ra,
                             @NotNull Authentication auth,
                             @RequestParam(value = "jumlahHari", required = false) Integer jumlahHari,
                             @RequestParam(value = "tanggalMulai", required = false) String tanggalMulai,
                             @RequestParam(value = "tanggalSelesai", required = false) String tanggalSelesai) throws ParseException {
        UserWeb user = (UserWeb) auth.getPrincipal();
        KaryawanBaruModel karyawan = karyawanService.getKaryawanByUsername(user.getUsername());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate mulaiDate = LocalDate.parse(tanggalMulai, dtf);
        LocalDate selesaiDate = LocalDate.parse(tanggalSelesai, dtf);

        KaryawanCutiModel cuti = new KaryawanCutiModel();
        cuti.setStartPeriode(mulaiDate);
        cuti.setEndPeriode(selesaiDate);
        cuti.setJumlahHari(jumlahHari);
        cuti.setIdKaryawan(karyawan.getIdKaryawan());

        karyawanCutiService.submitCutiKaryawan(cuti);

        String notif = "Cuti berhasil diajukan";

        ra.addFlashAttribute("submitSuccessNotif", notif);

        return "redirect:/employee/riwayat-cuti";
    }

    @PostMapping(value = "/employee/ubah-cuti")
    public String ubahCuti(RedirectAttributes ra,
                           @NotNull Authentication auth,
                           @RequestParam(value = "jumlahHari", required = false) Integer jumlahHari,
                           @RequestParam(value = "tanggalMulai", required = false) String tanggalMulai,
                           @RequestParam(value = "tanggalSelesai", required = false) String tanggalSelesai,
                           @RequestParam(value = "idKaryawan") Integer idKaryawan,
                           @RequestParam(value = "idCuti") Integer id) throws ParseException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate mulaiDate = LocalDate.parse(tanggalMulai, dtf);
        LocalDate selesaiDate = LocalDate.parse(tanggalSelesai, dtf);

        KaryawanCutiModel cutiBaru = new KaryawanCutiModel();
        cutiBaru.setId(id);
        cutiBaru.setIdKaryawan(idKaryawan);
        cutiBaru.setJumlahHari(jumlahHari);
        cutiBaru.setStartPeriode(mulaiDate);
        cutiBaru.setEndPeriode(selesaiDate);
        cutiBaru.setDisetujui(false);
        cutiBaru.setTolak(false);

        karyawanCutiService.updateCuti(cutiBaru);

        String notif = "Pengajuan cuti berhasil diubah";

        ra.addFlashAttribute("updSuccessNotif", notif);

        return "redirect:/employee/riwayat-cuti";
    }
}
