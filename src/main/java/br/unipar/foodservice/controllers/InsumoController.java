package br.unipar.foodservice.controllers;

import br.unipar.foodservice.dtos.InsumoCreateRequest;
import br.unipar.foodservice.dtos.InsumoPatchRequest;
import br.unipar.foodservice.dtos.InsumoResponse;
import br.unipar.foodservice.dtos.InsumoUpdateRequest;
import br.unipar.foodservice.entities.Insumo;
import br.unipar.foodservice.services.InsumoService;
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
@RequestMapping("/insumos")
@RequiredArgsConstructor
@Tag(name = "Insumos", description = "Matérias-primas e itens de estoque")
public class InsumoController {

    private final InsumoService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cadastra um insumo.")
    public ResponseEntity<InsumoResponse> criar(@Valid @RequestBody InsumoCreateRequest request) {
        Insumo novo = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(novo.getId()).toUri();
        return ResponseEntity.created(location).body(InsumoResponse.from(novo, service.estoqueAtual(novo.getId())));
    }

    @GetMapping
    @Operation(summary = "Lista insumos. abaixoDoMinimo=true filtra os que estão abaixo do estoque mínimo.")
    public ResponseEntity<List<InsumoResponse>> listar(
            @RequestParam(name = "apenasAtivos", defaultValue = "false") boolean apenasAtivos,
            @RequestParam(name = "abaixoDoMinimo", defaultValue = "false") boolean abaixoDoMinimo) {
        List<Insumo> lista = abaixoDoMinimo ? service.listarAbaixoDoMinimo() : service.listar(apenasAtivos);
        return ResponseEntity.ok(lista.stream()
                .map(i -> InsumoResponse.from(i, service.estoqueAtual(i.getId())))
                .toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca insumo por id (com estoque atual).")
    public ResponseEntity<InsumoResponse> buscar(@PathVariable Long id) {
        Insumo insumo = service.buscarPorId(id);
        return ResponseEntity.ok(InsumoResponse.from(insumo, service.estoqueAtual(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Atualiza um insumo (PUT — substituição completa).")
    public ResponseEntity<InsumoResponse> atualizar(@PathVariable Long id,
                                                    @Valid @RequestBody InsumoUpdateRequest request) {
        Insumo atualizado = service.atualizar(id, request);
        return ResponseEntity.ok(InsumoResponse.from(atualizado, service.estoqueAtual(id)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Atualização parcial. Útil para reativar via { \"ativo\": true } ou ajustar só o estoque mínimo.")
    public ResponseEntity<InsumoResponse> patch(@PathVariable Long id,
                                                @Valid @RequestBody InsumoPatchRequest request) {
        Insumo atualizado = service.patch(id, request);
        return ResponseEntity.ok(InsumoResponse.from(atualizado, service.estoqueAtual(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Inativa (soft delete) um insumo.")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
