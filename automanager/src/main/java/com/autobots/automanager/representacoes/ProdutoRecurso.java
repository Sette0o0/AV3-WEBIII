package com.autobots.automanager.representacoes;

import org.springframework.hateoas.RepresentationModel;
import com.autobots.automanager.entitades.Mercadoria;

public class ProdutoRecurso extends RepresentationModel<ProdutoRecurso> {
    private final Mercadoria mercadoria;

    public ProdutoRecurso(Mercadoria mercadoria) {
        this.mercadoria = mercadoria;
    }

    public Mercadoria getMercadoria() {
        return mercadoria;
    }
}
