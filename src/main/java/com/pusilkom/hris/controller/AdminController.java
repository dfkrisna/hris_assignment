package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.PegawaiModel;
import com.pusilkom.hris.model.KaryawanModel;
import com.pusilkom.hris.model.PenggunaModel;
import com.pusilkom.hris.model.Roles;
import com.pusilkom.hris.model.DivisibaruModel;
import com.pusilkom.hris.service.PegawaiService;
import com.pusilkom.hris.service.DivisiService;
import com.pusilkom.hris.service.KaryawanService;
import com.pusilkom.hris.service.PenggunaService;
import com.pusilkom.hris.service.RekapMappingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
public class AdminController
{
    @Autowired
    PenggunaService penggunaDAO;

    @Autowired
    RekapMappingService rekapMappingService;

    @Autowired
    PegawaiService pegawaiDAO;

    @Autowired
    DivisiService divisiDAO;

    @Autowired
    KaryawanService karyawanServiceDAO;
    /**
     * Method ini mengarahkan user ke halaman form add pengguna
     * @param model
     * @return
     */
    @GetMapping("/assignment/admin/addpengguna")
    @PreAuthorize("hasAuthority('GET_ADMIN')")
    public String addPenggunaForm(Model model)
    {
        PenggunaModel pengguna = new PenggunaModel();
        List<PegawaiModel> pegawais = pegawaiDAO.getAllAvailablePegawai();

        String dateToday = rekapMappingService.getCurrentDate();
        model.addAttribute("pengguna", pengguna);
        model.addAttribute("pegawais", pegawais);
        model.addAttribute("date_today", dateToday);
        model.addAttribute("page_title","Add Pengguna");
        return "admin-addpengguna";
    }

    /**
     * Method ini melakukan post ke database untuk menambahkan pengguna
     * @param model
     * @param newPengguna
     * @return
     */
    @PostMapping(value = "/assignment/admin/addpengguna/submit")
    @PreAuthorize("hasAuthority('POST_ADMIN_ADDPENGGUNA_SUBMIT')")
    public String addPenggunaSubmit(Model model, PenggunaModel newPengguna)
    {

        penggunaDAO.addPengguna(newPengguna);
        String dateToday = rekapMappingService.getCurrentDate();
        List<PenggunaModel> penggunas = penggunaDAO.getListPengguna();
        model.addAttribute("penggunas",penggunas);
        model.addAttribute("notification", "Pegawai dengan nama " + newPengguna.getNama() + " Berhasil menjadi pengguna");
        model.addAttribute("date_today", dateToday);
        model.addAttribute("page_title","Beranda Admin");
        return "index-admin";
    }


    /**
     * Method ini akan menampilkan halaman form update pengguna beserta field yang sudah terisi
     * @param model
     * @param id
     *
     */
    @GetMapping("/assignment/admin/updatepengguna/{id}")
    @PreAuthorize("hasAuthority('GET_ADMIN')")
    public String updatePengguna (Model model, @PathVariable(value = "id") int id)
    {
        PenggunaModel pengguna = penggunaDAO.getPengguna(id);
        List<PegawaiModel> pegawais = pegawaiDAO.getAllAvailablePegawai();
        List<Roles> systemRoles = penggunaDAO.getUnchosenRoles(id);
        List<Roles> currentRoles = pengguna.getRoles();
        if(currentRoles.get(0).getAlias().equalsIgnoreCase("Belum Punya Role")) {
            currentRoles.clear();
        }

        String dateToday = rekapMappingService.getCurrentDate();
        model.addAttribute("pegawais", pegawais);
        model.addAttribute("namaSekarang", pengguna.getNama());
        model.addAttribute("idPegawaiSekarang", pengguna.getId_pegawai());
        model.addAttribute("currentRoles",currentRoles);
        model.addAttribute("systemRoles",systemRoles);
        model.addAttribute("date_today", dateToday);
        model.addAttribute("pengguna", pengguna);
        model.addAttribute("page_title","Update Pengguna");

        return  "admin-updatepengguna";

    }


    /*
     * Method ini berfungsi untuk melakukan pengolahan data yang di-update pada form update pengguna
     * termasuk nama dan role pengguna
     */
    @PostMapping(value = "/assignment/admin/updatepengguna/submit")
    @PreAuthorize("hasAuthority('POST_ADMIN_UPDATEPENGGUNA_SUBTMIT')")
    public String updatePenggunaSubmit(Model model, PenggunaModel pengguna,
                                       @RequestParam(value="roleChecked") List<String> roleBaru)
    {
        int id_pengguna = Integer.parseInt(penggunaDAO.getIdByUsername(pengguna.getUsername()));
        System.out.println(id_pengguna);
        PenggunaModel penggunaLama = penggunaDAO.getPenggunaLama(pengguna.getUsername());
        List<String>  roleLama = penggunaDAO.getPenggunaStringRoleByUsername(pengguna.getUsername());

        String[] st = pengguna.getId_pegawai_temp().split(":");

        //menginisiasi list role lama dan role baru yang akan diolah
        List<String> roleBaruToCompare = new ArrayList<String>();
        List<String> roleLamaToCompare = new ArrayList<String>();
        for(int i = 0; i< roleBaru.size(); i++) {
            if(!roleBaru.get(i).equalsIgnoreCase("a-role")) {
                roleBaruToCompare.add(roleBaru.get(i));
            }
        }

        for(int i = 0; i< roleLama.size(); i++) {
            if(!roleLama.get(i).equalsIgnoreCase("Belum Punya Role")) {
                roleLamaToCompare.add(roleLama.get(i));
            }
        }

        System.out.println(roleLamaToCompare);
        System.out.println(roleBaruToCompare);

        String id = st[0];
        String namaBaru = st[1];

        //membatasi hanya 2 role yang dapat dipilih oleh pengguna
        if(roleBaruToCompare.size() > 2) {
            PenggunaModel penggunaz = penggunaDAO.getPengguna(id_pengguna);
            List<PegawaiModel> pegawais = pegawaiDAO.getAllAvailablePegawai();
            List<Roles> systemRoles = penggunaDAO.getUnchosenRoles(id_pengguna);
            List<Roles> currentRoles = penggunaz.getRoles();
            if(currentRoles.get(0).getAlias().equalsIgnoreCase("Belum Punya Role")) {
                currentRoles.clear();
            }

            String dateToday = rekapMappingService.getCurrentDate();
            model.addAttribute("pegawais", pegawais);
            model.addAttribute("namaSekarang", penggunaz.getNama());
            model.addAttribute("idPegawaiSekarang", penggunaz.getId_pegawai());
            model.addAttribute("currentRoles",currentRoles);
            model.addAttribute("systemRoles",systemRoles);
            model.addAttribute("date_today", dateToday);
            model.addAttribute("pengguna", penggunaz);
            model.addAttribute("page_title","Update Pengguna");
            model.addAttribute("notification", "1 Pengguna hanya boleh punya role maksimal 2");

            return  "admin-updatepengguna";
        }

        String hasilCompare = penggunaDAO.compareRole(roleLamaToCompare, roleBaruToCompare);
        System.out.println(hasilCompare);

        //menghandle berbagai kemungkinan perubahan role seperti penambahan role, pergantian role, handling
        //jika role lebih dari satu, dan berbagai case lain seperti nama saja yang berubah, role saja
        //yang berubah, keduanya berubah, dan keduanya tidak berubah
        if(namaBaru.equalsIgnoreCase(penggunaLama.getNama())) {

            if(hasilCompare.equalsIgnoreCase("sama")) {
                //ketika nama gk berubah, dan role gk berubah
                String dateToday = rekapMappingService.getCurrentDate();
                List<PenggunaModel> penggunas = penggunaDAO.getListPengguna();
                model.addAttribute("penggunas",penggunas);
                model.addAttribute("date_today",dateToday);
                model.addAttribute("page_title","Beranda Admin");
                model.addAttribute("notification", "Tidak ada perubahan");
                return "index-admin";
            }else {
                //ketika nama gk berubah, tapi role berubah
                if(roleBaruToCompare.size() == 0) {
                    //ketika rolenya gk dicentang sama sekali
                    penggunaDAO.deleteAllRolePengguna(id_pengguna);
                    penggunaDAO.unassignAllRole(id_pengguna);

                    String dateToday = rekapMappingService.getCurrentDate();
                    List<PenggunaModel> penggunas = penggunaDAO.getListPengguna();
                    model.addAttribute("penggunas",penggunas);
                    model.addAttribute("date_today",dateToday);
                    model.addAttribute("page_title","Beranda Admin");
                    model.addAttribute("notification", "Pengguna dengan nama " + penggunaLama.getNama() + " telah berganti role");
                    return "index-admin";
                }

                penggunaDAO.deleteAllRolePengguna(id_pengguna);
                penggunaDAO.addRoleBaru(id_pengguna, roleBaruToCompare);

                String dateToday = rekapMappingService.getCurrentDate();
                List<PenggunaModel> penggunas = penggunaDAO.getListPengguna();
                model.addAttribute("penggunas",penggunas);
                model.addAttribute("date_today",dateToday);
                model.addAttribute("page_title","Beranda Admin");
                model.addAttribute("notification", "Pengguna dengan nama " + penggunaLama.getNama() + " telah berganti role");
                return "index-admin";

            }

        }else {
            if(hasilCompare.equalsIgnoreCase("sama")) {
                //ketika nama berubah, tapi role gk berubah
                penggunaDAO.updateNamaPengguna(id_pengguna, pengguna.getId_pegawai_temp(), namaBaru);

                String dateToday = rekapMappingService.getCurrentDate();
                List<PenggunaModel> penggunas = penggunaDAO.getListPengguna();
                model.addAttribute("penggunas",penggunas);
                model.addAttribute("date_today",dateToday);
                model.addAttribute("page_title","Beranda Admin");
                model.addAttribute("notification", "Pengguna dengan nama " + penggunaLama.getNama() + " telah berganti nama menjadi " +
                        namaBaru);
                return "index-admin";

            }else{
                //ketika nama berubah, dan role berubah
                if(roleBaruToCompare.size() == 0) {
                    //ketika rolenya gk dicentang sama sekali
                    penggunaDAO.updateNamaPengguna(id_pengguna, pengguna.getId_pegawai_temp(), namaBaru);
                    penggunaDAO.deleteAllRolePengguna(id_pengguna);
                    penggunaDAO.unassignAllRole(id_pengguna);

                    String dateToday = rekapMappingService.getCurrentDate();
                    List<PenggunaModel> penggunas = penggunaDAO.getListPengguna();
                    model.addAttribute("penggunas",penggunas);
                    model.addAttribute("date_today",dateToday);
                    model.addAttribute("page_title","Beranda Admin");
                    model.addAttribute("notification", "Pengguna dengan nama " + penggunaLama.getNama() + " telah berganti nama menjadi " +
                            namaBaru + " dan berganti role");
                    return "index-admin";
                }
                penggunaDAO.updateNamaPengguna(id_pengguna, pengguna.getId_pegawai_temp(), namaBaru);
                penggunaDAO.deleteAllRolePengguna(id_pengguna);
                penggunaDAO.addRoleBaru(id_pengguna, roleBaruToCompare);

                String dateToday = rekapMappingService.getCurrentDate();
                List<PenggunaModel> penggunas = penggunaDAO.getListPengguna();
                model.addAttribute("penggunas",penggunas);
                model.addAttribute("date_today",dateToday);
                model.addAttribute("page_title","Beranda Admin");
                model.addAttribute("notification", "Pengguna dengan nama " + penggunaLama.getNama() + " telah berganti nama menjadi " +
                        namaBaru + " dan berganti role");
                return "index-admin";



            }


        }
    }


    /*
     * method yang berfungsi untuk menghapus pengguna dari sistem
     */
    @GetMapping("/assignment/admin/deletepengguna/{id}")
    @PreAuthorize("hasAuthority('GET_ADMIN')")
    public String deletePengguna (Model model, @PathVariable(value = "id") int id)
    {
        PenggunaModel pengguna = penggunaDAO.getPengguna(id);
        penggunaDAO.deletePengguna(id);

        String dateToday = rekapMappingService.getCurrentDate();
        List<PenggunaModel> penggunas = penggunaDAO.getListPengguna();
        model.addAttribute("penggunas",penggunas);
        model.addAttribute("date_today",dateToday);
        model.addAttribute("page_title","Beranda Admin");
        model.addAttribute("notification", "Pengguna dengan nama " + pengguna.getNama() + " telah dihapus");
        return "index-admin";
    }

    //    @GetMapping("/admin/deletepengguna/")
//    public String deletePengguna (Model model,
//                                  @RequestParam(value = "id") int id,
//                                  @RequestParam(value = "usernamePengguna") String usernamePengguna)
//    {
//        PenggunaModel pengguna = penggunaDAO.getPengguna(id);
//        System.out.println(pengguna);
//        penggunaDAO.deletePengguna(id);
//
//        String dateToday = rekapMappingService.getCurrentDate();
//        List<PenggunaModel> penggunas = penggunaDAO.getListPengguna();
//        model.addAttribute("penggunas",penggunas);
//        model.addAttribute("date_today",dateToday);
//        model.addAttribute("page_title","Beranda Admin");
//        model.addAttribute("notification", "Pengguna dengan nama " + pengguna.getNama() + " telah dihapus");
//        return "index-admin";
//    }

    @RequestMapping("/employee/admin/keloladivisi")
    @PreAuthorize("hasAuthority('GET_ADMIN')")
    public String kelolaDivisi (Model model)
    {
       List<DivisibaruModel> divisis = divisiDAO.selectAllDivisiAktif();
       List<DivisibaruModel> divisiNonAktif = divisiDAO.selectAllDivisiNonAktif();
       List<KaryawanModel> namaKaryawan = karyawanServiceDAO.selectNamaEmployeeAll();

       DivisibaruModel divisi = new DivisibaruModel();

       model.addAttribute("divisi", divisi);
       model.addAttribute("namaKaryawans", namaKaryawan);


       if(divisis!=null){
            model.addAttribute("divisis",divisis); }

        model.addAttribute("divisiNonAktif",divisiNonAktif);

        System.out.println(divisiNonAktif);

        return  "admin-keloladivisi";
    }


    @RequestMapping(value = "/employee/admin/keloladivisi/tambah", method = RequestMethod.POST)
    public String addDivisi (RedirectAttributes ra, Model model, @ModelAttribute DivisibaruModel divisi)
    {

        divisiDAO.addDivisi(divisi);

        ra.addFlashAttribute("notification", "Berhasil menambahkan divisi " + divisi.getNama());
        return "redirect:/employee/admin/keloladivisi";
    }

    @RequestMapping(value = "/employee/admin/keloladivisi/ubah/{id}")
    public String updateDivisi (Model model, @PathVariable(value = "id") int id)
    {
        DivisibaruModel divisi = divisiDAO.selectDivisiBaruByID(id);
        List<KaryawanModel> namaKaryawan = karyawanServiceDAO.selectNamaEmployeeAll();

        model.addAttribute ("divisi", divisi);
        model.addAttribute("namaKaryawans", namaKaryawan);

        return "ubah-divisi";
    }

    @RequestMapping(value = "/employee/admin/keloladivisi/ubah/{id}" , method = RequestMethod.POST)
    public String updateDivisi (@ModelAttribute DivisibaruModel divisiUpdate, Model model, @RequestParam(value="pindahDivisi", required=false) String divisiBaru) {

        divisiDAO.updateDivisi(divisiUpdate);

        List<DivisibaruModel> divisis = divisiDAO.selectAllDivisiAktif();
        List<DivisibaruModel> divisiNonAktif = divisiDAO.selectAllDivisiNonAktif();
        List<KaryawanModel> namaKaryawan = karyawanServiceDAO.selectNamaEmployeeAll();


        if(divisiBaru!=null){
            karyawanServiceDAO.updateKaryawanDivisi(divisiUpdate.getIdManager(), divisiUpdate.getId());
        }

        DivisibaruModel divisi = new DivisibaruModel();

        model.addAttribute("divisi", divisi);
        model.addAttribute("namaKaryawans", namaKaryawan);

        if(divisis!=null){
            model.addAttribute("divisis",divisis);
        }

        model.addAttribute("divisiNonAktif",divisiNonAktif);

        return "admin-keloladivisi";
    }

    @GetMapping(value = "/employee/admin/keloladivisi/nonaktifkan/{id}" )
    @PreAuthorize("hasAuthority('GET_ADMIN')")
    public String nonAktifkanDivisi (RedirectAttributes ra, Model model, @PathVariable(value = "id") int id)
    {
        DivisibaruModel divisi = divisiDAO.selectDivisiBaruByID(id);
        divisiDAO.nonAktifkanDivisi(id);

        ra.addFlashAttribute("nonaktifkan", "Divisi " + divisi.getNama() + " telah dinonaktifkan");

        return "redirect:/employee/admin/keloladivisi";
    }

    @GetMapping(value = "/employee/admin/keloladivisi/aktifkan/{id}" )
    @PreAuthorize("hasAuthority('GET_ADMIN')")
    public String aktifkanDivisi (RedirectAttributes ra, Model model, @PathVariable(value = "id") int id)
    {
        DivisibaruModel divisi = divisiDAO.selectDivisiBaruByID(id);
        divisiDAO.aktifkanDivisi(id);

        ra.addFlashAttribute("notification", "Divisi " + divisi.getNama() + " telah diaktifkan");
        return "redirect:/employee/admin/keloladivisi";
    }

    @RequestMapping("/employee/admin/keloladivisi/hapus/{id}")
    public String delete (RedirectAttributes ra, Model model, @PathVariable(value = "id") int id)
    {
        DivisibaruModel divisi = divisiDAO.selectDivisiBaruByID(id);

        divisiDAO.hapusDivisi(id);

        ra.addFlashAttribute("nonaktifkan", "Divisi " + divisi.getNama() + " telah dihapus");
        return "redirect:/employee/admin/keloladivisi";
    }

}
