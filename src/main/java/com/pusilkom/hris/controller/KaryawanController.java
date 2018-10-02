package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.*;
import com.pusilkom.hris.model.FeedbackRatingModel;
import com.pusilkom.hris.model.KaryawanBaruModel;
import com.pusilkom.hris.model.PenugasanModel;
import com.pusilkom.hris.model.RekapModel;
import com.pusilkom.hris.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class KaryawanController {
    @Autowired
    ProyekService proyekService;

    @Autowired
    KaryawanProyekService karyawanProyekService;

    @Autowired
    KaryawanService karyawanService;

    @Autowired
    RekapService rekapService;

    @Autowired
    PenugasanService penugasanService;

    @Autowired
    RekapMappingService rekapMappingService;

    @Autowired
    DivisiService divisiService;

    @Autowired
    RatingFeedbackService ratingFeedbackService;

    /**
     * method ini berfungsi untuk menampilkan beranda karyawan yang berisi penugasan pada periode ini
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("/assignment/karyawan")
    @PreAuthorize("hasAuthority('GET_KARYAWAN')")
    public String indexKaryawan(Model model, Principal principal) {
        KaryawanBaruModel karyawan = karyawanService.selectKaryawanByEmail(principal.getName());
        KaryawanBaruModel karyawanLogin = karyawanService.getKaryawanById(karyawan.getIdKaryawan());
        //get periode saat ini dan mengecek penugasan pada periode tersebut
        LocalDate periode = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1);
        List<PenugasanModel> penugasanPeriodeIni = penugasanService.getPenugasanAktifPeriodeIni(karyawan.getIdKaryawan(), periode);
      
        List<PenugasanModel> penugasanList = penugasanService.getPenugasanList(karyawan.getIdKaryawan());

        int ratingKaryawan = ratingFeedbackService.getAvgRatingKaryawan(karyawan.getIdKaryawan());
        int persentaseKontribusi = (int) (rekapService.getKaryawanKontribusi(karyawan.getIdKaryawan(), LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1)) * 100);


        int bebanKerjaSaatini = 0;

        for(int i =0; i < penugasanPeriodeIni.size(); i++){
            bebanKerjaSaatini+= penugasanPeriodeIni.get(i).getPersentase();
        }

        int rataRataBeban;
        
        if(penugasanPeriodeIni.size() == 0){
            rataRataBeban = 0;
        }else{
            rataRataBeban = bebanKerjaSaatini/penugasanPeriodeIni.size();
        }
        
        

        model.addAttribute("rataRataBeban", rataRataBeban);
        model.addAttribute("ratingKaryawan", ratingKaryawan);
        model.addAttribute("persentaseKontribusi", persentaseKontribusi);
        model.addAttribute("penugasanList", penugasanList);
        model.addAttribute("bebanKerjaSaatini", bebanKerjaSaatini);


        String dateToday = rekapMappingService.getCurrentDate();

        model.addAttribute("date_today", dateToday);

        if(penugasanPeriodeIni != null){
            //pass penugasan periode ini ke view
            model.addAttribute("page_title","Beranda Karyawan");
            model.addAttribute("penugasanPeriodeIni", penugasanPeriodeIni);
        }
        else{
            String notification = "Tidak ada penugasan pada periode ini.";
            model.addAttribute("notification", notification);
        }

        log.info(" " + karyawanLogin.getNamaLengkap());
        log.info(" " + karyawan.getIdKaryawan());
        log.info(" " + karyawanLogin.getIdKaryawan());
        model.addAttribute("karyawanLogin", karyawanLogin);
        return "index-karyawan";
    }

    /**
     * method ini digunakan untuk menampilkan seluruh penugasan karyawan yang sedang login
     * @param model
     * @param principal
     * @return
     */
    @GetMapping("/assignment/karyawan/penugasan")
    @PreAuthorize("hasAuthority('GET_KARYAWAN')")
    public String penugasanKaryawan(Model model, Principal principal) {
        KaryawanBaruModel karyawan = karyawanService.selectKaryawanByEmail(principal.getName());

        //select seluruh penugasan karyawan pada seluruh periode
        List<PenugasanModel> penugasanKaryawan = penugasanService.assignLatestStatus(penugasanService.getRiwayatPenugasanKaryawan(karyawan.getIdKaryawan()));

        if(penugasanKaryawan != null){
            //pass penugasan ke view
            model.addAttribute("page_title","Beranda Karyawan");
            model.addAttribute("penugasanKaryawan", penugasanKaryawan);
        }
        else{
            String notification = "Tidak ada penugasan.";
            model.addAttribute("notification", notification);
        }

        String dateToday = rekapMappingService.getCurrentDate();
        model.addAttribute("date_today", dateToday);
        model.addAttribute("page_title","Riwayat Penugasan");
        model.addAttribute("penugasanKaryawan", penugasanKaryawan);

        return "penugasan-karyawan";
    }

    /**
     * method ini digunakan untuk menampilka detail penugasan proyek
     * @param model
     * @param idProyek
     * @param principal
     * @return
     */
    @GetMapping(value ="/assignment/karyawan/penugasan/detail/{idProyek}")
    @PreAuthorize("hasAuthority('GET_KARYAWAN')")
    public String detailpenugasanKaryawan(Model model, HttpSession session, @PathVariable Integer idProyek,
                                          Principal principal, @RequestParam(value = "periode", required = false) String periode) {
        //select karyawan, lalu cek detail penugasan dan rekap evaluasi diri
        KaryawanBaruModel karyawan = karyawanService.selectKaryawanByEmail(principal.getName());
        PenugasanModel detailPenugasan = penugasanService.getDetailPenugasanById(idProyek, karyawan.getIdKaryawan());
        List<RekapModel> rekapPenilaianMandiri = rekapService.selectRekapByIdKaryawanProyek(detailPenugasan.getIdKaryawanProyek());

        //pass object ke view
        String dateToday = rekapMappingService.getCurrentDate();
        model.addAttribute("date_today", dateToday);
        model.addAttribute("page_title","Detail Penugasan");
        model.addAttribute("detailPenugasan", detailPenugasan);
        model.addAttribute("rekapPenilaianMandiri", rekapPenilaianMandiri);
        System.out.println(detailPenugasan.getIdProyek());
        LocalDate periodeNow = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1);

        //Melakukan handling periode yang akan ditampilkan pada halaman rekan seproyek
        if(periode != null) {
            String[] split = periode.split(" ");
            int year = Integer.parseInt(split[1]);
            String monthString = split[0].toUpperCase();
            LocalDate periodeCompare = LocalDate.of(year, Month.valueOf(split[0].toUpperCase()),1);
            if(periodeCompare.equals(periodeNow)) {
                model.addAttribute("offPeriode", "offPeriode");
                session.setAttribute("periodeSelected", periodeNow);
            }else{
                if(periodeCompare.equals(periodeNow.plusMonths(1))) {
                    session.setAttribute("periodeSelected", periodeNow);
                    model.addAttribute("periodeOut", "Pengisian Feedback dan Penilaian Belum Dimulai atau Sudah Ditutup");
                }else {
                    periodeNow = periodeCompare;
                    session.setAttribute("periodeSelected", periodeNow);
                    model.addAttribute("periodeOut", "Pengisian Feedback dan Penilaian Belum Dimulai atau Sudah Ditutup");
                }
            }
        }else {
            model.addAttribute("offPeriode", "offPeriode");
            session.setAttribute("periodeSelected", periodeNow);
        }

        int userId = karyawan.getIdKaryawan();

        String penugasan = karyawanService.cekPenugasanKaryawanById(userId);

        //kondisional jika karyawan yang sedang login punya penugasan atau tidak punya penugasan
        if(penugasan.equalsIgnoreCase("yes")){

            List<Integer> proyekSekarang = karyawanService.getUserProyek(userId);

            List<FeedbackRatingModel> rekanWithFeedback =
                    karyawanService.getRekanSeproyekFeedback(proyekSekarang, userId, periodeNow);

            List<FeedbackRatingModel> rekanNoFeedback =
                    karyawanService.getRekanSeproyek(proyekSekarang, userId, periodeNow);

            model.addAttribute("rekanWithFeedBack", rekanWithFeedback);
            model.addAttribute("rekanWithNoFeedBack", rekanNoFeedback);

            if(rekanWithFeedback.isEmpty() && rekanNoFeedback.isEmpty()) {
                model.addAttribute("notification","Tidak ada rekan seproyek di periode ini");
            }else if(rekanWithFeedback.isEmpty()) {
                model.addAttribute("rekans",rekanNoFeedback);
                model.addAttribute("noFeedback","noFeedback");
            }else if(rekanNoFeedback.isEmpty()){
                model.addAttribute("rekans",rekanWithFeedback);
            }else {
                model.addAttribute("rekans", rekanWithFeedback);
                model.addAttribute("rekansNoFeedback", rekanNoFeedback);
            }

            System.out.println();
            for(FeedbackRatingModel rekan:rekanNoFeedback){
                System.out.println("nama rekan: " + rekan.getNamaRekan());
                System.out.println("nama project: " + rekan.getKodeProyek());
                System.out.println("feed back: " + rekan.getFeedback() );
                System.out.println("=================================");
            }

            System.out.println("<==========================================================>");

            for(FeedbackRatingModel rekan:rekanWithFeedback){
                System.out.println("nama rekan: " + rekan.getNamaRekan());
                System.out.println("nama project: " + rekan.getKodeProyek());
                System.out.println("feed back: " + rekan.getFeedback() );
                System.out.println("=================================");
            }

            model.addAttribute("prevPeriode", periodeNow.minusMonths(1));
            model.addAttribute("periodeNow", periodeNow);
            model.addAttribute("nextPeriode", periodeNow.plusMonths(1));
            model.addAttribute("date_today", dateToday);

        }else {
            model.addAttribute("noPenugasan", "noPenugasan");
            model.addAttribute("date_today", dateToday);
        }

        return "detail-proyek";
    }

    /**
     * method ini digunakan untuk menambahkan evaluasi diri karyawan
     * @param model
     * @param session
     * @param idProyek
     * @param id
     * @return
     */
    @GetMapping(value="/assignment/karyawan/penilaian-mandiri/tambah/{idProyek}/{id}")
    @PreAuthorize("hasAuthority('GET_KARYAWAN')")
    public String tambahPenilaianMandiri (Model model, HttpSession session,@PathVariable Integer idProyek, @PathVariable Integer id){
        //select rekap bulanan milik karyawan
        RekapModel rekap = rekapService.selectRekapById(id);
        //set deadline pengisian
        LocalDate deadlinePengisian = rekap.getPeriode().plusMonths(1);

        //pass ke view
        model.addAttribute("deadlinePengisian", deadlinePengisian);
        model.addAttribute("rekap", rekap);

        return "form-add-penilaianmandiri";
    }

    /**
     * method ini digunakan untuk submit evaluasi diri yang dimasukkan karyawan
     * @param model
     * @param session
     * @param rekap
     * @param idProyek
     * @param id
     * @param redirectAttributes
     * @return
     */
    @PostMapping(value="/assignment/karyawan/penilaian-mandiri/tambah/{idProyek}/{id}")
    @PreAuthorize("hasAuthority('POST_KARYAWAN_PENILAIAN_MANDIRI_TAMBAH_IDPROYEK_ID')")
    public String tambahPenilaianMandiriSubmit (Model model, HttpSession session, @RequestParam(value = "isi-evaluasi") String isiEvaluasi,
                                                @PathVariable Integer idProyek, @PathVariable Integer id, RedirectAttributes redirectAttributes){

        
        //Cari rekap by id
        RekapModel rekap = rekapService.selectRekapById(id);

        //set tanggal penilaian
        LocalDate tanggalPenilaian = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
        rekap.setTanggalPenilaian(tanggalPenilaian);

        //update object rekap bulanan
        rekapService.updatePenilaianMandiri(rekap, isiEvaluasi);

        //add flash attribute
        String notification = "Evaluasi diri berhasil disimpan";
        redirectAttributes.addFlashAttribute("notification", notification);

        return "redirect:/assignment/karyawan/penugasan/detail/"+rekap.getIdProyek();
    }

    /**
     * Method ini berfungsi untuk menampilkan daftar rekan seproyek suatu karyawan,
     * dan juga memfasilitasi untuk mengisi feedback dan penilaian
     * @param principal
     * @param periode
     * @return
     */
    @GetMapping(value="/assignment/karyawan/rekanseproyek")
    @PreAuthorize("hasAuthority('GET_KARYAWAN')")
    public String lihatRekanSekproyek(Model model, HttpSession session, Principal principal,
                                      @RequestParam(value = "periode", required = false) String periode)
    {
        LocalDate periodeNow = LocalDate.of(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), 1);
        KaryawanBaruModel karyawan = karyawanService.selectKaryawanByEmail(principal.getName());
        //Melakukan handling periode yang akan ditampilkan pada halaman rekan seproyek
        if(periode != null) {
            String[] split = periode.split(" ");
            int year = Integer.parseInt(split[1]);
            String monthString = split[0].toUpperCase();
            LocalDate periodeCompare = LocalDate.of(year, Month.valueOf(split[0].toUpperCase()),1);
            if(periodeCompare.equals(periodeNow)) {
                model.addAttribute("offPeriode", "offPeriode");
                session.setAttribute("periodeSelected", periodeNow);
            }else{
                if(periodeCompare.equals(periodeNow.plusMonths(1))) {
                    session.setAttribute("periodeSelected", periodeNow);
                    model.addAttribute("periodeOut", "Pengisian Feedback dan Penilaian Belum Dimulai atau Sudah Ditutup");
                }else {
                    periodeNow = periodeCompare;
                    session.setAttribute("periodeSelected", periodeNow);
                    model.addAttribute("periodeOut", "Pengisian Feedback dan Penilaian Belum Dimulai atau Sudah Ditutup");
                }
            }
        }else {
            model.addAttribute("offPeriode", "offPeriode");
            session.setAttribute("periodeSelected", periodeNow);
        }

        int userId = karyawan.getIdKaryawan();

        String penugasan = karyawanService.cekPenugasanKaryawanById(userId);

        //kondisional jika karyawan yang sedang login punya penugasan atau tidak punya penugasan
        if(penugasan.equalsIgnoreCase("yes")){

            List<Integer> proyekSekarang = karyawanService.getUserProyek(userId);

            List<FeedbackRatingModel> rekanWithFeedback =
                    karyawanService.getRekanSeproyekFeedback(proyekSekarang, userId, periodeNow);

            List<FeedbackRatingModel> rekanNoFeedback =
                    karyawanService.getRekanSeproyek(proyekSekarang, userId, periodeNow);

            if(rekanWithFeedback.isEmpty() && rekanNoFeedback.isEmpty()) {
                model.addAttribute("notification","Tidak ada rekan seproyek di periode ini");
            }else if(rekanWithFeedback.isEmpty()) {
                model.addAttribute("rekans",rekanNoFeedback);
                model.addAttribute("noFeedback","noFeedback");
            }else if(rekanNoFeedback.isEmpty()){
                model.addAttribute("rekans",rekanWithFeedback);
            }else {
                model.addAttribute("rekans", rekanWithFeedback);
                model.addAttribute("rekansNoFeedback", rekanNoFeedback);
            }

            String dateToday = rekapMappingService.getCurrentDate();
            model.addAttribute("prevPeriode", periodeNow.minusMonths(1));
            model.addAttribute("periodeNow", periodeNow);
            model.addAttribute("nextPeriode", periodeNow.plusMonths(1));
            model.addAttribute("date_today", dateToday);

        }else {
            String dateToday = rekapMappingService.getCurrentDate();
            model.addAttribute("noPenugasan", "noPenugasan");
            model.addAttribute("date_today", dateToday);
        }



        return "karyawan-rekanseproyek";
    }

    /**
     * Method ini berfungsi untuk menampilkan daftar rekan seproyek suatu karyawan,
     *  dan juga memfasilitasi untuk mengisi feedback dan penilaian
     * @param principal
     * @param namaRekan
     * @param idRekan
     * @param kodeProyek
     * @param feedback
     * @param ratingRekan
     * @return
     */
    @GetMapping(value="/assignment/karyawan/rekanseproyek/feedback")
    @PreAuthorize("hasAuthority('GET_KARYAWAN')")
    public String manageFeedbackRekan(Model model, RedirectAttributes redirectAttributes, HttpSession session, Principal principal,
                                      @RequestParam(value = "namaRekan", required = false) String namaRekan,
                                      @RequestParam(value = "idRekan", required = false) int idRekan,
                                      @RequestParam(value = "kodeProyek", required = false) String kodeProyek,
                                      @RequestParam(value = "feedback", required = false) String feedback,
                                      @RequestParam(value = "ratingRekan", required = false) int ratingRekan) throws ParseException {

        //Mempersiapkan variabel yang akan digunakan untuk mengambil rekan dari database
        KaryawanBaruModel karyawan = karyawanService.selectKaryawanByEmail(principal.getName());
        int userId = karyawan.getIdKaryawan();
        LocalDate periodeSelected = (LocalDate) session.getAttribute("periodeSelected");
        ProyekModel proyek = proyekService.getProyekByKode(kodeProyek);
        int idProyek = proyek.getId();

        KaryawanBaruModel karyawanPenilai = karyawanService.selectKaryawanByEmail(principal.getName());
        int idPenilai = karyawanPenilai.getIdKaryawan();

        KaryawanProyekModel karyawanProyek = karyawanProyekService.getKaryawanProyekByKaryawanandProyek(idRekan, idProyek);
        int idKaryawanProyek = karyawanProyek.getIdKaryawan();

        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        Date now = new Date();
        java.sql.Timestamp waktuIsi = new java.sql.Timestamp(now.getTime());


        //Memvalidasi apakah rekan sudah memiliki feedback atau belum
        //jika belum ada maka akan di-add, jika ada maka akan di-update
        String verifFeedback = karyawanService.verifyFeedbackRekan(idKaryawanProyek, idPenilai, idProyek, periodeSelected);
        if(verifFeedback.equalsIgnoreCase("belum ada")) {
            karyawanService.addFeedbackRekan(feedback, ratingRekan, idKaryawanProyek, idPenilai, idProyek, periodeSelected, waktuIsi);
            model.addAttribute("successEdit", "Berhasil memberikan feedback untuk rekan yang bernama " + namaRekan);
        }else {
            karyawanService.updateFeedbackRekan(feedback, ratingRekan, idKaryawanProyek, idPenilai, idProyek, periodeSelected, waktuIsi);
            model.addAttribute("successEdit", "Berhasil mengubah feedback untuk rekan yang bernama " + namaRekan);
        }


        List<Integer> proyekSekarang = karyawanService.getUserProyek(userId);

        //Mendapatkan rekan seproyek yang sudah diberi feedback dan penilaian dan yang belum diberi feedback dan penilaian
        List<FeedbackRatingModel> rekanWithFeedback =
                karyawanService.getRekanSeproyekFeedback(proyekSekarang, userId, periodeSelected);

        List<FeedbackRatingModel> rekanNoFeedback =
                karyawanService.getRekanSeproyek(proyekSekarang, userId, periodeSelected);

        if(rekanWithFeedback.isEmpty() && rekanNoFeedback.isEmpty()) {
            model.addAttribute("notification","Tidak ada rekan seproyek di periode ini");
        }else if(rekanWithFeedback.isEmpty()) {
            model.addAttribute("rekans",rekanNoFeedback);
            model.addAttribute("noFeedback","noFeedback");
        }else if(rekanNoFeedback.isEmpty()){
            model.addAttribute("rekans",rekanWithFeedback);
        }else {
            model.addAttribute("rekans", rekanWithFeedback);
            model.addAttribute("rekansNoFeedback", rekanNoFeedback);
        }

        String dateToday = rekapMappingService.getCurrentDate();
        model.addAttribute("prevPeriode", periodeSelected.minusMonths(1));
        model.addAttribute("offPeriode", "offPeriode");
        model.addAttribute("periodeNow", periodeSelected);
        model.addAttribute("nextPeriode", periodeSelected.plusMonths(1));
        model.addAttribute("date_today", dateToday);
        redirectAttributes.addFlashAttribute("notification","Evaluasi rekan berhasil disimpian");
        return "redirect:/assignment/karyawan/penugasan/detail/" + idProyek;
    }
}

