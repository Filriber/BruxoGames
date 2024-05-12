package com.bruxo.bruxo.service;

import com.bruxo.bruxo.models.Produto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    List<Produto> findByMarca(String marca);

}