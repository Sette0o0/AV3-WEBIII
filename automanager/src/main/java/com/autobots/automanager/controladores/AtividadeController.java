package com.autobots.automanager.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.Servico;
import com.autobots.automanager.representacoes.AtividadeRecurso;
import com.autobots.automanager.repositorios.AtividadeRepositorio;

@RestController
@RequestMapping("/atividade")
public class AtividadeController {

    @Autowired
    private AtividadeRepositorio repositorio;

    @GetMapping("/{id}")
    public ResponseEntity<AtividadeRecurso> buscarAtividade(@PathVariable Long id) {
        return repositorio.findById(id)
                .map(servico -> {
                    AtividadeRecurso recurso = new AtividadeRecurso(servico);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AtividadeController.class).buscarAtividade(id)).withSelfRel());
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AtividadeController.class).listarAtividades()).withRel("atividades"));
                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<CollectionModel<AtividadeRecurso>> listarAtividades() {
        List<AtividadeRecurso> recursos = repositorio.findAll().stream()
                .map(servico -> {
                    AtividadeRecurso recurso = new AtividadeRecurso(servico);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AtividadeController.class).buscarAtividade(servico.getId())).withSelfRel());
                    return recurso;
                })
                .collect(Collectors.toList());
        CollectionModel<AtividadeRecurso> colecao = CollectionModel.of(recursos,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AtividadeController.class).listarAtividades()).withSelfRel());
        return ResponseEntity.ok(colecao);
    }

    @PostMapping
    public ResponseEntity<AtividadeRecurso> cadastrarAtividade(@RequestBody Servico servico) {
        Servico salvo = repositorio.save(servico);
        AtividadeRecurso recurso = new AtividadeRecurso(salvo);
        recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AtividadeController.class).buscarAtividade(salvo.getId())).withSelfRel());
        recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AtividadeController.class).listarAtividades()).withRel("atividades"));
        return ResponseEntity.status(HttpStatus.CREATED).body(recurso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AtividadeRecurso> atualizarAtividade(@PathVariable Long id, @RequestBody Servico dados) {
        return repositorio.findById(id)
                .map(servico -> {
                    if (dados.getNome() != null) servico.setNome(dados.getNome());
                    if (dados.getDescricao() != null) servico.setDescricao(dados.getDescricao());
                    if (dados.getValor() > 0) servico.setValor(dados.getValor());
                    Servico atualizado = repositorio.save(servico);
                    AtividadeRecurso recurso = new AtividadeRecurso(atualizado);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AtividadeController.class).buscarAtividade(id)).withSelfRel());
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AtividadeController.class).listarAtividades()).withRel("atividades"));
                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAtividade(@PathVariable Long id) {
        if (!repositorio.existsById(id)) return ResponseEntity.notFound().build();
        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
