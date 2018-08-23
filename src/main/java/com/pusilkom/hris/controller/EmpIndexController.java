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
    @PreAuthorize("hasAnyAuthority('GET_HR','GET_MNGDIVISI')")
    public String detailKaryawan(Model model, @PathVariable("idKaryawan") int idKaryawan){
        KaryawanBaruModel karyawanBaru = karyawanService.getKaryawanBaruById(idKaryawan);
        DivisibaruModel divisi = divisiService.selectDivisiBaruByID(karyawanBaru.getIdDivisi());
        DataDiriModel dataDiri = karyawanService.getDataDiriByIdKaryawan(karyawanBaru.getIdKaryawan());
        List<KeluargaModel> keluarga = karyawanService.selectAnggotaKeluargaAll(idKaryawan);
        if(dataDiri == null){
            dataDiri = new DataDiriModel();
            dataDiri.setIdKaryawan(idKaryawan);
        }
        model.addAttribute("karyawan", karyawanBaru);
        model.addAttribute("divisi", divisi);
        model.addAttribute("dataDiri", dataDiri);
        model.addAttribute("keluarga", keluarga);

        System.out.println(keluarga);
        return "detail-karyawan";
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/insert-data-diri")
    public String updateKaryawan(Model model, 
                                @ModelAttribute("dataDiri") DataDiriModel dataDiri,
                                @PathVariable("idKaryawan") int idKaryawan){
        karyawanService.insertDataDiri(dataDiri);
        return "redirect:/employee/detail-karyawan/"+idKaryawan;
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/insert-keluarga")
    public String insertKeluarga(Model model,
                                 @ModelAttribute("keluarga") KeluargaModel keluarga,
                                 @PathVariable("idKaryawan") int idKaryawan){

        keluarga.setIdKaryawan(idKaryawan);
        karyawanService.insertAnggotaKeluarga(keluarga);
        return "redirect:/employee/detail-karyawan/"+idKaryawan;
    }

    @RequestMapping(value = "/employee/detail-karyawan/{idKaryawan}/update-anggota-keluarga/{id}" , method = RequestMethod.POST)
    public String updateAnggotaKeluarga (@ModelAttribute KeluargaModel keluarga, Model model, @PathVariable("idKaryawan") int idKaryawan, @PathVariable(value = "id") int id) {

        karyawanService.updateAnggotaKeluarga(keluarga);

        return "redirect:/employee/detail-karyawan/"+idKaryawan;
    }


    @RequestMapping("/employee/detail-karyawan/hapus/{idKaryawan}/{id}")
    public String deleteAnggotaKeluarga (Model model, @PathVariable("idKaryawan") int idKaryawan, @PathVariable(value = "id") int id)
    {
        karyawanService.deleteAnggotaKeluarga(id);

        return "redirect:/employee/detail-karyawan/"+idKaryawan;
    }


}
