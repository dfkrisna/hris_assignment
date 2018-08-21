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
import javax.xml.crypto.Data;

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
    public String detailKaryawan(Model model, @PathVariable("idKaryawan") int idKaryawan, @NotNull Authentication auth){
        KaryawanBaruModel karyawanBaru = karyawanService.getKaryawanBaruById(idKaryawan);
        DivisibaruModel divisi = divisiService.selectDivisiBaruByID(karyawanBaru.getIdDivisi());
        
        //get data diri
        DataDiriModel dataDiri = karyawanService.getDataDiriByIdKaryawan(karyawanBaru.getIdKaryawan());
        if(dataDiri == null){
            dataDiri = new DataDiriModel();
            dataDiri.setIdKaryawan(idKaryawan);
        }

        //get data gaji
        List<RiwayatGajiModel> listRiwayatGaji = karyawanService.selectAllRiwayatGajiById(idKaryawan);
        
        // check if user can edit
        UserWeb user = (UserWeb) auth.getPrincipal();
        boolean isEmployeeSelected = false;
        boolean isHR = false;
        KaryawanBaruModel karyawan = karyawanService.getKaryawanByUsername(user.getUsername());
        if(karyawan != null && karyawan.getIdKaryawan() == idKaryawan){
            isEmployeeSelected = true;
        }
        for (String role : user.getStrRoles()) {
            if(role.equals("ROLE_HR") || role.equals("ROLE_MANAJERDIVISI")){
                isHR = true;                
            }
        }

        model.addAttribute("isEmployeeSelected", isEmployeeSelected);
        model.addAttribute("isHR", isHR);
        model.addAttribute("karyawan", karyawanBaru);
        model.addAttribute("divisi", divisi);
        model.addAttribute("dataDiri", dataDiri);
        model.addAttribute("listRiwayatGaji", listRiwayatGaji);
        return "detail-karyawan";
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/insert-data-diri")
    public String updateKaryawan(Model model, 
                                @ModelAttribute("dataDiri") DataDiriModel dataDiri,
                                @PathVariable("idKaryawan") int idKaryawan,
                                @NotNull Authentication auth){
        //check if user can edit
        UserWeb user = (UserWeb) auth.getPrincipal();
        boolean canEdit = false;
        KaryawanBaruModel karyawan = karyawanService.getKaryawanByUsername(user.getUsername());
        if(karyawan != null && karyawan.getIdKaryawan() == idKaryawan){
            canEdit = true;
        }
        for (String role : user.getStrRoles()) {
            if(role.equals("ROLE_HR") || role.equals("ROLE_MANAJERDIVISI")){
                canEdit = true;
            }
        }

        if(canEdit){
            karyawanService.insertDataDiri(dataDiri);
        }
        System.out.println(canEdit);
        return "redirect:/employee/detail-karyawan/"+idKaryawan;
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/insert-gaji")
    public String insertGaji(Model model, @RequestParam("gaji") int gaji, @PathVariable("idKaryawan") int idKaryawan){
        karyawanService.insertGaji(idKaryawan, gaji);
        return "redirect:/employee/detail-karyawan/"+idKaryawan;
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/update-gaji/{idGaji}")
    public String updateGaji(@RequestParam("gaji") int gaji, @PathVariable("idKaryawan") int idKaryawan, @PathVariable("idGaji") int idGaji){
        karyawanService.updateGajiById(idGaji, gaji);
        return "redirect:/employee/detail-karyawan/"+idKaryawan; 
    }

}
