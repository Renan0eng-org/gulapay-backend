package br.unipar.foodservice.dtos;

import br.unipar.foodservice.enums.SetorProducao;
import br.unipar.foodservice.enums.TipoProduto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProdutoPatchRequest(
        @Size(min = 2, max = 120) String nome,
        @Size(max = 500) String descricao,
        @DecimalMin(value = "0.00") BigDecimal preco,
        TipoProduto tipoProduto,
        SetorProducao setorProducao,
        Long categoriaId,
        Long insumoId,
        Boolean ativo
) {
}
