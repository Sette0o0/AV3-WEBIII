package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobots.automanager.entitades.Mercadoria;

public interface ProdutoRepositorio extends JpaRepository<Mercadoria, Long> {}
