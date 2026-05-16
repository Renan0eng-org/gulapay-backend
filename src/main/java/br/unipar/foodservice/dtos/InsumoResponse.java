package br.unipar.foodservice.dtos;

import br.unipar.foodservice.entities.Insumo;

import java.math.BigDecimal;

public record InsumoResponse(
        Long id,
        String nome,
        Long unidadePadraoId,
        String unidadePadraoSimbolo,
        String unidadePadraoNome,
        BigDecimal estoqueMinimo,
        BigDecimal estoqueAtual,
        Boolean abaixoDoMinimo,
        Boolean ativo
) {
    public static InsumoResponse from(Insumo i, BigDecimal estoqueAtual) {
        boolean abaixo = estoqueAtual.compareTo(i.getEstoqueMinimo()) < 0;
        return new InsumoResponse(
                i.getId(),
                i.getNome(),
                i.getUnidadePadrao().getId(),
                i.getUnidadePadrao().getSimbolo(),
                i.getUnidadePadrao().getNome(),
                i.getEstoqueMinimo(),
                estoqueAtual,
                abaixo,
                i.getAtivo());
    }
}
