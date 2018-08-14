package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.KaryawanBaruModel;
import com.pusilkom.hris.service.KaryawanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HRController {

    @Autowired
    KaryawanService karyawanService;

    /**
     * method untuk menampilkan beranda eksekutif yang berisi matriks penugasan
     * @param model
     * @return
     */
    @GetMapping("/employee/hr")
    public String indexhr(Model model){
        List<KaryawanBaruModel> karyawanList = karyawanService.getKaryawanBaruAll();
        model.addAttribute("karyawanList",karyawanList);
        return "index-hr";
    }
}
