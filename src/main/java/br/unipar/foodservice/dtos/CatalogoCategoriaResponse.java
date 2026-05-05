package br.unipar.foodservice.dtos;

import java.util.List;

public record CatalogoCategoriaResponse(
        Long categoriaId,
        String categoria,
        List<CatalogoItemResponse> itens
) {
}
