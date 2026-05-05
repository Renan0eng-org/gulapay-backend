package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MesaCreateRequest(
        @NotBlank @Size(min = 1, max = 20)
        String numero,

        @Size(max = 120)
        String descricao,

        @NotNull @Min(1)
        Integer capacidade
) {
}
