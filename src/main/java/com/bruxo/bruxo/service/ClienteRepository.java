package com.bruxo.bruxo.service;

import java.util.Optional;

import com.bruxo.bruxo.models.Cliente;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByEmail(String email);
    boolean existsByEmail(String email);

}