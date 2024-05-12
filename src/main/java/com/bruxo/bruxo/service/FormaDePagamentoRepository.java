package com.bruxo.bruxo.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bruxo.bruxo.models.FormasDePagamento;

public interface FormaDePagamentoRepository extends JpaRepository <FormasDePagamento, Integer>{

}