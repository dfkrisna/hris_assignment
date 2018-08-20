package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.*;
import com.pusilkom.hris.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Controller
@ControllerAdvice
public class EmpIndexController {
    @Autowired
    KaryawanCutiService karyawanCutiService;

    @Autowired
    KaryawanService karyawanService;

    @Autowired
    DivisiService divisiService;
    
    @GetMapping("/employee")
    public String indexMoka(Model model, @NotNull Authentication auth) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        model.addAttribute("currentUser", user);
        return "emp-index";
    }

    /**
     * method ini digunakan untuk menampilkan index manajer divisi yang berisi daftar anggota di divisinya empployee 
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("/employee/cuti")
    @PreAuthorize("hasAuthority('GET_MNGDIVISI')")
    public String indexManajerDivisiEmployee(Model model, Principal principal) {
        List<KaryawanCutiModel> listOfKaryawanCuti = karyawanCutiService.getAll(principal.getName());
        
        model.addAttribute("listOfKaryawanCuti", listOfKaryawanCuti);
        return "index-manajerdivisi-employee";
    }

    /**
     * Method ini untuk melihat detail karyawan
     */
    @GetMapping("/employee/detail-karyawan/{idKaryawan}")
    @PreAuthorize("hasAnyAuthority('GET_HR','GET_MNGDIVISI')")
    public String detailKaryawan(Model model, @PathVariable("idKaryawan") int idKaryawan){
        KaryawanBaruModel karyawanBaru = karyawanService.getKaryawanBaruById(idKaryawan);
        DivisibaruModel divisi = divisiService.selectDivisiBaruByID(karyawanBaru.getIdDivisi());

        model.addAttribute("karyawan", karyawanBaru);
        model.addAttribute("divisi", divisi);
        return "detail-karyawan";
    }

}
