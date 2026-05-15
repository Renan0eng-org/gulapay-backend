package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.Size;

/**
 * tipoMedida e fatorParaBase continuam imutáveis (regra 4.5.1 do CLAUDE.md).
 */
public record UnidadeMedidaPatchRequest(
        @Size(min = 2, max = 60) String nome,
        @Size(min = 1, max = 10) String simbolo,
        Boolean ativo
) {
}
