package br.unipar.foodservice.controllers;

import br.unipar.foodservice.dtos.LoteCreateRequest;
import br.unipar.foodservice.dtos.LotePatchRequest;
import br.unipar.foodservice.dtos.LoteResponse;
import br.unipar.foodservice.dtos.LoteUpdateRequest;
import br.unipar.foodservice.entities.Lote;
import br.unipar.foodservice.services.LoteService;
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
@RequestMapping("/lotes")
@RequiredArgsConstructor
@Tag(name = "Lotes", description = "Lotes de insumos com validade. Saídas seguem FEFO automaticamente.")
public class LoteController {

    private final LoteService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAIXA')")
    @Operation(summary = "Cria um lote de insumo. Quantidade convertida automaticamente para a unidade padrão.")
    public ResponseEntity<LoteResponse> criar(@Valid @RequestBody LoteCreateRequest request) {
        Lote novo = service.criar(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(novo.getId()).toUri();
        return ResponseEntity.created(location).body(LoteResponse.from(novo));
    }

    @GetMapping
    @Operation(summary = "Lista lotes de um insumo, ordenados por validade (FEFO).")
    public ResponseEntity<List<LoteResponse>> listarPorInsumo(@RequestParam Long insumoId) {
        return ResponseEntity.ok(service.listarPorInsumo(insumoId).stream()
                .map(LoteResponse::from).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca lote por id.")
    public ResponseEntity<LoteResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(LoteResponse.from(service.buscarPorId(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAIXA')")
    @Operation(summary = "Atualiza código e validade do lote (PUT — substituição completa).")
    public ResponseEntity<LoteResponse> atualizar(@PathVariable Long id,
                                                  @Valid @RequestBody LoteUpdateRequest request) {
        return ResponseEntity.ok(LoteResponse.from(service.atualizar(id, request)));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAIXA')")
    @Operation(summary = "Atualização parcial. Útil para reativar ou corrigir só a validade.")
    public ResponseEntity<LoteResponse> patch(@PathVariable Long id,
                                              @Valid @RequestBody LotePatchRequest request) {
        return ResponseEntity.ok(LoteResponse.from(service.patch(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @Operation(summary = "Inativa lote (somente se saldo zero).")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }
}
