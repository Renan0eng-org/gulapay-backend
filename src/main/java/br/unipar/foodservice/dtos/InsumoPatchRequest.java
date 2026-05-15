package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record InsumoPatchRequest(
        @Size(min = 2, max = 120) String nome,
        Long unidadePadraoId,
        @DecimalMin(value = "0.000") BigDecimal estoqueMinimo,
        Boolean ativo
) {
}
