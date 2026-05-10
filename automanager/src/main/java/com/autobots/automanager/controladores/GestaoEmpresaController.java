package com.autobots.automanager.controladores;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.Empresa;
import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.representacoes.EmpresaRecurso;
import com.autobots.automanager.repositorios.RepositorioEmpresa;
import com.autobots.automanager.repositorios.PessoaRepositorio;

@RestController
@RequestMapping("/empresa")
public class GestaoEmpresaController {

    @Autowired
    private RepositorioEmpresa repositorio;

    @Autowired
    private PessoaRepositorio pessoaRepositorio;

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaRecurso> buscarEmpresa(@PathVariable Long id) {
        return repositorio.findById(id)
                .map(empresa -> {
                    EmpresaRecurso recurso = new EmpresaRecurso(empresa);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GestaoEmpresaController.class).buscarEmpresa(id)).withSelfRel());
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GestaoEmpresaController.class).listarEmpresas()).withRel("empresas"));
                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EmpresaRecurso>> listarEmpresas() {
        List<EmpresaRecurso> recursos = repositorio.findAll().stream()
                .map(empresa -> {
                    EmpresaRecurso recurso = new EmpresaRecurso(empresa);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GestaoEmpresaController.class).buscarEmpresa(empresa.getId())).withSelfRel());
                    return recurso;
                })
                .collect(Collectors.toList());
        CollectionModel<EmpresaRecurso> colecao = CollectionModel.of(recursos,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GestaoEmpresaController.class).listarEmpresas()).withSelfRel());
        return ResponseEntity.ok(colecao);
    }

    @PostMapping
    public ResponseEntity<EmpresaRecurso> cadastrarEmpresa(@RequestBody Empresa empresa) {
        empresa.setCadastro(new Date());
        Empresa salva = repositorio.save(empresa);
        EmpresaRecurso recurso = new EmpresaRecurso(salva);
        recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GestaoEmpresaController.class).buscarEmpresa(salva.getId())).withSelfRel());
        recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GestaoEmpresaController.class).listarEmpresas()).withRel("empresas"));
        return ResponseEntity.status(HttpStatus.CREATED).body(recurso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaRecurso> atualizarEmpresa(@PathVariable Long id, @RequestBody Empresa dados) {
        return repositorio.findById(id)
                .map(empresa -> {
                    if (dados.getRazaoSocial() != null) empresa.setRazaoSocial(dados.getRazaoSocial());
                    if (dados.getNomeFantasia() != null) empresa.setNomeFantasia(dados.getNomeFantasia());
                    if (dados.getEndereco() != null) empresa.setEndereco(dados.getEndereco());
                    if (dados.getTelefones() != null && !dados.getTelefones().isEmpty())
                        empresa.getTelefones().addAll(dados.getTelefones());
                    Empresa atualizada = repositorio.save(empresa);
                    EmpresaRecurso recurso = new EmpresaRecurso(atualizada);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GestaoEmpresaController.class).buscarEmpresa(id)).withSelfRel());
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GestaoEmpresaController.class).listarEmpresas()).withRel("empresas"));
                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEmpresa(@PathVariable Long id) {
        if (!repositorio.existsById(id)) return ResponseEntity.notFound().build();
        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{empresaId}/usuario/{usuarioId}")
    public ResponseEntity<EmpresaRecurso> associarUsuario(
            @PathVariable Long empresaId, @PathVariable Long usuarioId) {
        return repositorio.findById(empresaId)
                .map(empresa -> pessoaRepositorio.findById(usuarioId)
                        .map(usuario -> {
                            empresa.getUsuarios().add(usuario);
                            Empresa salva = repositorio.save(empresa);
                            EmpresaRecurso recurso = new EmpresaRecurso(salva);
                            recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GestaoEmpresaController.class).buscarEmpresa(empresaId)).withSelfRel());
                            recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(GestaoEmpresaController.class).listarEmpresas()).withRel("empresas"));
                            return ResponseEntity.ok(recurso);
                        })
                        .orElse(ResponseEntity.notFound().build()))
                .orElse(ResponseEntity.notFound().build());
    }
}
