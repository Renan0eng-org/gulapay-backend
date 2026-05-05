package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoriaUpdateRequest(
        @NotBlank @Size(min = 2, max = 100)
        String nome,

        @Size(max = 255)
        String descricao,

        @NotNull
        Boolean ativo
) {
}
