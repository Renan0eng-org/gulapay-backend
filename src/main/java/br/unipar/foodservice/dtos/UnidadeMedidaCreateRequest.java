package br.unipar.foodservice.dtos;

import br.unipar.foodservice.enums.TipoMedida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UnidadeMedidaCreateRequest(
        @NotBlank @Size(min = 2, max = 60)
        String nome,

        @NotBlank @Size(min = 1, max = 10)
        String simbolo,

        @NotNull
        TipoMedida tipoMedida,

        @NotNull @DecimalMin(value = "0.000001", message = "fatorParaBase deve ser > 0")
        BigDecimal fatorParaBase
) {
}
