package com.bruxo.bruxo.controllers;

import java.math.BigDecimal;

import com.bruxo.bruxo.models.*;
import com.bruxo.bruxo.service.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bruxo.bruxo.service.ProdutoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private ClienteRepository clienteRepository;

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
            // Se estiver logado, redireciona para tela de checkout
            Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
            Endereco enderecoCliente = clienteLogado.getEnderecoPadrao();

            model.addAttribute("carrinho", carrinho);
            model.addAttribute("usuarioLogado", true);
            model.addAttribute("clienteId", clienteLogado.getId());
            model.addAttribute("nomeCliente", clienteLogado.getNome());

            model.addAttribute("enderecoCliente", enderecoCliente);

            return "checkout/checkout";
        } else {
            // PAGINA DE LOGIN
            return "redirect:/login";
        }
    }

    @GetMapping("/entrega")
    public String finalizarEntrega(Model model, @RequestParam int id, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Cliente cliente = clienteRepository.findById(id)
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

            return "checkout/entrega";

        } else {
            return "redirect:/login";
        }
    }

}