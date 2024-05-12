package com.bruxo.bruxo.controllers;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

import com.bruxo.bruxo.models.*;
import com.bruxo.bruxo.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private FormaDePagamentoRepository formaDePagamentoRepository;
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @GetMapping
    public String mostrarCarrinho(HttpSession session, Model model, HttpServletRequest request) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");

        HttpSession session2 = request.getSession();
        Cliente clienteLogado = (Cliente) session2.getAttribute("clienteLogado");

        if (clienteLogado != null) {
            model.addAttribute("usuarioLogado", true);
            model.addAttribute("clienteId", clienteLogado.getId());
            model.addAttribute("nomeCliente", clienteLogado.getNome());

            Endereco enderecoCliente = clienteLogado.getEnderecoPadrao();
            if (enderecoCliente != null) {
                // Adicionar as informações do endereço ao modelo
                model.addAttribute("enderecoCliente", enderecoCliente);
            } else {
                model.addAttribute("enderecoCliente", "Endereço não encontrado");
            }

        } else {
            model.addAttribute("usuarioLogado", false);

        }

        // SE NAO EXISTIR CARRINHO, CRIA UM NOVO
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute("carrinho", carrinho);
        }

        // Adicione o carrinho ao modelo para exibição na página
        model.addAttribute("carrinho", carrinho);
        return "carrinho";
    }

    @PostMapping("/adicionar")
    public String adicionarProdutoAoCarrinho(@RequestParam int produtoId, @RequestParam int quantidade,
                                             HttpSession session) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // CRIA O CARRINHO NA SESSÃO DO SITE SE NÃO EXISTIR
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute("carrinho", carrinho);
        }

        // ADICIONA PRODUTO AO CARRINHO
        carrinho.adicionarItem(produto, quantidade);

        // REDIRECIONA PARA PAGINA DO CARRINHO
        return "redirect:/carrinho";
    }

    @PostMapping("/remove")
    public String removeProduto(@RequestParam int produtoId, HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            session.setAttribute("carrinho", carrinho);
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        // ADICIONA PRODUTO AO CARRINHO
        carrinho.removerItem(produto);

        if (carrinho.getItens().isEmpty()) {
            // Resetar o valor do frete para zero
            carrinho.setFrete(BigDecimal.ZERO);
        }
        // REDIRECIONA PARA PAGINA DO CARRINHO
        return "redirect:/carrinho";
    }

    @PostMapping("/aumentar")
    public String aumentarQuantidade(@RequestParam int produtoId, HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            session.setAttribute("carrinho", carrinho);
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        carrinho.aumentaQuantidade(produto);

        return "redirect:/carrinho";
    }

    @PostMapping("/diminuir")
    public String diminuirQuantidade(@RequestParam int produtoId, HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            session.setAttribute("carrinho", carrinho);
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        carrinho.diminuiQuantidade(produto);

        return "redirect:/carrinho";
    }

    @PostMapping("/adicionarFrete")
    public String adicionarFrete(@RequestParam BigDecimal frete, HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute("carrinho", carrinho);
        }
        // Define o valor do frete no carrinho
        carrinho.setFrete(frete);

        return "redirect:/carrinho";
    }

    @GetMapping("/checkout")
    public String finalizarPedido(HttpSession session, Model model, HttpServletRequest request) {

        Cliente clienteLogado = (Cliente) request.getSession().getAttribute("clienteLogado");

        if (clienteLogado != null) {
            Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
            Endereco enderecoCliente = clienteLogado.getEnderecoPadrao();

            model.addAttribute("carrinho", carrinho);
            model.addAttribute("usuarioLogado", true);
            model.addAttribute("clienteId", clienteLogado.getId());
            model.addAttribute("nomeCliente", clienteLogado.getNome());
            model.addAttribute("enderecoCliente", enderecoCliente);
            model.addAttribute("formaDePagamento", carrinho.getFormaDePagamento());

            return "checkout/checkout";
        } else {
            return "redirect:/login";
        }
    }

    @GetMapping("/entrega")
    public String finalizarEntrega(Model model, @RequestParam(required = false) Integer id,
                                   HttpServletRequest request) {
        HttpSession session = request.getSession();
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");


        if (id == null || clienteLogado == null) {
            return "redirect:/login";
        }

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setEnderecos(cliente.getEnderecos());
        EnderecoDto enderecoDto = new EnderecoDto();


        model.addAttribute("usuarioLogado", true);
        model.addAttribute("clienteId", clienteLogado.getId());
        model.addAttribute("nomeCliente", clienteLogado.getNome());
        model.addAttribute("enderecoDto", enderecoDto);
        model.addAttribute("cliente", cliente);
        model.addAttribute("clienteDto", clienteDto);

        return "checkout/entrega";
    }

    @PostMapping("/escolhe")
    public String defineEnderecoPadrao(@RequestParam int id, HttpSession session) {
        Endereco endereco = enderecoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Endereço não encontrado"));
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

        // Atualizar o endereço atual como o padrão no cliente
        clienteLogado.setEnderecoPadrao(endereco);
        clienteRepository.save(clienteLogado);

        return "checkout/pagamento";
    }

    @PostMapping("/escolhe/add")
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

        enderecoRepository.save(novoEndereco);

        // Recupera o cliente novamente para atualizar a lista de endereços apos
        // adicionar um novo endereço
        Cliente cliente = clienteRepository.findById(clienteLogado.getId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        ClienteDto clienteDto = new ClienteDto();
        clienteDto.setEnderecos(cliente.getEnderecos());

        // Adiciona os atributos do modelo novamente
        model.addAttribute("usuarioLogado", true);
        model.addAttribute("clienteId", clienteLogado.getId());
        model.addAttribute("nomeCliente", clienteLogado.getNome());
        model.addAttribute("enderecoDto", new EnderecoDto());
        model.addAttribute("cliente", cliente);
        model.addAttribute("clienteDto", clienteDto);

        return "checkout/entrega";
    }

    @PostMapping("/checkout/processar")
    public String processarPagamento(@ModelAttribute("pagamentoDto") @Valid PagamentoDto pagamentoDto,
                                     BindingResult bindingResult,
                                     HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "pagamento";
        }

        // Recupera o cliente logado da sessao
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        System.out.println("Cliente: " + cliente.getNome());

        if (cliente != null) {

            // Cria uma instancia FormasDePagamento e associe ao cliente

            FormasDePagamento novaFormaDePagamento = new FormasDePagamento();
            novaFormaDePagamento.setTipo(pagamentoDto.getTipo());
            novaFormaDePagamento.setNumeroCartao(pagamentoDto.getNumeroCartao());
            novaFormaDePagamento.setCodigoVerificador(pagamentoDto.getCodigoVerificador());
            novaFormaDePagamento.setNomeCompleto(pagamentoDto.getNomeCompleto());
            novaFormaDePagamento.setDataVencimento(pagamentoDto.getDataVencimento());
            novaFormaDePagamento.setQuantidadeParcelas(pagamentoDto.getQuantidadeParcelas());

            // Associa a forma de pagamento ao cliente
            novaFormaDePagamento.setCliente(cliente);

            // Salva a forma de pagamento no banco de dados
            formaDePagamentoRepository.save(novaFormaDePagamento);

            // Adiciona a forma de pagamento do carrinho na sessão
            Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
            carrinho.setFormaDePagamento(novaFormaDePagamento);
        }

        return "redirect:/carrinho/checkout";
    }

    @PostMapping("/checkout/finalizar")
    public String finalizarPedido(HttpSession session, Model model) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        Cliente cliente = (Cliente) session.getAttribute("clienteLogado");
        String mensagemAviso;

        try {
            Pedido pedido = new Pedido();
            pedido.setCliente(cliente);
            pedido.setEnderecoEntrega(cliente.getEnderecoPadrao());
            pedido.setFormaPagamento(carrinho.getFormaDePagamento());
            pedido.setValorTotal(carrinho.calcularTotal());
            pedido.setStatus("AGUARDANDO PAGAMENTO");
            pedido.setNumeroSequencial(gerarNumeroSequencial());
            pedido.setDataPedido(LocalDate.now());

            pedidoRepository.save(pedido);

            for (ItemPedido itemCarrinho : carrinho.getItens()) {
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setPedido(pedido);
                itemPedido.setProduto(itemCarrinho.getProduto());
                itemPedido.setQuantidade(itemCarrinho.getQuantidade());
                itemPedidoRepository.save(itemPedido);
            }

            carrinho.limpar();

            mensagemAviso = "Pedido gravado com sucesso! Número do pedido: " + pedido.getNumeroSequencial()
                    + ", Valor total: R$ " + pedido.getValorTotal();
        } catch (Exception e) {
            mensagemAviso = "Erro ao salvar o pedido: " + e.getMessage();
        }

        model.addAttribute("confirmationMessage", mensagemAviso);

        return "/clientes/confirmacao";
    }
    private String gerarNumeroSequencial() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(random.nextInt(10)); // Adiciona um numero aleatorio de 0 a 9
        }
        return sb.toString();
    }

}