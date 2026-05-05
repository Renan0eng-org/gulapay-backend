package br.unipar.foodservice.dtos;

import br.unipar.foodservice.entities.Categoria;

public record CategoriaResponse(
        Long id,
        String nome,
        String descricao,
        Boolean ativo
) {
    public static CategoriaResponse from(Categoria c) {
        return new CategoriaResponse(c.getId(), c.getNome(), c.getDescricao(), c.getAtivo());
    }
}
