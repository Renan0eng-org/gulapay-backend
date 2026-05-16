package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record InsumoUpdateRequest(
        @NotBlank @Size(min = 2, max = 120)
        String nome,

        @NotNull
        Long unidadePadraoId,

        @NotNull @DecimalMin(value = "0.000")
        BigDecimal estoqueMinimo,

        @NotNull
        Boolean ativo
) {
}
