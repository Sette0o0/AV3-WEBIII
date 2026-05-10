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

import com.autobots.automanager.entitades.Venda;
import com.autobots.automanager.representacoes.TransacaoRecurso;
import com.autobots.automanager.repositorios.TransacaoRepositorio;

@RestController
@RequestMapping("/transacao")
public class TransacaoController {

    @Autowired
    private TransacaoRepositorio repositorio;

    @GetMapping("/{id}")
    public ResponseEntity<TransacaoRecurso> buscarTransacao(@PathVariable Long id) {
        return repositorio.findById(id)
                .map(venda -> {
                    TransacaoRecurso recurso = new TransacaoRecurso(venda);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TransacaoController.class).buscarTransacao(id)).withSelfRel());
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TransacaoController.class).listarTransacoes()).withRel("transacoes"));
                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<CollectionModel<TransacaoRecurso>> listarTransacoes() {
        List<TransacaoRecurso> recursos = repositorio.findAll().stream()
                .map(venda -> {
                    TransacaoRecurso recurso = new TransacaoRecurso(venda);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TransacaoController.class).buscarTransacao(venda.getId())).withSelfRel());
                    return recurso;
                })
                .collect(Collectors.toList());
        CollectionModel<TransacaoRecurso> colecao = CollectionModel.of(recursos,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TransacaoController.class).listarTransacoes()).withSelfRel());
        return ResponseEntity.ok(colecao);
    }

    @PostMapping
    public ResponseEntity<TransacaoRecurso> registrarTransacao(@RequestBody Venda venda) {
        venda.setCadastro(new Date());
        Venda salva = repositorio.save(venda);
        TransacaoRecurso recurso = new TransacaoRecurso(salva);
        recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TransacaoController.class).buscarTransacao(salva.getId())).withSelfRel());
        recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TransacaoController.class).listarTransacoes()).withRel("transacoes"));
        return ResponseEntity.status(HttpStatus.CREATED).body(recurso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransacaoRecurso> atualizarTransacao(@PathVariable Long id, @RequestBody Venda dados) {
        return repositorio.findById(id)
                .map(venda -> {
                    if (dados.getIdentificacao() != null) venda.setIdentificacao(dados.getIdentificacao());
                    if (dados.getCliente() != null) venda.setCliente(dados.getCliente());
                    if (dados.getFuncionario() != null) venda.setFuncionario(dados.getFuncionario());
                    if (dados.getVeiculo() != null) venda.setVeiculo(dados.getVeiculo());
                    if (dados.getMercadorias() != null && !dados.getMercadorias().isEmpty()) venda.getMercadorias().addAll(dados.getMercadorias());
                    if (dados.getServicos() != null && !dados.getServicos().isEmpty()) venda.getServicos().addAll(dados.getServicos());
                    Venda atualizada = repositorio.save(venda);
                    TransacaoRecurso recurso = new TransacaoRecurso(atualizada);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TransacaoController.class).buscarTransacao(id)).withSelfRel());
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(TransacaoController.class).listarTransacoes()).withRel("transacoes"));
                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarTransacao(@PathVariable Long id) {
        if (!repositorio.existsById(id)) return ResponseEntity.notFound().build();
        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
