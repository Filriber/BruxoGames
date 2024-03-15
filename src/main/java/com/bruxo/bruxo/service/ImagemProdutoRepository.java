package com.bruxo.bruxo.service;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import com.bruxo.bruxo.models.ImagemProduto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagemProdutoRepository extends
        JpaRepository<ImagemProduto, Integer> {

    Optional<ImagemProduto> findByProduto_IdAndNomeArquivo(int produtoId, String nomeArquivo);

}