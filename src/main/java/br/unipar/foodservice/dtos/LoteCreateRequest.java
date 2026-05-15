package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteCreateRequest(
        @NotNull
        Long insumoId,

        @Size(max = 60)
        String codigo,

        @NotNull
        LocalDate validade,

        @NotNull @DecimalMin(value = "0.001", message = "quantidadeInicial deve ser > 0")
        BigDecimal quantidadeInicial,

        /** Unidade em que a quantidade está sendo informada. Pode diferir da unidadePadrao do insumo. */
        @NotNull
        Long unidadeId,

        @NotNull @DecimalMin(value = "0.0000")
        BigDecimal custoUnitario
) {
}
