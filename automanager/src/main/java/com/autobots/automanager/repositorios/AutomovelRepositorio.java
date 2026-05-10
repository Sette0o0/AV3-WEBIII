package com.autobots.automanager.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import com.autobots.automanager.entitades.Veiculo;

public interface AutomovelRepositorio extends JpaRepository<Veiculo, Long> {}
