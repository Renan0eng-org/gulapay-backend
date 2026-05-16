package br.unipar.foodservice.controllers;

import br.unipar.foodservice.dtos.UnidadeMedidaCreateRequest;
import br.unipar.foodservice.dtos.UnidadeMedidaPatchRequest;
import br.unipar.foodservice.dtos.UnidadeMedidaResponse;
import br.unipar.foodservice.dtos.UnidadeMedidaUpdateRequest;
import br.unipar.foodservice.entities.UnidadeMedida;
import br.unipar.foodservice.enums.TipoMedida;
import br.unipar.foodservice.services.UnidadeMedidaService;
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
@RequestMapping("/unidades-medida")
@RequiredArgsConstructor
@Tag(name = "Unidades de medida", description = "Cadastro com conversão (massa, volume, unidade)")
public class UnidadeMedidaController {

    private final UnidadeMedidaService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cria uma unidade de medida.")
    public ResponseEntity<UnidadeMedidaResponse> criar(@Valid @RequestBody UnidadeMedidaCreateRequest request) {
        UnidadeMedida nova = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(nova.getId()).toUri();
        return ResponseEntity.created(location).body(UnidadeMedidaResponse.from(nova));
    }

    @GetMapping
    @Operation(summary = "Lista unidades. Filtra por tipo (MASSA/VOLUME/UNIDADE) e/ou ativos.")
    public ResponseEntity<List<UnidadeMedidaResponse>> listar(
            @RequestParam(required = false) TipoMedida tipoMedida,
            @RequestParam(name = "apenasAtivas", defaultValue = "false") boolean apenasAtivas) {
        return ResponseEntity.ok(service.listar(apenasAtivas, tipoMedida).stream()
                .map(UnidadeMedidaResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca unidade por id.")
    public ResponseEntity<UnidadeMedidaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(UnidadeMedidaResponse.from(service.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Atualiza nome, símbolo ou status (PUT — substituição completa).")
    public ResponseEntity<UnidadeMedidaResponse> atualizar(@PathVariable Long id,
                                                           @Valid @RequestBody UnidadeMedidaUpdateRequest request) {
        return ResponseEntity.ok(UnidadeMedidaResponse.from(service.atualizar(id, request)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Atualização parcial. Tipo e fator continuam imutáveis. Útil para reativar.")
    public ResponseEntity<UnidadeMedidaResponse> patch(@PathVariable Long id,
                                                       @Valid @RequestBody UnidadeMedidaPatchRequest request) {
        return ResponseEntity.ok(UnidadeMedidaResponse.from(service.patch(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Inativa (soft delete) uma unidade de medida.")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
