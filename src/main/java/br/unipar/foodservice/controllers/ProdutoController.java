package br.unipar.foodservice.controllers;

import br.unipar.foodservice.dtos.ProdutoCreateRequest;
import br.unipar.foodservice.dtos.ProdutoResponse;
import br.unipar.foodservice.dtos.ProdutoUpdateRequest;
import br.unipar.foodservice.entities.Produto;
import br.unipar.foodservice.services.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Itens vendáveis (UNITARIO no MVP; COMPOSTO/COMBO em sprints futuras)")
public class ProdutoController {

    private final ProdutoService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cria um produto.")
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoCreateRequest request) {
        Produto novo = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(novo.getId()).toUri();
        return ResponseEntity.created(location).body(ProdutoResponse.from(novo));
    }

    @GetMapping
    @Operation(summary = "Lista produtos. Filtra por categoria via 'categoriaId' e por status via 'apenasAtivos'.")
    public ResponseEntity<List<ProdutoResponse>> listar(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(name = "apenasAtivos", defaultValue = "false") boolean apenasAtivos) {
        List<Produto> produtos = categoriaId != null
                ? service.listarPorCategoria(categoriaId)
                : service.listar(apenasAtivos);
        return ResponseEntity.ok(produtos.stream().map(ProdutoResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca produto por id.")
    public ResponseEntity<ProdutoResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(ProdutoResponse.from(service.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Atualiza um produto.")
    public ResponseEntity<ProdutoResponse> atualizar(@PathVariable Long id,
                                                     @Valid @RequestBody ProdutoUpdateRequest request) {
        return ResponseEntity.ok(ProdutoResponse.from(service.atualizar(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Inativa (soft delete) um produto.")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
