package com.bruxo.bruxo.controllers;

import com.bruxo.bruxo.models.Produto;
import com.bruxo.bruxo.models.ProdutoDto;
import com.bruxo.bruxo.service.ProdutoRepository;

import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/templates/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository repo;

    @GetMapping({"", "/"})
    public String showProdutosList(Model model) {
        List<Produto> produtos = repo.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("templates/produtos", produtos);
        return "templates/produtos/index";
    }

    @GetMapping("/create")
    public String showCriaProduto(Model model) {
        ProdutoDto produtoDto = new ProdutoDto();
        model.addAttribute("produtoDto", produtoDto);
        return "templates/produtos/CriaProduto";
    }

    @PostMapping("/create")
    public String criarProduto(@ModelAttribute("produtoDto") @Valid ProdutoDto produtoDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Se houver erros de validação, retorne para o formulário de registro
            return "templates/produtos/CriaProduto";
        }

        // Mapear produtoDto para a entidade produto
        Produto produto = new Produto();

        produto.setNome(produtoDto.getNome());
        produto.setAvaliacao(produtoDto.getAvaliacao());
        produto.setPreco(produtoDto.getPreco());
        produto.setQtd_estoque(produtoDto.getQtd_estoque());
        produto.setDescricao(produtoDto.getDescricao());
        produto.setStatus(produtoDto.getStatus());
        // Configurar atributos de produtoDto para produto

        // Salvar produto no repositório
        repo.save(produto);

        // Redirecionar para a lista de usuários após a criação bem-sucedida
        return "redirect:/produtos";
    }

    @GetMapping("/edit")
    public String MostraEdicao(Model model, @RequestParam int id) {

        try {
            Produto produto = repo.findById(id).get();
            model.addAttribute("produto", produto);

            ProdutoDto produtoDto = new ProdutoDto();
            produtoDto.setId(produto.getId());
            produtoDto.setNome(produto.getNome());
            produtoDto.setAvaliacao(produto.getAvaliacao());
            produtoDto.setPreco(produto.getPreco());
            produtoDto.setQtd_estoque(produto.getQtd_estoque());
            produtoDto.setDescricao(produto.getDescricao());
            produtoDto.setStatus(produto.getStatus());

            model.addAttribute("produtoDto", produtoDto);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/produtos";
        }
        return "templates/produtos/EditarProduto";
    }



    @PostMapping("/edit")
    public String editarProduto(Model model, Principal principal, @RequestParam int id, @Valid @ModelAttribute ProdutoDto produtoDto, BindingResult bindingResult) {

        try {
            Produto produto = repo.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            model.addAttribute("produto", produto);

            if (bindingResult.hasErrors()) {
                // Se houver erros de validação, retorne para o formulário de edição
                return "templates/produtos/EditarProduto";
            }

            // Configurar atributos de produtoDto para produto
            produto.setNome(produtoDto.getNome());
            produto.setAvaliacao(produtoDto.getAvaliacao());
            produto.setPreco(produtoDto.getPreco());
            produto.setQtd_estoque(produtoDto.getQtd_estoque());
            produto.setDescricao(produtoDto.getDescricao());

            // Salvar produto no repositório
            repo.save(produto);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }

        // Redirecionar para a lista de usuários após a edição bem-sucedida
        return "redirect:/produtos";
    }



    @PostMapping("/atualizarStatus")
    public String atualizaStatus(@RequestParam int id, @ModelAttribute ProdutoDto produtoDto) {
        Produto produto = repo.findById(id).orElseThrow(() -> new RuntimeException("produto não foi encontrado"));

        //altera o status do produto
        produto.setStatus("Ativo".equals(produto.getStatus()) ? "Inativo" : "Ativo");
        //se o status for ativo, se for true, altera para inativo, caso contrario altera para ativo

        repo.save(produto);
        return "redirect:/produtos";
    }

}