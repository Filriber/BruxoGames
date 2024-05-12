package com.bruxo.bruxo.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bruxo.bruxo.models.Cliente;
import com.bruxo.bruxo.models.Pedido;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByCliente(Cliente cliente);
}
