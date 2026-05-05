package br.unipar.foodservice.dtos;

import br.unipar.foodservice.entities.Produto;
import br.unipar.foodservice.enums.SetorProducao;
import br.unipar.foodservice.enums.TipoProduto;

import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        TipoProduto tipoProduto,
        SetorProducao setorProducao,
        Long categoriaId,
        String categoriaNome,
        Boolean ativo
) {
    public static ProdutoResponse from(Produto p) {
        return new ProdutoResponse(
                p.getId(),
                p.getNome(),
                p.getDescricao(),
                p.getPreco(),
                p.getTipoProduto(),
                p.getSetorProducao(),
                p.getCategoria().getId(),
                p.getCategoria().getNome(),
                p.getAtivo());
    }
}
