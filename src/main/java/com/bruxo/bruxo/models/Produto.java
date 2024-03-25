package com.bruxo.bruxo.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Produtos")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nome;

    private String avaliacao;

    private String status;

    private BigDecimal preco;

    private int qtd_estoque;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ElementCollection
    @CollectionTable(name="Produto_Imagens", joinColumns =@JoinColumn(name = "produto_id"))
    @Column(name = "imagem")
    private List<String> imagens = new ArrayList<>();

    @Column(name = "imagem_padrao")
    private String imagemPadrao;

    public List<String> getImagens() {
        return imagens;
    }

    public void setImagens(List<String> imagens) {
        this.imagens = imagens;
    }

    public String getImagemPadrao() {
        return imagemPadrao;
    }

    public void setImagemPadrao(String imagemPadrao) {
        this.imagemPadrao = imagemPadrao;
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

}