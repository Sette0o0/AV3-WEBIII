package com.autobots.automanager.representacoes;

import org.springframework.hateoas.RepresentationModel;
import com.autobots.automanager.entitades.Usuario;

public class PessoaRecurso extends RepresentationModel<PessoaRecurso> {
    private final Usuario usuario;

    public PessoaRecurso(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
