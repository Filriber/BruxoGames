package com.bruxo.bruxo.controllers;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bruxo.bruxo.models.Carrinho;
import com.bruxo.bruxo.models.Cliente;
import com.bruxo.bruxo.models.Produto;
import com.bruxo.bruxo.service.ProdutoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/carrinho")
public class CarrinhoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @GetMapping
    public String mostrarCarrinho(HttpSession session, Model model, HttpServletRequest request) {
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");

        HttpSession session2 = request.getSession();
        Cliente clienteLogado = (Cliente) session2.getAttribute("clienteLogado");

        if (clienteLogado != null) {
            model.addAttribute("usuarioLogado", true);
            model.addAttribute("clienteId", clienteLogado.getId());
            model.addAttribute("nomeCliente", clienteLogado.getNome());

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

    @PostMapping("/adicionarComFrete")
    public String adicionarProdutoAoCarrinhoComFrete(@RequestParam int produtoId, @RequestParam int quantidade,
                                                     @RequestParam double valorFrete, HttpSession session, Model model) {

        // Armazena o valor do frete na sessão
        session.setAttribute("valorFrete", valorFrete);

        // Obtém o produto a ser adicionado ao carrinho
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        // Cria o carrinho na sessão do site se não existir
        Carrinho carrinho = (Carrinho) session.getAttribute("carrinho");
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute("carrinho", carrinho);
        }

        // Adiciona o produto ao carrinho
        carrinho.adicionarItem(produto, quantidade);

        // Converte o valor do frete para BigDecimal
        BigDecimal valorFreteBigDecimal = BigDecimal.valueOf(valorFrete);

        // Adiciona o valor do frete ao total do carrinho
        carrinho.adicionarFrete(valorFreteBigDecimal);

        // Adiciona o valor do frete ao modelo
        model.addAttribute("valorFrete", valorFreteBigDecimal);

        // Redireciona para a página do carrinho após somar o valor do frete ao total do
        // carrinho
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
}