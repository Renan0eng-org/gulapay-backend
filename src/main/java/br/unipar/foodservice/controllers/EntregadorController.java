package br.unipar.foodservice.controllers;

import br.unipar.foodservice.dtos.EntregadorCreateRequest;
import br.unipar.foodservice.dtos.EntregadorPatchRequest;
import br.unipar.foodservice.dtos.EntregadorResponse;
import br.unipar.foodservice.dtos.EntregadorUpdateRequest;
import br.unipar.foodservice.entities.Entregador;
import br.unipar.foodservice.services.EntregadorService;
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
@RequestMapping("/entregadores")
@RequiredArgsConstructor
@Tag(name = "Entregadores", description = "Entregadores internos — sem login. Recebem comanda impressa.")
public class EntregadorController {

    private final EntregadorService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAIXA')")
    @Operation(summary = "Cadastra um entregador.")
    public ResponseEntity<EntregadorResponse> criar(@Valid @RequestBody EntregadorCreateRequest request) {
        Entregador novo = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(novo.getId()).toUri();
        return ResponseEntity.created(location).body(EntregadorResponse.from(novo));
    }

    @GetMapping
    @Operation(summary = "Lista entregadores.")
    public ResponseEntity<List<EntregadorResponse>> listar(
            @RequestParam(name = "apenasAtivos", defaultValue = "false") boolean apenasAtivos) {
        return ResponseEntity.ok(service.listar(apenasAtivos).stream()
                .map(EntregadorResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca entregador por id.")
    public ResponseEntity<EntregadorResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(EntregadorResponse.from(service.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAIXA')")
    @Operation(summary = "Atualiza um entregador (PUT — substituição completa).")
    public ResponseEntity<EntregadorResponse> atualizar(@PathVariable Long id,
                                                        @Valid @RequestBody EntregadorUpdateRequest request) {
        return ResponseEntity.ok(EntregadorResponse.from(service.atualizar(id, request)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAIXA')")
    @Operation(summary = "Atualização parcial. Útil para reativar via { \"ativo\": true }.")
    public ResponseEntity<EntregadorResponse> patch(@PathVariable Long id,
                                                    @Valid @RequestBody EntregadorPatchRequest request) {
        return ResponseEntity.ok(EntregadorResponse.from(service.patch(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Inativa (soft delete) um entregador.")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
