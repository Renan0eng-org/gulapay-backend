package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.Size;

/**
 * Atualização parcial de Categoria. Semântica JSON Merge Patch leve:
 * campo null no body = "não altera"; campo presente = "atualiza para este valor".
 */
public record CategoriaPatchRequest(
        @Size(min = 2, max = 100)
        String nome,

        @Size(max = 255)
        String descricao,

        Boolean ativo
) {
}
