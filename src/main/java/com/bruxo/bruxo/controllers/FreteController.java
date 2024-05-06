package com.bruxo.bruxo.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.bruxo.bruxo.models.OpcaoFrete;

@Controller
public class FreteController {

    @GetMapping("/calcularFrete")
    public ModelAndView exibirFormulario() {
        return new ModelAndView("formularioCalcularFrete");
    }

    @PostMapping("/calcularFrete")
    public ModelAndView calcularFrete(@RequestParam("cep") String cep) {


        ModelAndView modelAndView = new ModelAndView("modalOpcoesFrete");
        modelAndView.addObject("opcoesFrete", simularOpcoesFrete());
        return modelAndView;
    }

    private List<OpcaoFrete> simularOpcoesFrete() {

        return Arrays.asList(
                new OpcaoFrete(10.00, "3 dias úteis"),
                new OpcaoFrete(15.00, "2 dias úteis"),
                new OpcaoFrete(20.00, "1 dia útil")
        );
    }
}
