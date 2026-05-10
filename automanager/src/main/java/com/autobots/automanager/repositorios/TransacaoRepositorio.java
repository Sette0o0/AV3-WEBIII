package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobots.automanager.entitades.Venda;

public interface TransacaoRepositorio extends JpaRepository<Venda, Long> {}
