package com.bruxo.bruxo.controllers;

import java.security.Principal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.bruxo.bruxo.models.*;
import com.bruxo.bruxo.service.PedidoRepository;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;


import com.bruxo.bruxo.service.ClienteRepository;
import com.bruxo.bruxo.service.EnderecoRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteRepository repo;

    @Autowired
    private EnderecoRepository repository;

    @Autowired
    private PedidoRepository pedidoRepository;

    PasswordEncoder passwordEncoder;

    public ClienteController(ClienteRepository clienteRepository) {
        this.repo = clienteRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @GetMapping("/create")
    public String showCriaCliente(Model model) {
        ClienteDto clienteDto = new ClienteDto();

        model.addAttribute("clienteDto", clienteDto);
        return "clientes/CriaCliente";
    }

    @PostMapping("/create")
    public String criarCliente(@ModelAttribute("clienteDto") @Valid ClienteDto clienteDto, BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            // Se houver erros de validação, retorne para o formulário de registro
            return "clientes/CriaCliente";
        }

        if (repo.existsByEmail((clienteDto.getEmail()))) {
            bindingResult.rejectValue("email", "error.clienteDto", "Este email já está em uso");
            return "clientes/CriaCliente";
        }
        if (!clienteDto.getSenha().equals(clienteDto.getConfirmaSenha())) {
            // Adicione um erro ao BindingResult
            bindingResult.rejectValue("confirmaSenha", "error.clienteDto", "As senhas não coincidem");
            return "clientes/CriaCliente";
        }

        // Verificar se o cliente forneceu um endereço
        if (clienteDto.getCep() == null || clienteDto.getLogradouro() == null || clienteDto.getNumero() == null) {
            // Adicionar uma mensagem de erro ao BindingResult
            bindingResult.reject("endereco", "É necessário fornecer um endereço");

            // Retornar para o formulário de registro com a mensagem de erro
            return "clientes/CriaCliente";
        }

        // Mapear clienteDto para a entidade cliente
        Cliente cliente = new Cliente();
        Endereco enderecoFatura = new Endereco();
        Endereco enderecoEntrega = new Endereco();

        cliente.setNome(clienteDto.getNome());
        cliente.setEmail(clienteDto.getEmail());
        cliente.setGenero(clienteDto.getGenero());
        cliente.setCpf(clienteDto.getCpf());
        cliente.setDataNascimento(clienteDto.getDataNascimento());

        enderecoFatura.setCep(clienteDto.getCep());
        enderecoFatura.setLogradouro(clienteDto.getLogradouro());
        enderecoFatura.setNumero(clienteDto.getNumero());
        enderecoFatura.setComplemento(clienteDto.getComplemento());
        enderecoFatura.setBairro(clienteDto.getBairro());
        enderecoFatura.setCidade(clienteDto.getCidade());
        enderecoFatura.setUf(clienteDto.getUf());
        enderecoFatura.setEndereco("FATURAMENTO");

        enderecoEntrega.setCep(clienteDto.getCep());
        enderecoEntrega.setLogradouro(clienteDto.getLogradouro());
        enderecoEntrega.setNumero(clienteDto.getNumero());
        enderecoEntrega.setComplemento(clienteDto.getComplemento());
        enderecoEntrega.setBairro(clienteDto.getBairro());
        enderecoEntrega.setCidade(clienteDto.getCidade());
        enderecoEntrega.setUf(clienteDto.getUf());
        enderecoEntrega.setEndereco("ENTREGA");

        cliente.setEnderecoPadrao(enderecoEntrega);
        cliente.getEnderecos().add(enderecoFatura);
        enderecoFatura.setCliente(cliente);

        cliente.getEnderecos().add(enderecoEntrega);
        enderecoEntrega.setCliente(cliente);

        // encripatar a senha usando o Bcrypt
        String senhaEcripitada = this.passwordEncoder.encode(clienteDto.getSenha());
        cliente.setSenha(senhaEcripitada);

        String cpf = clienteDto.getCpf();
        if (!isValidCPF(cpf)) {
            bindingResult.rejectValue("cpf", "error.clienteDto", "CPF inválido");
            return "clientes/Criacliente";
        }

        // Configurar atributos de clienteDto para cliente
        // Salvar cliente no repositório
        repo.save(cliente);

        // Redirecionar para a lista de usuários após a criação bem-sucedida
        return "redirect:/login";
    }

    @GetMapping("/edit")
    public String mostrarEdicao(Model model, @RequestParam int id) {

        try {
            Cliente cliente = repo.findById(id).orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

            // Mapear os atributo s do produto para o DTO
            ClienteDto clienteDto = new ClienteDto();
            clienteDto.setId(cliente.getId());
            clienteDto.setNome(cliente.getNome());
            clienteDto.setEmail(cliente.getEmail());
            clienteDto.setSenha(cliente.getSenha());
            clienteDto.setConfirmaSenha(cliente.getSenha());
            clienteDto.setGenero(cliente.getGenero());
            clienteDto.setCpf(cliente.getCpf());
            clienteDto.setGenero(cliente.getGenero());
            clienteDto.setDataNascimento(cliente.getDataNascimento());

            // Adicionar o cliente e o DTO ao modelo
            model.addAttribute("cliente", cliente);
            model.addAttribute("clienteDto", clienteDto);

            // List<String> imagens = cliente.getImagens();
            // model.addAttribute("imagens", imagens);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/home";
        }
        return "clientes/EditarCliente";
    }

    @PostMapping("/edit")
    public String editarCliente(Model model, Principal principal, @RequestParam int id,
                                @Valid @ModelAttribute ClienteDto clienteDto, BindingResult bindingResult, HttpSession session) {
        // Verificar se o usuário autenticado está tentando editar seu próprio perfil

        try {
            Cliente cliente = repo.findById(id).orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            model.addAttribute("cliente", cliente);

            // if (bindingResult.hasErrors()) {
            // Se houver erros de validação, retorne para o formulário de edição
            // return "clientes/EditarCliente";
            // }

            if (!clienteDto.getSenha().equals(clienteDto.getConfirmaSenha()) || clienteDto.getSenha().isEmpty()
                    || clienteDto.getConfirmaSenha().isEmpty()) {
                // Adicione um erro ao BindingResult
                bindingResult.rejectValue("confirmaSenha", "error.clienteDto", "As senhas não coincidem");
                return "clientes/EditarCliente";
            }

            // Configurar atributos de usuarioDto para usuario
            cliente.setNome(clienteDto.getNome());
            cliente.setSenha(clienteDto.getSenha());
            cliente.setDataNascimento(clienteDto.getDataNascimento());
            cliente.setGenero(clienteDto.getGenero());

            // Encriptar a senha usando o Bcrypt
            String senhaEncriptada = this.passwordEncoder.encode(clienteDto.getSenha());
            cliente.setSenha(senhaEncriptada);

            // Salvar cliente no repositório
            repo.save(cliente);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        // Redirecionar para a lista de clientes após a edição bem-sucedida
        return "redirect:/login";
    }

    @GetMapping("/PerfilCliente")
    public String acessaPerfil(Model model, HttpServletRequest request) {

        HttpSession session = request.getSession();
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        if (clienteLogado != null) {
            model.addAttribute("usuarioLogado", true);
            model.addAttribute("clienteId", clienteLogado.getId());
            model.addAttribute("nomeCliente", clienteLogado.getNome());

        } else {
            model.addAttribute("usuarioLogado", false);
            return "redirect:/login";

        }

        return "clientes/PerfilCliente";
    }

    @GetMapping("/endereco")
    public String mostrarEndereco(Model model, @RequestParam int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Cliente cliente = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Cria um ClienteDto e define os endereços
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setEnderecos(cliente.getEnderecos());
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        EnderecoDto enderecoDto = new EnderecoDto();

        if (clienteLogado != null) {
            model.addAttribute("usuarioLogado", true);
            model.addAttribute("clienteId", clienteLogado.getId());
            model.addAttribute("nomeCliente", clienteLogado.getNome());
            model.addAttribute("enderecoDto", enderecoDto);
            model.addAttribute("cliente", cliente);
            model.addAttribute("clienteDto", clienteDto);

        } else {
            model.addAttribute("usuarioLogado", false);
        }

        // Adiciona o cliente e o ClienteDto ao modelo

        return "enderecos/EnderecosCliente";
    }

    @PostMapping("/endereco/add")
    public String adicionarEndereco(Model model, @ModelAttribute("enderecoDto") @Valid EnderecoDto enderecoDto,
                                    BindingResult bindingResult, HttpSession session, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("enderecoDto", enderecoDto);
            return "redirect:" + request.getHeader("Referer");
        }

        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado == null) {
            return "redirect:/login";
        }

        // Cria um novo endereço e configura seus detalhes
        Endereco novoEndereco = new Endereco();
        novoEndereco.setEndereco("ENTREGA");
        novoEndereco.setCep(enderecoDto.getCep());
        novoEndereco.setLogradouro(enderecoDto.getLogradouro());
        novoEndereco.setNumero(enderecoDto.getNumero());
        novoEndereco.setComplemento(enderecoDto.getComplemento());
        novoEndereco.setBairro(enderecoDto.getBairro());
        novoEndereco.setCidade(enderecoDto.getCidade());
        novoEndereco.setUf(enderecoDto.getUf());
        novoEndereco.setCliente(clienteLogado); // Associa o endereço ao cliente logado

        // Salve o novo endereço no repositorio de endereços
        repository.save(novoEndereco);

        // Redireciona de volta para a pagina de perfil do cliente apos adicionar o
        // endereço com sucesso

        return "redirect:/clientes/PerfilCliente?returnUrl=/clientes/PerfilCliente";

    }

    @PostMapping("/atualizarStatus")
    public String atualizaStatus(@RequestParam int id, @ModelAttribute Endereco endereco) {
        Endereco endereco2 = repository.findById(id).orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        // altera o status do usuario
        endereco2.setStatus("ATIVO".equals(endereco2.getStatus()) ? "INATIVO" : "ATIVO");

        repository.save(endereco2);
        return "redirect:/clientes/PerfilCliente?returnUrl=/clientes/PerfilCliente";
    }

    @PostMapping("/definePadrao")
    public String defineEnderecoPadrao(@RequestParam int id, HttpSession session) {
        Endereco endereco = repository.findById(id).orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        // Atualizar o endereço atual como o padrão no cliente
        clienteLogado.setEnderecoPadrao(endereco);
        repo.save(clienteLogado);

        return "redirect:/clientes/PerfilCliente?returnUrl=/clientes/PerfilCliente";
    }

    @GetMapping("/pedidos")
    public String mostrarPedidos(Model model, @RequestParam int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Cliente cliente = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // Obter os pedidos do cliente
        List<Pedido> pedidos = pedidoRepository.findByCliente(cliente);

        // Verificar se o cliente está logado
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");
        if (clienteLogado != null) {
            model.addAttribute("usuarioLogado", true);
            model.addAttribute("clienteId", clienteLogado.getId());
            model.addAttribute("nomeCliente", clienteLogado.getNome());
            model.addAttribute("cliente", cliente);
            model.addAttribute("pedidos", pedidos);

        } else {
            model.addAttribute("usuarioLogado", false);
            return "redirect:/login";
        }
        List<List<ItemPedido>> detalhesProdutos = new ArrayList<>();
        for (Pedido pedido : pedidos) {
            List<ItemPedido> itensPedido = pedido.getItens();
            detalhesProdutos.add(itensPedido);
        }
        model.addAttribute("detalhesProdutos", detalhesProdutos);

        // Adicionar os pedidos ao modelo

        return "clientes/PedidoClientes";
    }

    @GetMapping("/detalhes/{pedidoId}")
    public String mostrarDetalhesPedido(Model model, @PathVariable int pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        model.addAttribute("pedido", pedido);

        return "clientes/DetalhesPedido";
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