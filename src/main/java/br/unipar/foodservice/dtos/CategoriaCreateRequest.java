package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaCreateRequest(
        @NotBlank @Size(min = 2, max = 100)
        String nome,

        @Size(max = 255)
        String descricao
) {
}
