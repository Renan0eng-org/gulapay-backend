package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Atualizações permitidas em Lote: código (rastreio), validade e ativo.
 * Quantidade NÃO é editada por aqui — use AJUSTE_INVENTARIO em /movimentacoes-estoque.
 */
public record LotePatchRequest(
        @Size(max = 60) String codigo,
        LocalDate validade,
        Boolean ativo
) {
}
