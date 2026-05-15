package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Atualização parcial de UnidadeMedida. Apenas `nome`, `simbolo` e `ativo` podem
 * ser alterados após o cadastro — `tipoMedida` e `fatorParaBase` são imutáveis
 * para preservar a integridade das conversões em insumos / lotes / movimentações
 * já existentes (regra 4.5.1 do CLAUDE.md).
 */
public record UnidadeMedidaUpdateRequest(
        @NotBlank @Size(min = 2, max = 60)
        String nome,

        @NotBlank @Size(min = 1, max = 10)
        String simbolo,

        @NotNull
        Boolean ativo
) {
}
