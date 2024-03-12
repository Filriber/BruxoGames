package com.bruxo.bruxo.models;

import org.springframework.web.multipart.MultipartFile;


public class ImagemProdutoDto {
    
    private Integer id;
    
    private String nomeArquivo;
    
    private MultipartFile arquivo;
    
    private int ordenacao;
    
    private boolean principal;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public MultipartFile getArquivo() {
        return arquivo;
    }

    public void setArquivo(MultipartFile arquivo) {
        this.arquivo = arquivo;
    }

    public int getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(int ordenacao) {
        this.ordenacao = ordenacao;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }
}