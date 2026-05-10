package com.autobots.automanager.representacoes;

import org.springframework.hateoas.RepresentationModel;
import com.autobots.automanager.entitades.Servico;

public class AtividadeRecurso extends RepresentationModel<AtividadeRecurso> {
    private final Servico servico;

    public AtividadeRecurso(Servico servico) {
        this.servico = servico;
    }

    public Servico getServico() {
        return servico;
    }
}
