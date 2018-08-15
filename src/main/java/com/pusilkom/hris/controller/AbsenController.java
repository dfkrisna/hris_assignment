package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.AbsenModel;
import com.pusilkom.hris.model.KaryawanBaruModel;
import com.pusilkom.hris.model.KaryawanModel;
import com.pusilkom.hris.model.UserWeb;
import com.pusilkom.hris.service.AbsenService;
import com.pusilkom.hris.service.KaryawanService;
import com.sun.org.apache.xpath.internal.operations.Mod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.constraints.NotNull;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

@Slf4j
@Controller
@ControllerAdvice
public class AbsenController {
    @Autowired
    KaryawanService karyawanService;

    @Autowired
    AbsenService absenService;

    @GetMapping("/employee/absen")
    public String homeKaryawan(Model model,
                               @NotNull Authentication auth,
                               @ModelAttribute("checkInNotif") String checkInNotif,
                               @ModelAttribute("checkOutNotif") String checkOutNotif) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        KaryawanBaruModel karyawan = karyawanService.getKaryawanByUsername(user.getUsername());
        boolean isCheckedIn = absenService.isCheckedIn(karyawan);

        if(isCheckedIn) {
            AbsenModel absen = absenService.getKaryawanLatestCheckIn(karyawan);
            model.addAttribute("absen", absen);
        }

        model.addAttribute("isCheckedIn", isCheckedIn);
        model.addAttribute("karyawan", karyawan);
        model.addAttribute("checkinSuccess", checkInNotif);
        model.addAttribute("checkOutSuccess", checkOutNotif);
        return "emp-karyawan";
    }

    @GetMapping("/employee/absen/checkin")
    public String checkIn(RedirectAttributes ra, @NotNull Authentication auth) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        KaryawanBaruModel karyawan = karyawanService.getKaryawanByUsername(user.getUsername());
        absenService.checkIn(karyawan);

        AbsenModel absen = absenService.getKaryawanLatestCheckIn(karyawan);

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String checkInString = sdfDate.format(absen.getCheckInTime());

        String notification = "Check-in pada " + checkInString + " berhasil dilakukan";
        ra.addFlashAttribute("checkInNotif", notification);
        return "redirect:/employee/absen";
    }

    @PostMapping("employee/absen/checkout")
    public String checkOut(RedirectAttributes ra,
                           @NotNull Authentication auth,
                           @RequestParam(value = "detail") String detail) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        KaryawanBaruModel karyawan = karyawanService.getKaryawanByUsername(user.getUsername());
        absenService.checkOut(karyawan, detail);

        AbsenModel absen = absenService.getKaryawanLatestCheckIn(karyawan);

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String checkOutString = sdfDate.format(absen.getCheckOutTime());

        String notification = "Check-out pada " + checkOutString + " berhasil dilakukan";
        ra.addFlashAttribute("checkOutNotif", notification);
        return "redirect:/employee/absen";
    }
}
