package com.autobots.automanager.representacoes;

import org.springframework.hateoas.RepresentationModel;
import com.autobots.automanager.entitades.Venda;

public class TransacaoRecurso extends RepresentationModel<TransacaoRecurso> {
    private final Venda venda;

    public TransacaoRecurso(Venda venda) {
        this.venda = venda;
    }

    public Venda getVenda() {
        return venda;
    }
}
