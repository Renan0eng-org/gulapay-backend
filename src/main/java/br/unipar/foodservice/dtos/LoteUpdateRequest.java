package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Atualizações permitidas em Lote: código (rastreio) e validade (correção).
 * Quantidade não é editada diretamente — para alterar saldo, use MovimentacaoEstoque
 * com tipo AJUSTE_INVENTARIO.
 */
public record LoteUpdateRequest(
        @Size(max = 60)
        String codigo,

        @NotNull
        LocalDate validade,

        @NotNull
        Boolean ativo
) {
}
