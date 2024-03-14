package com.bruxo.bruxo.controllers;

import jakarta.validation.Valid;
import java.security.Principal;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import com.bruxo.bruxo.models.Usuario;
import com.bruxo.bruxo.models.UsuarioDto;
import com.bruxo.bruxo.service.UsuarioRepository;



@Controller
@CrossOrigin("*")
@RequestMapping("/usuarios")
@Service
public class UsuarioController {

    
    @Autowired
    private UsuarioRepository repo;
    
    PasswordEncoder passwordEncoder;
    
    public UsuarioController(UsuarioRepository usuarioRepository){
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    
    @GetMapping({"", "/"})
    public String showUsuariosList(Model model) {
        List<Usuario> usuarios = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("usuarios", usuarios);
        return "usuarios/index";
    }


    @GetMapping("/create")
    public String showCriaUsuario(Model model) {
        UsuarioDto usuarioDto = new UsuarioDto();
        model.addAttribute("usuarioDto", usuarioDto);
        return "usuarios/CriaUsuario";
    }

    @PostMapping("/create")
    public String criarUsuario(@ModelAttribute("usuarioDto") @Valid UsuarioDto usuarioDto, BindingResult bindingResult, Model Model) {
        if (bindingResult.hasErrors()) {
            // Se houver erros de validação, retorne para o formulário de registro
            return "usuarios/CriaUsuario";
        }

        if (repo.existsByEmail((usuarioDto.getEmail()))) {
            bindingResult.rejectValue("email", "error.usuarioDto", "Este email já está em uso");
            return "usuarios/CriaUsuario";
        }

        // Mapear UsuarioDto para a entidade Usuario
        Usuario usuario = new Usuario();

        usuario.setNome(usuarioDto.getNome());
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setCpf(usuarioDto.getCpf());
        
        //encripatar a senha usando o Bcrypt
        String senhaEcripitada = this.passwordEncoder.encode(usuarioDto.getSenha());
        usuario.setSenha(senhaEcripitada);
        
        usuario.setGrupo(usuarioDto.getGrupo());
        usuario.setStatus(usuarioDto.getStatus());

  

        // Configurar atributos de usuarioDto para usuario

        // Salvar usuario no repositório
        repo.save(usuario);

        // Redirecionar para a lista de usuários após a criação bem-sucedida
        return "redirect:/usuarios";
    }

    @GetMapping("/edit")
    public String MostraEdicao(Model Model, @RequestParam int id) {

        try {
            Usuario usuario = repo.findById(id).get();
            Model.addAttribute("usuario", usuario);

            UsuarioDto usuarioDto = new UsuarioDto();
            usuarioDto.setId(usuario.getId());
            usuarioDto.setNome(usuario.getNome());
            usuarioDto.setEmail(usuario.getEmail());
            usuarioDto.setCpf(usuario.getCpf());
            usuarioDto.setSenha(usuario.getSenha());
            usuarioDto.setGrupo(usuario.getGrupo());
            usuarioDto.setStatus(usuario.getStatus());

            Model.addAttribute("usuarioDto", usuarioDto);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/usuarios";
        }
        return "usuarios/EditarUsuario";

    }

    @PostMapping("/edit")
public String editarUsuario(Model model, Principal principal, @RequestParam int id, @Valid @ModelAttribute UsuarioDto usuarioDto, BindingResult bindingResult) {
    // Verificar se o usuário autenticado está tentando editar seu próprio perfil
    if (principal != null && principal.getName().equals(usuarioDto.getEmail())) {
        bindingResult.rejectValue("email", "error.usuarioDto", "Você não pode editar seu próprio perfil");
        return "usuarios/EditarUsuario";
    }

    try {
        Usuario usuario = repo.findById(id).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        model.addAttribute("usuario", usuario);

        if (bindingResult.hasErrors()) {
            // Se houver erros de validação, retorne para o formulário de edição
            return "usuarios/EditarUsuario";
        }

        // Configurar atributos de usuarioDto para usuario
        usuario.setNome(usuarioDto.getNome());
        usuario.setEmail(usuarioDto.getEmail());
        usuario.setCpf(usuarioDto.getCpf());
        usuario.setGrupo(usuarioDto.getGrupo());

        // Encriptar a senha usando o Bcrypt
        String senhaEncriptada = this.passwordEncoder.encode(usuarioDto.getSenha());
        usuario.setSenha(senhaEncriptada);

        // Salvar usuario no repositório
        repo.save(usuario);

    } catch (Exception ex) {
        System.out.println("Exception: " + ex.getMessage());
    }

    // Redirecionar para a lista de usuários após a edição bem-sucedida
    return "redirect:/usuarios";
}
    @PostMapping("/atualizarStatus")
    public String atualizaStatus(@RequestParam int id,@ModelAttribute UsuarioDto usuarioDto){
        Usuario usuario = repo.findById(id).orElseThrow(() -> new RuntimeException("Usuario não encontrado"));

        //altera o status do usuario
        usuario.setStatus("Ativo".equals(usuario.getStatus()) ? "Inativo" : "Ativo");
        //se o status for ativo, se for true, altera para inativo, caso contrario altera para ativo

        repo.save(usuario);
        return "redirect:/usuarios";
    }
}