package com.bruxo.bruxo.controllers;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.bruxo.bruxo.models.Usuario;
import com.bruxo.bruxo.models.UsuarioDto;
import com.bruxo.bruxo.service.UsuarioRepository;

import java.util.Optional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Controller
@Service
public class SignUpController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    PasswordEncoder passwordEncoder;
    
    public SignUpController(UsuarioRepository usuarioRepository){
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping("/signup")
    public String getSignupPage(){
        return "signup";
    }

    @PostMapping("/signup")
    public String submitSignUp(UsuarioDto usuarioDto, HttpServletRequest request) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(usuarioDto.getEmail());

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            // Encriptar a senha fornecida antes de comparar
            String senhaEncriptada = passwordEncoder.encode(usuarioDto.getSenha());

            if (passwordEncoder.matches(usuarioDto.getSenha(), usuario.getSenha())) {
                HttpSession session = request.getSession();
                session.setAttribute("grupo", usuario.getGrupo());

                if ("Cliente".equals(usuario.getGrupo())) {
                    return "redirect:/signup?error=cliente";
                } else if ("Administrador".equals(usuario.getGrupo())) {
                    return "redirect:/";
                } else if ("Estoquista".equals(usuario.getGrupo())) {
                    return "redirect:/estoquista";
                } else {
                    return "redirect:/signup?error";
                }
            } else {
                return "redirect:/signup?error";
            }
        } else {
            return "redirect:/signup?error";
        }
    }
    @GetMapping("/estoquista")
    public String estoquistaPage() {
        return "templates/usuarios/estoquista";
    }

}