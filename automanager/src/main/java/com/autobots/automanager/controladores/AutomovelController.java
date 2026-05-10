package com.autobots.automanager.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.Veiculo;
import com.autobots.automanager.representacoes.AutomovelRecurso;
import com.autobots.automanager.repositorios.AutomovelRepositorio;

@RestController
@RequestMapping("/automovel")
public class AutomovelController {

    @Autowired
    private AutomovelRepositorio repositorio;

    @GetMapping("/{id}")
    public ResponseEntity<AutomovelRecurso> buscarAutomovel(@PathVariable Long id) {
        return repositorio.findById(id)
                .map(veiculo -> {
                    AutomovelRecurso recurso = new AutomovelRecurso(veiculo);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AutomovelController.class).buscarAutomovel(id)).withSelfRel());
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AutomovelController.class).listarAutomoveis()).withRel("automoveis"));
                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<CollectionModel<AutomovelRecurso>> listarAutomoveis() {
        List<AutomovelRecurso> recursos = repositorio.findAll().stream()
                .map(veiculo -> {
                    AutomovelRecurso recurso = new AutomovelRecurso(veiculo);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AutomovelController.class).buscarAutomovel(veiculo.getId())).withSelfRel());
                    return recurso;
                })
                .collect(Collectors.toList());
        CollectionModel<AutomovelRecurso> colecao = CollectionModel.of(recursos,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AutomovelController.class).listarAutomoveis()).withSelfRel());
        return ResponseEntity.ok(colecao);
    }

    @PostMapping
    public ResponseEntity<AutomovelRecurso> cadastrarAutomovel(@RequestBody Veiculo veiculo) {
        Veiculo salvo = repositorio.save(veiculo);
        AutomovelRecurso recurso = new AutomovelRecurso(salvo);
        recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AutomovelController.class).buscarAutomovel(salvo.getId())).withSelfRel());
        recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AutomovelController.class).listarAutomoveis()).withRel("automoveis"));
        return ResponseEntity.status(HttpStatus.CREATED).body(recurso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AutomovelRecurso> atualizarAutomovel(@PathVariable Long id, @RequestBody Veiculo dados) {
        return repositorio.findById(id)
                .map(veiculo -> {
                    if (dados.getPlaca() != null) veiculo.setPlaca(dados.getPlaca());
                    if (dados.getModelo() != null) veiculo.setModelo(dados.getModelo());
                    if (dados.getTipo() != null) veiculo.setTipo(dados.getTipo());
                    if (dados.getProprietario() != null) veiculo.setProprietario(dados.getProprietario());
                    Veiculo atualizado = repositorio.save(veiculo);
                    AutomovelRecurso recurso = new AutomovelRecurso(atualizado);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AutomovelController.class).buscarAutomovel(id)).withSelfRel());
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(AutomovelController.class).listarAutomoveis()).withRel("automoveis"));
                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAutomovel(@PathVariable Long id) {
        if (!repositorio.existsById(id)) return ResponseEntity.notFound().build();
        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
