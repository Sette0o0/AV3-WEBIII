package com.autobots.automanager.entitades;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
public class CredencialUsuarioSenha extends Credencial {
    @Column(nullable = false, unique = true)
    private String nomeUsuario;
    @Column(nullable = false)
    private String senha;
}