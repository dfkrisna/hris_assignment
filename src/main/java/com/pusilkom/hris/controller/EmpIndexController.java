package com.pusilkom.hris.controller;

import com.pusilkom.hris.model.UserWeb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.constraints.NotNull;

@Slf4j
@Controller
@ControllerAdvice
public class EmpIndexController {
    @GetMapping("/employee")
    public String indexMoka(Model model, @NotNull Authentication auth) {
        UserWeb user = (UserWeb) auth.getPrincipal();
        model.addAttribute("currentUser", user);
        return "emp-index";
    }
}
