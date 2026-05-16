package br.unipar.foodservice.dtos;

import br.unipar.foodservice.entities.UnidadeMedida;
import br.unipar.foodservice.enums.TipoMedida;

import java.math.BigDecimal;

public record UnidadeMedidaResponse(
        Long id,
        String nome,
        String simbolo,
        TipoMedida tipoMedida,
        BigDecimal fatorParaBase,
        Boolean ehBase,
        Boolean ativo
) {
    public static UnidadeMedidaResponse from(UnidadeMedida u) {
        return new UnidadeMedidaResponse(
                u.getId(),
                u.getNome(),
                u.getSimbolo(),
                u.getTipoMedida(),
                u.getFatorParaBase(),
                u.ehBase(),
                u.getAtivo());
    }
}
