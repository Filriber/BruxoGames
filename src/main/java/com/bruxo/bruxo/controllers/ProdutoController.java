package com.bruxo.bruxo.controllers;

import com.bruxo.bruxo.models.Cliente;
import com.bruxo.bruxo.models.Produto;
import com.bruxo.bruxo.models.ProdutoDto;
import com.bruxo.bruxo.service.ProdutoRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    @Autowired
    private ProdutoRepository repo;

    @GetMapping({ "", "/" })
    public String showProdutosList(Model model, HttpSession session, @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size) {
        Page<Produto> produtosPage = repo.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
        model.addAttribute("produtos", produtosPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", produtosPage.getTotalPages());

        String grupoUsuario = (String) session.getAttribute("grupo");
        model.addAttribute("grupoUsuario", grupoUsuario);

        return "produtos/index";
    }

    @GetMapping("/view/{id}")
    public String showProductDetails(@PathVariable int id, Model model, HttpServletRequest request) {
        try {
            // Buscar o produto no banco de dados pelo ID
            Produto produto = repo.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));
            HttpSession session = request.getSession();
            Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");

            if (clienteLogado != null) {
                model.addAttribute("usuarioLogado", true);
                model.addAttribute("clienteId", clienteLogado.getId());
                model.addAttribute("nomeCliente", clienteLogado.getNome());

            } else {
                model.addAttribute("usuarioLogado", false);
            }
            // Mapear os atributos do produto para o DTO
            ProdutoDto produtoDto = new ProdutoDto();
            produtoDto.setId(produto.getId());
            produtoDto.setNome(produto.getNome());
            produtoDto.setAvaliacao(produto.getAvaliacao());
            produtoDto.setPreco(produto.getPreco());
            produtoDto.setQtd_estoque(produto.getQtd_estoque());
            produtoDto.setDescricao(produto.getDescricao());
            produtoDto.setStatus(produto.getStatus());

            // Adicionar o produto e o DTO ao modelo
            model.addAttribute("produto", produto);
            model.addAttribute("produtoDto", produtoDto);
            model.addAttribute("tamanhos", produto.getTamanhos());

            List<String> imagens = produto.getImagens();
            model.addAttribute("imagens", imagens);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/produtos";
        }
        return "produtos/VisualizarProduto";
    }

    @GetMapping("/create")
    public String showCriaProduto(Model model) {
        ProdutoDto produtoDto = new ProdutoDto();
        model.addAttribute("produtoDto", produtoDto);
        return "produtos/CriaProduto";
    }

    @PostMapping("/create")
    public String criarProduto(@ModelAttribute("produtoDto") @Valid ProdutoDto produtoDto, BindingResult bindingResult,
                               Model model) {
        if (bindingResult.hasErrors()) {
            // Se houver erros de validação, retorne para o formulário de registro
            return "produtos/CriaProduto";
        }

        if (produtoDto.getImagens() == null || produtoDto.getImagens().stream().allMatch(MultipartFile::isEmpty)) {
            bindingResult.rejectValue("imagens", "error.produto", "É necessário selecionar pelo menos uma imagem.");
            return "produtos/CriaProduto";
        }

        List<String> imagensSalvas = new ArrayList<>();
        for (MultipartFile imagem : produtoDto.getImagens()) {
            String nomeArquivo = UUID.randomUUID().toString() + "_" + imagem.getOriginalFilename();

            // Diretório de imagens dentro do projeto
            File diretorioImagens = new File("src/main/resources/static/imagens_produtos");

            // verifica se o diretório imagens_produtos existe
            if (!diretorioImagens.exists()) {
                // se não existir, ele cria
                if (diretorioImagens.mkdirs()) {
                    System.out.println("Diretório " + diretorioImagens.getAbsolutePath() + " foi criado.");
                } else {
                    System.out.println("Falha ao criar o diretório " + diretorioImagens.getAbsolutePath());
                    // Lidar com a falha ao criar o diretório, se necessário
                }
            }

            try {
                String caminhoApp = new File("").getAbsolutePath();
                Path uploadPath = Paths.get(caminhoApp, "src/main/resources/static/imagens_produtos");
                Path filePath = uploadPath.resolve(nomeArquivo);
                Files.copy(imagem.getInputStream(), filePath);
                imagensSalvas.add(nomeArquivo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Definição da primeira imagem como padrão
        String imagemPadrao = imagensSalvas.isEmpty() ? null : imagensSalvas.get(0);

        // Mapear produtoDto para a entidade produto
        Produto produto = new Produto();

        produto.setNome(produtoDto.getNome());
        produto.setAvaliacao(produtoDto.getAvaliacao());
        produto.setPreco(produtoDto.getPreco());
        produto.setQtd_estoque(produtoDto.getQtd_estoque());
        produto.setDescricao(produtoDto.getDescricao());
        produto.setStatus(produtoDto.getStatus());
        produto.setImagens(imagensSalvas);
        produto.setImagemPadrao(imagemPadrao);
        produto.setMarca(produtoDto.getMarca());
        // Configurar atributos de produtoDto para produto

        // Salvar produto no repositório
        repo.save(produto);

        // Redirecionar para a lista de usuários após a criação bem-sucedida
        return "redirect:/produtos";
    }

    @GetMapping("/edit")
    public String mostrarEdicao(Model model, @RequestParam int id, HttpSession session) {

        try {
            // Buscar o produto no banco de dados pelo ID
            Produto produto = repo.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            String grupoUsuario = (String) session.getAttribute("grupo");
            model.addAttribute("grupoUsuario", grupoUsuario);

            // Mapear os atributos do produto para o DTO
            ProdutoDto produtoDto = new ProdutoDto();
            produtoDto.setId(produto.getId());
            produtoDto.setNome(produto.getNome());
            produtoDto.setAvaliacao(produto.getAvaliacao());
            produtoDto.setPreco(produto.getPreco());
            produtoDto.setQtd_estoque(produto.getQtd_estoque());
            produtoDto.setDescricao(produto.getDescricao());
            produtoDto.setStatus(produto.getStatus());
            produtoDto.setMarca(produto.getMarca());

            // Adicionar o produto e o DTO ao modelo
            model.addAttribute("produto", produto);
            model.addAttribute("produtoDto", produtoDto);

            List<String> imagens = produto.getImagens();
            model.addAttribute("imagens", imagens);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/produtos";
        }
        return "produtos/EditarProduto";
    }

    @PostMapping("/edit")
    public String editarProduto(@ModelAttribute("produtoDto") @Valid ProdutoDto produtoDto, BindingResult bindingResult,
                                Model model) {
        if (bindingResult.hasErrors()) {
            // Se houver erros de validação, retornar para o form de edição
            return "produtos/EditarProduto";
        }
        try {
            // Buscar o produto no banco de dados pelo ID
            Produto produto = repo.findById(produtoDto.getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            // Atualizar os atributos do produto com base nos dados do DTO
            produto.setNome(produtoDto.getNome());
            produto.setAvaliacao(produtoDto.getAvaliacao());
            produto.setPreco(produtoDto.getPreco());
            produto.setQtd_estoque(produtoDto.getQtd_estoque());
            produto.setDescricao(produtoDto.getDescricao());
            produto.setStatus(produtoDto.getStatus());
            produto.setMarca(produtoDto.getMarca());

            if (produto.getImagens().size() == 1 && produtoDto.getImagensRemovidas() != null
                    && !produtoDto.getImagensRemovidas().isEmpty()) {
                bindingResult.rejectValue("imagensRemovidas", "error.produto",
                        "Não é permitido remover a única imagem associada ao produto.");
                return "produtos/EditarProduto";
            }

            if (produto.getImagens().isEmpty() && (produtoDto.getImagens() == null || produtoDto.getImagens().isEmpty()
                    || produtoDto.getImagens().stream().allMatch(MultipartFile::isEmpty))) {
                bindingResult.rejectValue("imagens", "error.produto", "Não é permitido deixar o produto sem imagem.");
                return "produtos/EditarProduto";
            }

            if (!produtoDto.getImagemPadrao().isEmpty()) {
                produto.setImagemPadrao(produtoDto.getImagemPadrao());
            }

            if (produtoDto.getImagensRemovidas() != null && !produtoDto.getImagensRemovidas().isEmpty()) {
                for (String nomeImagemRemovida : produtoDto.getImagensRemovidas()) {
                    // Remover imagem do banco de dados
                    produto.getImagens().remove(nomeImagemRemovida);

                    // Se a imagem removida for a imagem padrão, definir a proxima imagem como
                    // imagem padrão
                    if (nomeImagemRemovida.equals(produto.getImagemPadrao())) {
                        if (!produto.getImagens().isEmpty()) {
                            // Define a próxima imagem da lista como imagem padrão
                            produto.setImagemPadrao(produto.getImagens().get(0));
                        } else {
                            // Se não houver mais imagens na lista, defina a imagem padrão como vazia
                            produto.setImagemPadrao("");
                        }
                    }

                    // Excluir fisicamente a imagem do diretório
                    String diretorioImagens = "src/main/resources/static/imagens_produtos/";
                    Path imagemRemovidaPath = Paths.get(diretorioImagens + nomeImagemRemovida);
                    Files.deleteIfExists(imagemRemovidaPath);
                }
            }

            // Adicionar novas imagens, se houver
            List<MultipartFile> novasImagens = produtoDto.getImagens();
            if (novasImagens != null && !novasImagens.isEmpty()) {
                for (MultipartFile imagem : novasImagens) {
                    // Processar a imagem apenas se ela não estiver vazia
                    if (!imagem.isEmpty()) {
                        String nomeArquivo = UUID.randomUUID().toString() + "_" + imagem.getOriginalFilename();
                        try {
                            String diretorioImagens = "src/main/resources/static/imagens_produtos/";
                            Path uploadPath = Paths.get(diretorioImagens);
                            if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                            }
                            Path filePath = uploadPath.resolve(nomeArquivo);
                            Files.copy(imagem.getInputStream(), filePath);
                            produto.getImagens().add(nomeArquivo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            // Salvar o produto atualizado no banco de dados
            repo.save(produto);

            // Redirecionar para a lista de produtos após a edição bem-sucedida
            return "redirect:/produtos";
        } catch (Exception ex) {
            System.out.println("Erro ao editar o produto: " + ex.getMessage());
            return "redirect:/produtos"; // Ou outro tratamento de erro adequado
        }
    }

    @GetMapping("/editestoque")
    public String mostrarEdicaoEstoque(Model model, @RequestParam int id, HttpSession session) {

        try {
            // Buscar o produto no banco de dados pelo ID
            Produto produto = repo.findById(id).orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            String grupoUsuario = (String) session.getAttribute("grupo");
            model.addAttribute("grupoUsuario", grupoUsuario);

            // Mapear os atributos do produto para o DTO
            ProdutoDto produtoDto = new ProdutoDto();
            produtoDto.setId(produto.getId());
            produtoDto.setNome(produto.getNome());
            produtoDto.setAvaliacao(produto.getAvaliacao());
            produtoDto.setPreco(produto.getPreco());
            produtoDto.setQtd_estoque(produto.getQtd_estoque());
            produtoDto.setDescricao(produto.getDescricao());
            produtoDto.setStatus(produto.getStatus());
            produtoDto.setMarca(produto.getMarca());

            // Adicionar o produto e o DTO ao modelo
            model.addAttribute("produto", produto);
            model.addAttribute("produtoDto", produtoDto);

            List<String> imagens = produto.getImagens();
            model.addAttribute("imagens", imagens);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/produtos";
        }
        return "produtos/EditarProdutoEstoquista";
    }

    @PostMapping("/editestoque")
    public String editarProdutoEstoque(@ModelAttribute("produtoDto") @Valid ProdutoDto produtoDto,
                                       BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            // Se houver erros de validação, retornar para o formulário de edição
            return "produtos/EditarProdutoEstoquista";
        }
        try {
            // Buscar o produto no banco de dados pelo ID
            Produto produto = repo.findById(produtoDto.getId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            // Atualizar os atributos do produto com base nos dados do DTO
            produto.setNome(produtoDto.getNome());
            produto.setAvaliacao(produtoDto.getAvaliacao());
            produto.setPreco(produtoDto.getPreco());
            produto.setQtd_estoque(produtoDto.getQtd_estoque());
            produto.setDescricao(produtoDto.getDescricao());
            produto.setStatus(produtoDto.getStatus());
            produtoDto.setMarca(produto.getMarca());

            if (!produtoDto.getImagemPadrao().isEmpty()) {
                produto.setImagemPadrao(produtoDto.getImagemPadrao());
            }

            if (produtoDto.getImagensRemovidas() != null && !produtoDto.getImagensRemovidas().isEmpty()) {
                for (String nomeImagemRemovida : produtoDto.getImagensRemovidas()) {
                    // Remover imagem do banco de dados
                    produto.getImagens().remove(nomeImagemRemovida);

                    // Se a imagem removida for a imagem padrão, definir a próxima imagem como
                    // imagem padrão
                    if (nomeImagemRemovida.equals(produto.getImagemPadrao())) {
                        if (!produto.getImagens().isEmpty()) {
                            // Define a próxima imagem da lista como imagem padrão
                            produto.setImagemPadrao(produto.getImagens().get(0));
                        } else {
                            // Se não houver mais imagens na lista, defina a imagem padrão como vazia
                            produto.setImagemPadrao("");
                        }
                    }

                    // Excluir fisicamente a imagem do diretório
                    String diretorioImagens = "src/main/resources/static/imagens_produtos/";
                    Path imagemRemovidaPath = Paths.get(diretorioImagens + nomeImagemRemovida);
                    Files.deleteIfExists(imagemRemovidaPath);
                }
            }

            // Adicionar novas imagens, se houver
            List<MultipartFile> novasImagens = produtoDto.getImagens();
            if (novasImagens != null && !novasImagens.isEmpty()) {
                for (MultipartFile imagem : novasImagens) {
                    // Processar a imagem apenas se ela não estiver vazia
                    if (!imagem.isEmpty()) {
                        String nomeArquivo = UUID.randomUUID().toString() + "_" + imagem.getOriginalFilename();
                        try {
                            String diretorioImagens = "src/main/resources/static/imagens_produtos/";
                            Path uploadPath = Paths.get(diretorioImagens);
                            if (!Files.exists(uploadPath)) {
                                Files.createDirectories(uploadPath);
                            }
                            Path filePath = uploadPath.resolve(nomeArquivo);
                            Files.copy(imagem.getInputStream(), filePath);
                            produto.getImagens().add(nomeArquivo);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            // Salvar o produto atualizado no banco de dados
            repo.save(produto);

            // Redirecionar para a lista de produtos após a edição bem-sucedida
            return "redirect:/produtos";
        } catch (Exception ex) {
            System.out.println("Erro ao editar o produto: " + ex.getMessage());
            return "redirect:/produtos"; // Ou outro tratamento de erro adequado
        }
    }

    @PostMapping("/atualizarStatus")
    public String atualizaStatus(@RequestParam int id, @ModelAttribute ProdutoDto produtoDto) {
        Produto produto = repo.findById(id).orElseThrow(() -> new RuntimeException("produto não encontrado"));

        // altera o status do produto
        produto.setStatus("Ativo".equals(produto.getStatus()) ? "Inativo" : "Ativo");
        // se o status for ativo, se for true, altera para inativo, caso contrario
        // altera para ativo

        repo.save(produto);
        return "redirect:/produtos";
    }


    @GetMapping("buscamarca/{marca}")
    public String listaProdutosMarca(@PathVariable String marca, Model model, HttpServletRequest request){
        HttpSession session = request.getSession();
        Cliente clienteLogado = (Cliente) session.getAttribute("clienteLogado");


        if (clienteLogado != null) {
            model.addAttribute("usuarioLogado", true);
            model.addAttribute("clienteId", clienteLogado.getId());
            model.addAttribute("nomeCliente", clienteLogado.getNome());

        } else {
            model.addAttribute("usuarioLogado", false);
        }

        List<Produto>  produtos = repo.findByMarca(marca);
        model.addAttribute("produtos", produtos);

        return  ("home/produtosPorMarca");

    }

}