package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.AbsenModel;
import com.pusilkom.hris.model.KaryawanBaruModel;
import com.pusilkom.hris.model.UserWeb;
import com.pusilkom.hris.service.AbsenService;
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
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@ControllerAdvice
public class AbsenController {
    @Autowired
    KaryawanService karyawanService;

    @Autowired
    AbsenService absenService;

    @ModelAttribute("statusCheckIn")
    public boolean isCheckedIn(Model model,
                               @NotNull Authentication auth) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        KaryawanBaruModel karyawan = karyawanService.getKaryawanByUsername(user.getUsername());
        boolean isCheckedIn = absenService.isCheckedIn(karyawan);
        if(isCheckedIn) {
            AbsenModel absen = absenService.getKaryawanLatestCheckIn(karyawan);
            model.addAttribute("absen", absen);
        }
        return isCheckedIn;
    }

    @GetMapping("/employee/riwayat-absen")
    public String absenKaryawan(Model model,
                               @ModelAttribute("tambahDetail") String notifTambahDetail,
                               @NotNull Authentication auth) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        KaryawanBaruModel karyawan = karyawanService.getKaryawanByUsername(user.getUsername());

        log.info("id karyawan " + karyawan.getIdKaryawan());

        List<AbsenModel> absens = absenService.getAbsenKaryawan(karyawan);

        model.addAttribute("notifTambahDetail", notifTambahDetail);
        model.addAttribute("absens", absens);

        return "riwayat-absen";
    }

    @GetMapping("/employee/absen/checkin")
    public String checkIn(RedirectAttributes ra,
                          @NotNull Authentication auth,
                          HttpServletRequest request) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        KaryawanBaruModel karyawan = karyawanService.getKaryawanByUsername(user.getUsername());
        absenService.checkIn(karyawan);

        AbsenModel absen = absenService.getKaryawanLatestCheckIn(karyawan);

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String checkInString = sdfDate.format(absen.getCheckInTime());

        String notification = "Check-in pada " + checkInString + " berhasil dilakukan";
        ra.addFlashAttribute("checkInNotif", notification);
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }

    @PostMapping("employee/absen/checkout")
    public String checkOut(RedirectAttributes ra,
                           @NotNull Authentication auth,
                           @RequestParam(value = "detail") String detail,
                           HttpServletRequest request) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        KaryawanBaruModel karyawan = karyawanService.getKaryawanByUsername(user.getUsername());
        absenService.checkOut(karyawan, detail);

        AbsenModel absen = absenService.getKaryawanLatestCheckIn(karyawan);

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String checkOutString = sdfDate.format(absen.getCheckOutTime());

        String notification = "Check-out pada " + checkOutString + " berhasil dilakukan";
        ra.addFlashAttribute("checkOutNotif", notification);
        String referer = request.getHeader("Referer");
        return "redirect:"+ referer;
    }

    @PostMapping("/employee/absen/tambah-detail")
    public String tambahDetail(RedirectAttributes ra,
                               @RequestParam(value = "detail") String detail,
                               @RequestParam(value = "idAbsen") Integer idAbsen) {
        AbsenModel absen = absenService.getAbsenById(idAbsen);
        absen.setDetail(detail);

        absenService.updateAbsen(absen);

        String notification = "Detail absen pada rentang waktu " + absen.getCheckInTime() + " hingga " + absen.getCheckOutTime() + " berhasil ditambahkan";
        ra.addFlashAttribute("tambahDetail", notification);

        return "redirect:/employee/riwayat-absen";
    }
}
