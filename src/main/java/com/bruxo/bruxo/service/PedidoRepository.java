package com.bruxo.bruxo.service;

import com.bruxo.bruxo.models.Cliente;
import com.bruxo.bruxo.models.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByCliente(Cliente cliente);
}
