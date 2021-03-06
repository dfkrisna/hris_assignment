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
import java.util.ArrayList;
import java.util.Map;

@Slf4j
@Controller
@ControllerAdvice
public class IndexController
{
    @Autowired
    ProyekService proyekService;

    @Autowired
    KaryawanProyekService karyawanProyekService;

    @Autowired
    KaryawanService karyawanService;

    @Autowired
    RekapService rekapService;

    @Autowired
    PenggunaService penggunaDAO;

    @Autowired
    RekapMappingService rekapMappingService;

    @Autowired
    UserService userService;

    @Autowired
    PenugasanService penugasanService;

    @Autowired
    DivisiService divisiService;

    @Autowired
    RatingFeedbackService ratingFeedbackService;

    @Autowired
    RoleProyekService roleProyekService;

    @Autowired
    KaryawanCutiService karyawanCutiService;

    /**
     * method untuk mengakses beranda utama
     * @param model
     * @param auth
     * @return
     */
    @GetMapping("/")
    @PreAuthorize("hasAuthority('GET_')")
    public String landingPage  (Model model, @NotNull Authentication auth) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        model.addAttribute("currentUser", user);
        return "landingpage";
    }

    @GetMapping("/assignment")
    @PreAuthorize("hasAuthority('GET_')")
    public String index(Model model, @NotNull Authentication auth) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        model.addAttribute("currentUser", user);
        return "index";
    }

    @GetMapping("/assignment/signin")
    public String login() {
        return "site/login";
    }

    /**
     * method untuk mendapatkan user yang saat ini login
     * @param auth
     * @return
     */
    @ModelAttribute("currentUser")
    public UserWeb getLoggedInUser(@NotNull Authentication auth) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        return user;
    }

    @RequestMapping("/assignment/login")
    public String login (Model model,
                         @RequestParam(value="error", required = false) String error)
    {
        if(error != null) {
            System.out.println(error);
        }
        return "login";
    }

    /**
     * method untuk menampilkan beranda eksekutif yang berisi matriks penugasan
     * @param model
     * @param periode
     * @return
     */
    @GetMapping(value = "/assignment/eksekutif")
    @PreAuthorize("hasAuthority('GET_EKSEKUTIF')")
    public String indexEksekutif (Model model, @RequestParam(value = "periode", required = false) String periode)
    {
        LocalDate periodeDate;
        if(periode != null) {
            String[] split = periode.split(" ");
            periodeDate = LocalDate.of(Integer.parseInt(split[1]), Month.valueOf(split[0].toUpperCase()), 1);
        } else { periodeDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1); }
        List<ProyekModel> proyekList = proyekService.getProyekByPeriode(periodeDate);
        model.addAttribute("next", periodeDate.plusMonths(1));
        model.addAttribute("current", periodeDate);
        model.addAttribute("previous", periodeDate.minusMonths(1));

        if(proyekList.size() > 0) {
            List<KaryawanBaruModel> karyawanList = karyawanService.getKaryawanBaruAll();
            List<KaryawanProyekModel> karyawanProyekList = karyawanProyekService.getKaryawanProyekByPeriode(periodeDate);
            List<RekapModel> rekapList = rekapService.getRekapByPeriode(periodeDate);
            List<KaryawanRekapModel> mapping = rekapMappingService.mapRekap(karyawanList, proyekList, karyawanProyekList, rekapList);
            int totalPerc = rekapMappingService.totalPercentage(mapping);

            int[] AllBebanKerja = new int[6];

            for(int i = 0; i < 6; i++) {
                List<KaryawanProyekModel> karyawanProyekListBB = karyawanProyekService.getKaryawanProyekByPeriode(periodeDate.minusMonths(i));
                List<RekapModel> rekapListBB = rekapService.getRekapByPeriode(periodeDate.minusMonths(i));
                List<KaryawanRekapModel> mappingBB = rekapMappingService.mapRekap(karyawanList, proyekList, karyawanProyekListBB, rekapListBB);
                AllBebanKerja [5-i] = rekapMappingService.totalPercentage(mappingBB);
            }


            int avgNilai = ratingFeedbackService.getAllAverageRating(periodeDate);
            int[] chartValue = rekapMappingService.chartValue(mapping);
            int chartSize = karyawanList.size();

            Map rekapRoleMap = rekapMappingService.mapRoleToRekap(mapping);

            List<RoleProyekModel> roles = roleProyekService.getAllRoleProyek();

            Map roleMap = new HashMap();

            for (RoleProyekModel role:roles) {
                roleMap.put(role.getId(), role.getNamaRole());
            }


            model.addAttribute("rekapRoleMap", rekapRoleMap);
            model.addAttribute("roles", roleMap);
            model.addAttribute("allAvgRating", avgNilai);
            model.addAttribute("proyekList", proyekList);
            model.addAttribute("mapping", mapping);
            model.addAttribute("chartValue", chartValue);
            model.addAttribute("chartSize", chartSize);
            model.addAttribute("totalPercentage", totalPerc);
            model.addAttribute("totalGreen", (int) (255 - (2.55 * totalPerc)));
            model.addAttribute("totalRed", (int) (2.55 * totalPerc));
            model.addAttribute("chartLabel", rekapService.getSixPeriod(periodeDate));
            model.addAttribute("chartValueNilai", ratingFeedbackService.getRecapAllAverageRating(periodeDate));
            model.addAttribute("AllBebanKerja", AllBebanKerja);


            return "index-eksekutif";
        } else {
            return "index-eksekutif-empty";
        }
    }

    /**
     * method untuk menampilkan beranda admin
     * @param model
     * @return
     */
    @GetMapping("/assignment/admin")
    @PreAuthorize("hasAuthority('GET_ADMIN')")
    public String indexAdmin (Model model)
    {
        String dateToday = rekapMappingService.getCurrentDate();
        List<PenggunaModel> penggunas = penggunaDAO.getListPengguna();
        model.addAttribute("penggunas",penggunas);
        model.addAttribute("date_today",dateToday);
        model.addAttribute("page_title","Beranda Admin");

        return "index-admin";
    }

    /**
     * method ini digunakan untuk menampilkan index PMO yang berisi matriks penugasan
     * @param model
     * @param periode
     * @param notification
     * @return
     */
    @GetMapping("/assignment/pmo")
    @PreAuthorize("hasAuthority('GET_PMO')")
    public String indexPMO (Model model,
                            @RequestParam(value = "periode", required = false) String periode,
                            @ModelAttribute("notification") String notification)
    {
        LocalDate periodeNow = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1);

        LocalDate periodeDate;
        if(periode != null) {
            String[] split = periode.split(" ");
            periodeDate = LocalDate.of(Integer.parseInt(split[1]), Month.valueOf(split[0].toUpperCase()), 1);
            if(periodeDate.isAfter(periodeNow)){
                periodeDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1);
            }
            LocalDate prevPeriode = periodeDate.minusMonths(1);
            rekapService.populatePrevRekap(prevPeriode, periodeDate);
        } else { periodeDate = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1); }

        List<KaryawanProyekModel> karyawanProyekList = karyawanProyekService.getKaryawanProyekByPeriode(periodeDate);
        List<ProyekModel> proyekList = proyekService.getProyekByPeriode(periodeDate);
        List<KaryawanBaruModel> karyawanList = karyawanService.getKaryawanAll();
        List<RekapModel> rekapList = rekapService.getRekapByPeriode(periodeDate);
        List<KaryawanRekapModel> mapping = rekapMappingService.mapRekap(karyawanList, proyekList, karyawanProyekList, rekapList);
        int totalPerc = rekapMappingService.totalPercentage(mapping);
        int[] chartValue = rekapMappingService.chartValue(mapping);
        int chartSize = karyawanList.size();

        Map rekapRoleMap = rekapMappingService.mapRoleToRekap(mapping);

        String dateToday = rekapMappingService.getCurrentDate();

        if(notification != null) {
            model.addAttribute("notification", notification);
        }

        List<RoleProyekModel> roles = roleProyekService.getAllRoleProyek();

        Map roleMap = new HashMap();

        for (RoleProyekModel role:roles) {
            roleMap.put(role.getId(), role.getNamaRole());
        }

        LocalDate next = periodeDate.plusMonths(1);

        if(periodeNow.plusMonths(1).isAfter(next)){
            model.addAttribute("next", periodeDate.plusMonths(1));
        }

        model.addAttribute("rekapRoleMap", rekapRoleMap);
        model.addAttribute("roles", roleMap);
        model.addAttribute("date_today", dateToday);
        model.addAttribute("proyekList", proyekList);
        model.addAttribute("mapping", mapping);
        model.addAttribute("chartValue", chartValue);
        model.addAttribute("chartSize", chartSize);
        model.addAttribute("totalPercentage", totalPerc);
        model.addAttribute("totalGreen", (int) (255 - (2.55*totalPerc)));
        model.addAttribute("totalRed", (int) (2.55*totalPerc));
        model.addAttribute("current", periodeDate);
        model.addAttribute("previous", periodeDate.minusMonths(1));
        model.addAttribute("periodeNow", periodeNow);
        model.addAttribute("invalidMonth", next);
        return "index-pmo";
    }

    /**
     * method ini digunakan untuk menampilkan index manajer divisi yang berisi daftar anggota di divisinya
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("/assignment/mngdivisi")
    @PreAuthorize("hasAuthority('GET_MNGDIVISI')")
    public String indexManajerDivisi(Model model, Principal principal) {
        KaryawanBaruModel pengguna = karyawanService.getKaryawanByUsername(principal.getName());

        DivisibaruModel divisi = divisiService.selectDivisiByManajer(pengguna.getIdKaryawan());
        LocalDate periodeDate = LocalDate.now();

        List<KaryawanBaruModel> karyawanList;
        if(divisi == null){
            karyawanList = new ArrayList<KaryawanBaruModel>();
        }else{
            model.addAttribute("divisi", divisi);
            karyawanList = karyawanService.getKaryawanByDivisi(divisi.getId());
        }
        
        Map mapKaryawanRating = new HashMap();

        for(KaryawanBaruModel karyawan:karyawanList) {
            List<PenugasanModel> penugasanList = penugasanService.getPenugasanList(karyawan.getIdKaryawan());
            int ratingKaryawan = ratingFeedbackService.getAvgRatingKaryawan(karyawan.getIdKaryawan());
//            int ratingKaryawan = penugasanService.getAverageRating(penugasanList);
            mapKaryawanRating.put(karyawan.getIdKaryawan(), ratingKaryawan);
            System.out.println("size penugasan = " + penugasanList.size() + "rating karyawan = " + ratingKaryawan);
        }

        String dateToday = rekapMappingService.getCurrentDate();

        model.addAttribute("date_today", dateToday);
        model.addAttribute("listKaryawan", karyawanList);
        model.addAttribute("mapping", mapKaryawanRating);
        return "index-manajerdivisi";
    }

}
