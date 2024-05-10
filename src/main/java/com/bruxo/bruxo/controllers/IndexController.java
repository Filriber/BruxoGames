package com.bruxo.bruxo.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        String grupoUsuario = (String) session.getAttribute("grupo");
        model.addAttribute("grupoUsuario", grupoUsuario);
        return "usuarios/home";
    }
}
