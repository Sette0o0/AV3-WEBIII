package com.autobots.automanager.representacoes;

import org.springframework.hateoas.RepresentationModel;
import com.autobots.automanager.entitades.Empresa;

public class EmpresaRecurso extends RepresentationModel<EmpresaRecurso> {
    private final Empresa empresa;

    public EmpresaRecurso(Empresa empresa) {
        this.empresa = empresa;
    }

    public Empresa getEmpresa() {
        return empresa;
    }
}
