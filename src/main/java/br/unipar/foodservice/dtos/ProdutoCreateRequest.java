package br.unipar.foodservice.dtos;

import br.unipar.foodservice.enums.SetorProducao;
import br.unipar.foodservice.enums.TipoProduto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProdutoCreateRequest(
        @NotBlank @Size(min = 2, max = 120)
        String nome,

        @Size(max = 500)
        String descricao,

        @NotNull @DecimalMin(value = "0.00", message = "preco deve ser >= 0")
        BigDecimal preco,

        @NotNull
        TipoProduto tipoProduto,

        @NotNull
        SetorProducao setorProducao,

        @NotNull
        Long categoriaId
) {
}
