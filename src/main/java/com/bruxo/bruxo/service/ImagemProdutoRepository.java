package com.bruxo.bruxo.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bruxo.bruxo.models.ImagemProduto;

public interface ImagemProdutoRepository extends
        JpaRepository<ImagemProduto, Integer> {

    Object findByProduto_IdAndNomeArquivo(int produtoId, String nomeArquivo);
    
}
