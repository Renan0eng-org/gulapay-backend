package br.unipar.foodservice.controllers;

import br.unipar.foodservice.dtos.MesaCreateRequest;
import br.unipar.foodservice.dtos.MesaPatchRequest;
import br.unipar.foodservice.dtos.MesaResponse;
import br.unipar.foodservice.dtos.MesaUpdateRequest;
import br.unipar.foodservice.entities.Mesa;
import br.unipar.foodservice.services.MesaService;
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
@RequestMapping("/mesas")
@RequiredArgsConstructor
@Tag(name = "Mesas", description = "Locais físicos de atendimento. Status muda conforme comandas vinculadas.")
public class MesaController {

    private final MesaService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Cria uma mesa.")
    public ResponseEntity<MesaResponse> criar(@Valid @RequestBody MesaCreateRequest request) {
        Mesa nova = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(nova.getId()).toUri();
        return ResponseEntity.created(location).body(MesaResponse.from(nova));
    }

    @GetMapping
    @Operation(summary = "Lista mesas.")
    public ResponseEntity<List<MesaResponse>> listar(
            @RequestParam(name = "apenasAtivas", defaultValue = "false") boolean apenasAtivas) {
        return ResponseEntity.ok(service.listar(apenasAtivas).stream()
                .map(MesaResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca mesa por id.")
    public ResponseEntity<MesaResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(MesaResponse.from(service.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Atualiza uma mesa (PUT — substituição completa).")
    public ResponseEntity<MesaResponse> atualizar(@PathVariable Long id,
                                                  @Valid @RequestBody MesaUpdateRequest request) {
        return ResponseEntity.ok(MesaResponse.from(service.atualizar(id, request)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Atualização parcial. Útil para reativar via { \"ativo\": true }.")
    public ResponseEntity<MesaResponse> patch(@PathVariable Long id,
                                              @Valid @RequestBody MesaPatchRequest request) {
        return ResponseEntity.ok(MesaResponse.from(service.patch(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Inativa (soft delete) uma mesa, se livre/fechada.")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
