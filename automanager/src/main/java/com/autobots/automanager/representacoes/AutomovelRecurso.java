package com.autobots.automanager.representacoes;

import org.springframework.hateoas.RepresentationModel;
import com.autobots.automanager.entitades.Veiculo;

public class AutomovelRecurso extends RepresentationModel<AutomovelRecurso> {
    private final Veiculo veiculo;

    public AutomovelRecurso(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }
}
