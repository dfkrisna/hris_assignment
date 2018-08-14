package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.AbsenModel;
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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Slf4j
@Controller
@ControllerAdvice
public class AbsenController {
    @Autowired
    KaryawanService karyawanService;

    @Autowired
    AbsenService absenService;

    @GetMapping("/employee/karyawan")
    public String homeKaryawan(Model model, @NotNull Authentication auth, @ModelAttribute("notification") String notification) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        int id = karyawanService.getKaryawanIdByUsername(user.getUsername());
        KaryawanModel karyawan = karyawanService.getKaryawanById(id);
        model.addAttribute("nama", karyawan.getNama());
        if(notification != null && notification != "") {
            model.addAttribute("checkin-success", "notification");
        }
        return "emp-karyawan";
    }

    @GetMapping("/employee/karyawan/checkin")
    public String checkIn(RedirectAttributes ra, @NotNull Authentication auth) {
        LocalDate now = LocalDate.now();
        UserWeb user = (UserWeb) auth.getPrincipal();
        int id = karyawanService.getKaryawanIdByUsername(user.getUsername());
        KaryawanModel karyawan = karyawanService.getKaryawanById(id);
        absenService.checkIn(karyawan);

        AbsenModel absen = absenService.getKaryawanLatestCheckIn(karyawan);

        String notification = "Check-in pada " + absen.getCheckInTime().toString() + " berhasil dilakukan";
        ra.addFlashAttribute("notification", notification);
        return "redirect:/employee/karyawan";
    }
}
