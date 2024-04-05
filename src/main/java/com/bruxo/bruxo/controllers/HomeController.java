package com.bruxo.bruxo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home(){

        return "pagInicial";
    }

   @Controller
    public class Principal{
        @GetMapping("/principal")
        public String principal(){
            return "principal";
        }
    }
}