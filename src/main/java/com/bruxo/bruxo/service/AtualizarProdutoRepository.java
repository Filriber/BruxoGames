package com.bruxo.bruxo.service;

import com.bruxo.bruxo.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AtualizarProdutoRepository extends JpaRepository<Pedido, Long> {
}