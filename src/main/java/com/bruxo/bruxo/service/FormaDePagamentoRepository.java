package com.bruxo.bruxo.service;

import com.bruxo.bruxo.models.FormasDePagamento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormaDePagamentoRepository extends JpaRepository<FormasDePagamento, Integer> {

}