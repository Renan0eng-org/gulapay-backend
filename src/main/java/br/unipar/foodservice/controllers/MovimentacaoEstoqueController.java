package br.unipar.foodservice.controllers;

import br.unipar.foodservice.dtos.MovimentacaoEstoqueCreateRequest;
import br.unipar.foodservice.dtos.MovimentacaoEstoqueResponse;
import br.unipar.foodservice.entities.MovimentacaoEstoque;
import br.unipar.foodservice.services.MovimentacaoEstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/movimentacoes-estoque")
@RequiredArgsConstructor
@Tag(name = "Movimentações de estoque", description = "Entradas, saídas (FEFO), perdas e ajustes")
public class MovimentacaoEstoqueController {

    private final MovimentacaoEstoqueService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','CAIXA')")
    @Operation(summary = "Registra movimentação. Saídas sem lote informado seguem FEFO automaticamente.")
    public ResponseEntity<List<MovimentacaoEstoqueResponse>> registrar(
            @Valid @RequestBody MovimentacaoEstoqueCreateRequest request) {
        List<MovimentacaoEstoque> registros = service.registrar(request);
        return ResponseEntity.ok(registros.stream()
                .map(MovimentacaoEstoqueResponse::from).toList());
    }

    @GetMapping
    @Operation(summary = "Lista movimentações de um insumo, mais recentes primeiro.")
    public ResponseEntity<List<MovimentacaoEstoqueResponse>> listar(@RequestParam Long insumoId) {
        return ResponseEntity.ok(service.listarPorInsumo(insumoId).stream()
                .map(MovimentacaoEstoqueResponse::from).toList());
    }
}
