package com.autobots.automanager.controladores;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.autobots.automanager.entitades.Usuario;
import com.autobots.automanager.representacoes.PessoaRecurso;
import com.autobots.automanager.repositorios.PessoaRepositorio;

@RestController
@RequestMapping("/pessoa")
public class PessoaController {

    @Autowired
    private PessoaRepositorio repositorio;

    @GetMapping("/{id}")
    public ResponseEntity<PessoaRecurso> buscarPessoa(@PathVariable Long id) {
        return repositorio.findById(id)
                .map(usuario -> {
                    PessoaRecurso recurso = new PessoaRecurso(usuario);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PessoaController.class).buscarPessoa(id)).withSelfRel());
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PessoaController.class).listarPessoas()).withRel("pessoas"));
                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<CollectionModel<PessoaRecurso>> listarPessoas() {
        List<PessoaRecurso> recursos = repositorio.findAll().stream()
                .map(usuario -> {
                    PessoaRecurso recurso = new PessoaRecurso(usuario);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PessoaController.class).buscarPessoa(usuario.getId())).withSelfRel());
                    return recurso;
                })
                .collect(Collectors.toList());
        CollectionModel<PessoaRecurso> colecao = CollectionModel.of(recursos,
                WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PessoaController.class).listarPessoas()).withSelfRel());
        return ResponseEntity.ok(colecao);
    }

    @PostMapping
    public ResponseEntity<PessoaRecurso> cadastrarPessoa(@RequestBody Usuario usuario) {
        Usuario salvo = repositorio.save(usuario);
        PessoaRecurso recurso = new PessoaRecurso(salvo);
        recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PessoaController.class).buscarPessoa(salvo.getId())).withSelfRel());
        recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PessoaController.class).listarPessoas()).withRel("pessoas"));
        return ResponseEntity.status(HttpStatus.CREATED).body(recurso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaRecurso> atualizarPessoa(@PathVariable Long id, @RequestBody Usuario dados) {
        return repositorio.findById(id)
                .map(usuario -> {
                    if (dados.getNome() != null) usuario.setNome(dados.getNome());
                    if (dados.getNomeSocial() != null) usuario.setNomeSocial(dados.getNomeSocial());
                    if (dados.getPerfis() != null && !dados.getPerfis().isEmpty()) usuario.getPerfis().addAll(dados.getPerfis());
                    if (dados.getEndereco() != null) usuario.setEndereco(dados.getEndereco());
                    if (dados.getTelefones() != null && !dados.getTelefones().isEmpty()) usuario.getTelefones().addAll(dados.getTelefones());
                    if (dados.getDocumentos() != null && !dados.getDocumentos().isEmpty()) usuario.getDocumentos().addAll(dados.getDocumentos());
                    if (dados.getEmails() != null && !dados.getEmails().isEmpty()) usuario.getEmails().addAll(dados.getEmails());
                    if (dados.getCredenciais() != null && !dados.getCredenciais().isEmpty()) usuario.getCredenciais().addAll(dados.getCredenciais());
                    Usuario atualizado = repositorio.save(usuario);
                    PessoaRecurso recurso = new PessoaRecurso(atualizado);
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PessoaController.class).buscarPessoa(id)).withSelfRel());
                    recurso.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(PessoaController.class).listarPessoas()).withRel("pessoas"));
                    return ResponseEntity.ok(recurso);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarPessoa(@PathVariable Long id) {
        if (!repositorio.existsById(id)) return ResponseEntity.notFound().build();
        repositorio.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
