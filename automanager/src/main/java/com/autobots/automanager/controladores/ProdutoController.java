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

import com.autobots.automanager.entitades.Mercadoria;
import com.autobots.automanager.representacoes.ProdutoRecurso;
import com.autobots.automanager.repositorios.ProdutoRepositorio;

@RestController
@RequestMapping("/produto")
public class ProdutoController {

    @Autowired
    private ProdutoRepositorio repositorio;

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoRecurso> buscarProduto(@PathVariable Long id) {
        return repositorio.findById(id)
                .map(mercadoria -> {
                    ProdutoRecurso recurso = new ProdutoRecurso(mercadoria);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProdutoController.class).buscarProduto(id)).withSelfRel());
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProdutoController.class).listarProdutos()).withRel("produtos"));
                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<CollectionModel<ProdutoRecurso>> listarProdutos() {
        List<ProdutoRecurso> recursos = repositorio.findAll().stream()
                .map(mercadoria -> {
                    ProdutoRecurso recurso = new ProdutoRecurso(mercadoria);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProdutoController.class).buscarProduto(mercadoria.getId())).withSelfRel());
                    return recurso;
                })
                .collect(Collectors.toList());
        CollectionModel<ProdutoRecurso> colecao = CollectionModel.of(recursos,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProdutoController.class).listarProdutos()).withSelfRel());
        return ResponseEntity.ok(colecao);
    }

    @PostMapping
    public ResponseEntity<ProdutoRecurso> cadastrarProduto(@RequestBody Mercadoria mercadoria) {
        mercadoria.setCadastro(new Date());
        Mercadoria salva = repositorio.save(mercadoria);
        ProdutoRecurso recurso = new ProdutoRecurso(salva);
        recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProdutoController.class).buscarProduto(salva.getId())).withSelfRel());
        recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProdutoController.class).listarProdutos()).withRel("produtos"));
        return ResponseEntity.status(HttpStatus.CREATED).body(recurso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoRecurso> atualizarProduto(@PathVariable Long id, @RequestBody Mercadoria dados) {
        return repositorio.findById(id)
                .map(mercadoria -> {
                    if (dados.getNome() != null) mercadoria.setNome(dados.getNome());
                    if (dados.getDescricao() != null) mercadoria.setDescricao(dados.getDescricao());
                    if (dados.getValor() > 0) mercadoria.setValor(dados.getValor());
                    if (dados.getQuantidade() > 0) mercadoria.setQuantidade(dados.getQuantidade());
                    if (dados.getValidade() != null) mercadoria.setValidade(dados.getValidade());
                    if (dados.getFabricao() != null) mercadoria.setFabricao(dados.getFabricao());
                    Mercadoria atualizada = repositorio.save(mercadoria);
                    ProdutoRecurso recurso = new ProdutoRecurso(atualizada);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProdutoController.class).buscarProduto(id)).withSelfRel());
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProdutoController.class).listarProdutos()).withRel("produtos"));
                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        if (!repositorio.existsById(id)) return ResponseEntity.notFound().build();
        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
