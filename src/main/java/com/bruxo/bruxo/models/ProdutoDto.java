package com.bruxo.bruxo.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

public class ProdutoDto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    private int qtd_estoque =1;

    @Size(min = 10, message = "A descrição precisa ter pelo menos 10 caracteres")
    @Size(max = 2000, message = "A descrição não pode exceder 2000 caracteres")
    private String descricao;

    private MultipartFile arquivo_imagem ;

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

    public MultipartFile getArquivo_imagem() {
        return arquivo_imagem;
    }

    public void setArquivo_imagem(MultipartFile arquivo_imagem) {
        this.arquivo_imagem = arquivo_imagem;
    }


}