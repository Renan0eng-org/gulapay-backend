package br.unipar.foodservice.controllers;

import br.unipar.foodservice.dtos.CategoriaCreateRequest;
import br.unipar.foodservice.dtos.CategoriaPatchRequest;
import br.unipar.foodservice.dtos.CategoriaResponse;
import br.unipar.foodservice.dtos.CategoriaUpdateRequest;
import br.unipar.foodservice.entities.Categoria;
import br.unipar.foodservice.services.CategoriaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Tag(name = "Categorias", description = "Categorias de produtos (bebidas, pratos, sobremesas, etc.)")
public class CategoriaController {

    private final CategoriaService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cria uma categoria.")
    public ResponseEntity<CategoriaResponse> criar(@Valid @RequestBody CategoriaCreateRequest request) {
        Categoria nova = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(nova.getId()).toUri();
        return ResponseEntity.created(location).body(CategoriaResponse.from(nova));
    }

    @GetMapping
    @Operation(summary = "Lista categorias. Use apenasAtivas=true para filtrar.")
    public ResponseEntity<List<CategoriaResponse>> listar(
            @RequestParam(name = "apenasAtivas", defaultValue = "false") boolean apenasAtivas) {
        return ResponseEntity.ok(service.listar(apenasAtivas).stream()
                .map(CategoriaResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca categoria por id.")
    public ResponseEntity<CategoriaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(CategoriaResponse.from(service.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Atualiza uma categoria (PUT — substituição completa).")
    public ResponseEntity<CategoriaResponse> atualizar(@PathVariable Long id,
                                                       @Valid @RequestBody CategoriaUpdateRequest request) {
        return ResponseEntity.ok(CategoriaResponse.from(service.atualizar(id, request)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Atualização parcial. Útil para reativar via { \"ativo\": true }.")
    public ResponseEntity<CategoriaResponse> patch(@PathVariable Long id,
                                                   @Valid @RequestBody CategoriaPatchRequest request) {
        return ResponseEntity.ok(CategoriaResponse.from(service.patch(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Inativa (soft delete) uma categoria.")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
