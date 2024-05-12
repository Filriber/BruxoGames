package com.bruxo.bruxo.controllers;


import com.bruxo.bruxo.models.Usuario;
import com.bruxo.bruxo.models.UsuarioDto;
import com.bruxo.bruxo.service.UsuarioRepository;


import jakarta.servlet.http.HttpSession;
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


@Controller
@RequestMapping("/usuarios")
@Service
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

    PasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping({"", "/"})
    public String showUsuariosList(Model model, HttpSession session) {
        List<Usuario> usuarios = repo.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("usuarios", usuarios);

        String grupoUsuario = (String) session.getAttribute("grupo");
        model.addAttribute("grupoUsuario", grupoUsuario);
        return "usuarios/index";
    }

    @GetMapping("/create")
    public String showCriaUsuario(Model model) {
        UsuarioDto usuarioDto = new UsuarioDto();
        model.addAttribute("usuarioDto", usuarioDto);
        return "usuarios/CriaUsuario";
    }

    @PostMapping("/create")
    public String criarUsuario(@ModelAttribute("usuarioDto") @Valid UsuarioDto usuarioDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Se houver erros de validação, retorne para o formulário de registro
            return "usuarios/CriaUsuario";
        }

        if (repo.existsByEmail((usuarioDto.getEmail()))) {
            bindingResult.rejectValue("email", "error.usuarioDto", "Este email já está em uso");
            return "usuarios/CriaUsuario";
        }
        if (!usuarioDto.getSenha().equals(usuarioDto.getConfirmaSenha())) {
            // Adicione um erro ao BindingResult
            bindingResult.rejectValue("confirmaSenha", "error.clienteDto", "As senhas não coincidem");
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

        String cpf = usuarioDto.getCpf();
        if (!isValidCPF(cpf)) {
            bindingResult.rejectValue("cpf", "error.usuarioDto", "CPF inválido");
            return "usuarios/CriaUsuario";
        }

        // Configurar atributos de usuarioDto para usuario
        // Salvar usuario no repositório
        repo.save(usuario);

        // Redirecionar para a lista de usuários após a criação bem-sucedida
        return "redirect:/usuarios";
    }

    @GetMapping("/edit")
    public String MostraEdicao(Model model, @RequestParam int id) {

        try {
            Usuario usuario = repo.findById(id).get();
            model.addAttribute("usuario", usuario);

            UsuarioDto usuarioDto = new UsuarioDto();
            usuarioDto.setId(usuario.getId());
            usuarioDto.setNome(usuario.getNome());
            usuarioDto.setEmail(usuario.getEmail());
            usuarioDto.setCpf(usuario.getCpf());
            usuarioDto.setSenha(usuario.getSenha());
            usuarioDto.setGrupo(usuario.getGrupo());
            usuarioDto.setStatus(usuario.getStatus());

            model.addAttribute("usuarioDto", usuarioDto);

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

            if (!usuarioDto.getSenha().equals(usuarioDto.getConfirmaSenha())) {
                // Adicione um erro ao BindingResult
                bindingResult.rejectValue("confirmaSenha", "error.clienteDto", "As senhas não coincidem");
                return "usuarios/CriaUsuario";
            }
            // Configurar atributos de usuarioDto para usuario
            usuario.setNome(usuarioDto.getNome());
            usuario.setEmail(usuarioDto.getEmail());
            usuario.setCpf(usuarioDto.getCpf());
            usuario.setGrupo(usuarioDto.getGrupo());

            // Encriptar a senha usando o Bcrypt
            String senhaEncriptada = this.passwordEncoder.encode(usuarioDto.getSenha());
            usuario.setSenha(senhaEncriptada);

            String cpf = usuarioDto.getCpf();
            if (!isValidCPF(cpf)) {
                bindingResult.rejectValue("cpf", "error.usuarioDto", "CPF inválido");
                return "usuarios/CriaUsuario";
            }

            // Salvar usuario no repositório
            repo.save(usuario);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        // Redirecionar para a lista de usuários após a edição bem-sucedida
        return "redirect:/usuarios";
    }

    @PostMapping("/atualizarStatus")
    public String atualizaStatus(@RequestParam int id, @ModelAttribute UsuarioDto usuarioDto) {
        Usuario usuario = repo.findById(id).orElseThrow(() -> new RuntimeException("Usuario não encontrado"));

        //altera o status do usuario
        usuario.setStatus("Ativo".equals(usuario.getStatus()) ? "Inativo" : "Ativo");
        //se o status for ativo, se for true, altera para inativo, caso contrario altera para ativo

        repo.save(usuario);
        return "redirect:/usuarios";
    }

    private boolean isValidCPF(String cpf) {
        // Remove caracteres especiais do CPF
        cpf = cpf.replaceAll("[^0-9]", "");

        // Verifica se o CPF possui 11 dígitos
        if (cpf.length() != 11) {
            return false;
        }

        // Calcula o primeiro dígito verificador
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (cpf.charAt(i) - '0') * (10 - i);
        }
        int remainder = 11 - (sum % 11);
        int digit1 = (remainder >= 10) ? 0 : remainder;

        // Verifica o primeiro dígito verificador
        if (digit1 != (cpf.charAt(9) - '0')) {
            return false;
        }

        // Calcula o segundo dígito verificador
        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (cpf.charAt(i) - '0') * (11 - i);
        }
        remainder = 11 - (sum % 11);
        int digit2 = (remainder >= 10) ? 0 : remainder;

        // Verifica o segundo dígito verificador
        if (digit2 != (cpf.charAt(10) - '0')) {
            return false;
        }

        return true;
    }

}
