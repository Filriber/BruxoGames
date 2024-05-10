package com.bruxo.bruxo.controllers;

import com.bruxo.bruxo.models.Cliente;
import com.bruxo.bruxo.models.Produto;
import com.bruxo.bruxo.service.ProdutoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
    @RequestMapping("/home")
    public class HomeController {

        @Autowired
        private ProdutoRepository repo;


        @GetMapping({"", "/"})
        public String home(HttpServletRequest request, Model model){
            HttpSession session = request.getSession();
            Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");


            if (clienteLogado != null) {
                model.addAttribute("usuarioLogado", true);
                model.addAttribute("clienteId", clienteLogado.getId());
                model.addAttribute("nomeCliente", clienteLogado.getNome());

            } else {
                model.addAttribute("usuarioLogado", false);
            }
            List<Produto> produtos = repo.findAll();

            model.addAttribute("produtos", produtos);
            return "home/index";
        }

        @GetMapping("/pagina")
        public String getHome() {
            return "homes/pagina";
        }
        @GetMapping("/produto/detalhes")
        public String mostrarDetalhesProduto(@PathVariable Long id, Model model) {
            Optional<Produto> optionalProduto = repo.findById(id.intValue());

            if (optionalProduto.isPresent()) {
                Produto produto = optionalProduto.get();
                model.addAttribute("produto", produto);
                return "produto/detalhes";
            } else {
                // Se o produto não existir, você pode redirecionar para uma página de erro ou página inicial
                return "redirect:/home";
            }
        }
    }

