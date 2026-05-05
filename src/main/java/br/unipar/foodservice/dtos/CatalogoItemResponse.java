package br.unipar.foodservice.dtos;

import br.unipar.foodservice.entities.Produto;

import java.math.BigDecimal;

public record CatalogoItemResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco
) {
    public static CatalogoItemResponse from(Produto p) {
        return new CatalogoItemResponse(p.getId(), p.getNome(), p.getDescricao(), p.getPreco());
    }
}
