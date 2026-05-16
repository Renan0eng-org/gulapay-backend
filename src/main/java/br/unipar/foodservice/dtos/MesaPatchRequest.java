package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record MesaPatchRequest(
        @Size(min = 1, max = 20) String numero,
        @Size(max = 120) String descricao,
        @Min(1) Integer capacidade,
        Boolean ativo
) {
}
