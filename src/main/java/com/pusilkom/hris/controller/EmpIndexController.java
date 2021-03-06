package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.*;
import com.pusilkom.hris.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotNull;
import javax.xml.crypto.Data;

import java.security.Principal;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;

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
        KaryawanBaruModel currentKaryawan = karyawanService.getKaryawanByUsername(user.getUsername());
        model.addAttribute("currentKaryawan", currentKaryawan);
        model.addAttribute("currentUser", user);
        return "emp-index";
    }

    @ModelAttribute("currentKaryawanLogged")
    public KaryawanBaruModel getLoggedKaryawan(Model model, @NotNull Authentication auth){
        UserWeb user = (UserWeb) auth.getPrincipal();
        KaryawanBaruModel currentKaryawan = karyawanService.getKaryawanByUsername(user.getUsername());
        boolean isHr = false;
        boolean isKaryawan = false;
        boolean isDivManager = false;
        return currentKaryawan;
    }

    /**
     * method ini digunakan untuk menampilkan index manajer divisi yang berisi
     * daftar anggota di divisinya empployee
     * 
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
    public String detailKaryawan(Model model, @PathVariable("idKaryawan") int idKaryawan,
            @NotNull Authentication auth) {
        KaryawanBaruModel karyawanBaru = karyawanService.getKaryawanBaruById(idKaryawan);
        DivisibaruModel divisi = divisiService.selectDivisiBaruByID(karyawanBaru.getIdDivisi());

        // get data diri
        DataDiriModel dataDiri = karyawanService.getDataDiriByIdKaryawan(karyawanBaru.getIdKaryawan());

        //get data keluarga
        List<KeluargaModel> keluarga = karyawanService.selectAnggotaKeluargaAll(idKaryawan);
        if (dataDiri == null) {
            dataDiri = new DataDiriModel();
            dataDiri.setIdKaryawan(idKaryawan);
        }
        //get data pendidikan
        List<PendidikanModel> pendidikan = karyawanService.selectPendidikanAll(idKaryawan);

        //get data gaji
        List<RiwayatGajiModel> listRiwayatGaji = karyawanService.selectAllRiwayatGajiById(idKaryawan);

        // check if user can edit
        UserWeb user = (UserWeb) auth.getPrincipal();
        boolean isEmployeeSelected = false;
        boolean isHR = false;
        KaryawanBaruModel karyawanLogged = karyawanService.getKaryawanByUsername(user.getUsername());
        if (karyawanLogged != null && karyawanLogged.getIdKaryawan() == idKaryawan) {
            isEmployeeSelected = true;
        }
        for (String role : user.getStrRoles()) {
            if (role.equals("ROLE_HR")) {
                isHR = true;
            }
        }

        //get benefit karyawan
        List<BenefitKaryawanModel> listBenefit = karyawanService.getBenefitKaryawan(idKaryawan);
        if(listBenefit.size() == 0){
             listBenefit = null;
         }
        List<KontakDaruratModel> dataDarurat = karyawanService.getKontakDaruratKaryawan(idKaryawan);
        List<KontrakModel> kontrak = karyawanService.selectKontrakAll(idKaryawan);

        List<DokumenModel> dokumens = karyawanService.getAllDokumenKaryawanById(idKaryawan);
        List<DivisibaruModel> divisis = divisiService.selectAllDivisiAktif();

        model.addAttribute("isEmployeeSelected", isEmployeeSelected);
        model.addAttribute("isHR", isHR);
        model.addAttribute("karyawan", karyawanBaru);
        model.addAttribute("divisi", divisi);
        model.addAttribute("dataDiri", dataDiri);
        model.addAttribute("listBenefit",listBenefit);
        model.addAttribute("pendidikan", pendidikan);
        model.addAttribute("keluarga", keluarga);
        model.addAttribute("listRiwayatGaji", listRiwayatGaji);
        model.addAttribute("dokumens", dokumens);
        model.addAttribute("darurats", dataDarurat);

        model.addAttribute("kontrak", kontrak);

        model.addAttribute("divisis", divisis);

        return "detail-karyawan";
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/insert-data-diri")
    public String updateKaryawan(Model model, @ModelAttribute("dataDiri") DataDiriModel dataDiri,
            @PathVariable("idKaryawan") int idKaryawan, @NotNull Authentication auth) {
        // check if user can edit
        UserWeb user = (UserWeb) auth.getPrincipal();
        boolean canEdit = false;
        KaryawanBaruModel karyawanLogged = karyawanService.getKaryawanByUsername(user.getUsername());
        if (karyawanLogged != null && karyawanLogged.getIdKaryawan() == idKaryawan) {
            canEdit = true;
        }
        for (String role : user.getStrRoles()) {
            if (role.equals("ROLE_HR") || role.equals("ROLE_MANAJERDIVISI")) {
                canEdit = true;
            }
        }

        if (canEdit) {
            karyawanService.insertDataDiri(dataDiri);
        }
        System.out.println(canEdit);
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/insert-gaji")
    public String insertGaji(Model model, @RequestParam("gaji") int gaji, @PathVariable("idKaryawan") int idKaryawan) {
        karyawanService.insertGaji(idKaryawan, gaji);
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/update-gaji/{idGaji}")
    public String updateGaji(@RequestParam("gaji") int gaji, @PathVariable("idKaryawan") int idKaryawan,
            @PathVariable("idGaji") int idGaji) {
        karyawanService.updateGajiById(idGaji, gaji);
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @GetMapping("/employee/detail-karyawan/{idKaryawan}/delete-gaji/{idGaji}")
    public String deleteGaji(@PathVariable("idKaryawan") int idKaryawan, @PathVariable("idGaji") int idGaji) {
        karyawanService.deleteGajiById(idGaji);
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @GetMapping("employee/detail-karyawan/{idKaryawan}/activate")
    public String activateKaryawan(@PathVariable("idKaryawan") int idKaryawan) {
        karyawanService.activateKaryawan(idKaryawan);
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @GetMapping("employee/detail-karyawan/{idKaryawan}/deactivate")
    public String deActivateKaryawan(@PathVariable("idKaryawan") int idKaryawan) {
        karyawanService.deActivateKaryawan(idKaryawan);
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/insert-keluarga")
    public String insertKeluarga(Model model, @ModelAttribute("keluarga") KeluargaModel keluarga,
            @PathVariable("idKaryawan") int idKaryawan) {

        keluarga.setIdKaryawan(idKaryawan);
        karyawanService.insertAnggotaKeluarga(keluarga);
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @RequestMapping(value = "/employee/detail-karyawan/{idKaryawan}/update-anggota-keluarga/{id}", method = RequestMethod.POST)
    public String updateAnggotaKeluarga(@ModelAttribute KeluargaModel keluarga, Model model,
            @PathVariable("idKaryawan") int idKaryawan, @PathVariable(value = "id") int id) {

        karyawanService.updateAnggotaKeluarga(keluarga);

        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }


    @RequestMapping("/employee/detail-karyawan/hapus-keluarga/{idKaryawan}/{id}")
    public String deleteAnggotaKeluarga (Model model, @PathVariable("idKaryawan") int idKaryawan, @PathVariable(value = "id") int id)
    {
        karyawanService.deleteAnggotaKeluarga(id);

        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/upload")
    public String uploadDokumen(@PathVariable("idKaryawan") int idKaryawan, @RequestParam("file") MultipartFile file,
            RedirectAttributes ra) {
        String dir = System.getProperty("user.dir") + "/uploads";
        Path p = Paths.get(dir, "dokumen-" + idKaryawan + "-" + file.getOriginalFilename());
        karyawanService.insertDokumen(idKaryawan, "dokumen-" + idKaryawan + "-" + file.getOriginalFilename());
        try {
            Files.write(p, file.getBytes());
            ra.addFlashAttribute("test", dir);
        } catch (Exception e) {
            ra.addFlashAttribute("test", e.toString());
            return "redirect:/employee/detail-karyawan/" + idKaryawan;
        }

        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @GetMapping("/employee/detail-karyawan/{idKaryawan}/download/{idFile}")
    public void downloadDokumen(HttpServletRequest request, HttpServletResponse response,
            @PathVariable("idKaryawan") int idKaryawan, @PathVariable("idFile") int idFile) throws IOException {
        String dir = System.getProperty("user.dir") + "/uploads";
        DokumenModel dokumen = karyawanService.getDokumen(idFile);
        File file = new File(dir + "/" + dokumen.getFileName());
        if (file.exists()) {

            String mimeType = URLConnection.guessContentTypeFromName(file.getName());
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);

            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

            response.setContentLength((int) file.length());

            InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

            FileCopyUtils.copy(inputStream, response.getOutputStream());
        }
    }

    @GetMapping("/employee/detail-karyawan/{idKaryawan}/delete-dokumen/{idFile}")
    public String deleteDokumen(@PathVariable("idKaryawan") int idKaryawan, @PathVariable("idFile") int idFile){
        karyawanService.deleteDokumen(idFile);
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/insert-pendidikan")
    public String insertPendidikan(Model model,
                                 @ModelAttribute("pendidikan") PendidikanModel pendidikan,
                                 @PathVariable("idKaryawan") int idKaryawan){

        pendidikan.setIdKaryawan(idKaryawan);
        karyawanService.insertPendidikan(pendidikan);
        return "redirect:/employee/detail-karyawan/"+idKaryawan;
    }

    @RequestMapping("/employee/detail-karyawan/hapus-pendidikan/{idKaryawan}/{id}")
    public String deletePendidikan (Model model, @PathVariable("idKaryawan") int idKaryawan, @PathVariable(value = "id") int id)
    {
        karyawanService.deletePendidikan(id);

        return "redirect:/employee/detail-karyawan/"+idKaryawan;
    }

    @PostMapping(value = "employee/detail-karyawan/{idKaryawan}/tambah-benefit/")
    public String tambahBenefitKaryawan(@PathVariable("idKaryawan") int idKaryawan, @RequestParam(value = "namaBenefitBaru") String namaBenefitBaru,
                                        @RequestParam(value = "keteranganBenefitBaru") String keteranganBenefitBaru){
        System.out.println("masuk sini");
        karyawanService.addBenefitKaryawan(idKaryawan, namaBenefitBaru, keteranganBenefitBaru);
        System.out.println("masuk sana");
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/edit-benefit/{idBenefit}")
    public String editBenefitKaryawan(@RequestParam(value = "keteranganBenefit") String keteranganBenefit, @PathVariable("idKaryawan") int idKaryawan, @PathVariable(value = "idBenefit") int idBenefit){

        karyawanService.updateBenefitKaryawan(idBenefit, keteranganBenefit);
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @GetMapping("/employee/detail-karyawan/{idKaryawan}/delete-benefit/{idBenefit}")
    public String deleteBenefitKaryawan(@PathVariable("idKaryawan") int idKaryawan, @PathVariable(value = "idBenefit") int idBenefit){

        karyawanService.deleteBenefitKaryawan(idBenefit);
        return "redirect:/employee/detail-karyawan/" + idKaryawan;
    }

    @RequestMapping(value = "/employee/detail-karyawan/{idKaryawan}/update-pendidikan/{id}" , method = RequestMethod.POST)
    public String updatePendidikan (@ModelAttribute PendidikanModel pendidikan, Model model, @PathVariable("idKaryawan") int idKaryawan, @PathVariable(value = "id") int id) {

        karyawanService.updatePendidikan(pendidikan);

        return "redirect:/employee/detail-karyawan/"+idKaryawan;
    }

    @PostMapping("/employee/detail-karyawan/{idKaryawan}/insert-kontrak")
    public String insertKontrak(Model model,
                                   @ModelAttribute("kontrak") KontrakModel kontrak,
                                   @PathVariable("idKaryawan") int idKaryawan){

        kontrak.setIdKaryawan(idKaryawan);
        karyawanService.insertKontrak(kontrak);
        return "redirect:/employee/detail-karyawan/"+idKaryawan;
    }

    @RequestMapping(value = "/employee/detail-karyawan/{idKaryawan}/update-kontrak/{id}" , method = RequestMethod.POST)
    public String updateKontrak (@ModelAttribute KontrakModel kontrak, Model model, @PathVariable("idKaryawan") int idKaryawan, @PathVariable(value = "id") int id) {

        karyawanService.updateKontrak(kontrak);

        return "redirect:/employee/detail-karyawan/"+idKaryawan;
    }

    @RequestMapping("/employee/detail-karyawan/hapus-kontrak/{idKaryawan}/{id}")
    public String deleteKontrak (Model model, @PathVariable("idKaryawan") int idKaryawan, @PathVariable(value = "id") int id)
    {
        karyawanService.deleteKontrak(id);

        return "redirect:/employee/detail-karyawan/"+idKaryawan;
    }

}
