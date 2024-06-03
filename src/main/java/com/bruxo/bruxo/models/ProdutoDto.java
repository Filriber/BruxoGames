package com.bruxo.bruxo.models;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class ProdutoDto {

    private int id;

    @NotEmpty(message = "O campo de nome não pode estar vazio")
    @Size(max = 200, message = "Você excedeu 200 caracteres")
    private String nome;

    @Max(5)
    @NotEmpty(message = "O campo de avaliação não pode estar vazio")
    private String avaliacao;

    private String status = "Ativo";

    @NotNull(message = "Defina um valor")
    private BigDecimal preco;

    @NotNull(message = "Defina uma quantidade para o estoque")
    private int qtd_estoque = 1;

    @Size(min = 10, message = "A descrição precisa ter pelo menos 10 caracteres")
    @Size(max = 2000, message = "A descrição não pode exceder 2000 caracteres")
    private String descricao;

    @NotNull(message = "Seleciona pelos uma imagem")
    @Size(min = 1, message = "Selecione pelo menos uma imagem")
    private List<MultipartFile> imagens;

    public List<MultipartFile> getImagens() {
        return imagens;
    }

    @Column(name = "caminho_imagem_padrao")
    private String caminhoImagemPadrao;
    // Novo campo para armazenar os nomes das imagens removidas
    @Getter
    private List<String> imagensRemovidas;
    private String imagemPadrao;


    @NotEmpty(message = "O campo de nome não pode estar vazio")
    @Size(max = 50, message = "Você excedeu 50 caracteres")
    private String marca;

    public String getImagemPadrao() {
        return imagemPadrao != null ? imagemPadrao : "";
    }

    public void setImagemPadrao(String imagemPadrao) {
        this.imagemPadrao = imagemPadrao;
    }
    // Getters e setters para os campos existentes...

    public void setImagensRemovidas(List<String> imagensRemovidas) {
        this.imagensRemovidas = imagensRemovidas;
    }

    public String getCaminhoImagemPadrao() {
        return caminhoImagemPadrao;
    }

    public void setCaminhoImagemPadrao(String caminhoImagemPadrao) {
        this.caminhoImagemPadrao = caminhoImagemPadrao;
    }

    public void setImagens(List<MultipartFile> imagens) {
        this.imagens = imagens;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(String avaliacao) {
        this.avaliacao = avaliacao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        this.preco = preco;
    }

    public int getQtd_estoque() {
        return qtd_estoque;
    }

    public void setQtd_estoque(int qtd_estoque) {
        this.qtd_estoque = qtd_estoque;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

}