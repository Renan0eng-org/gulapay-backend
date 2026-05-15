package br.unipar.foodservice.dtos;

import br.unipar.foodservice.entities.Lote;

import java.math.BigDecimal;
import java.time.LocalDate;

public record LoteResponse(
        Long id,
        Long insumoId,
        String insumoNome,
        String unidadePadraoSimbolo,
        String codigo,
        LocalDate validade,
        BigDecimal quantidadeInicial,
        BigDecimal quantidadeRestante,
        BigDecimal custoUnitario,
        Boolean ativo
) {
    public static LoteResponse from(Lote l) {
        return new LoteResponse(
                l.getId(),
                l.getInsumo().getId(),
                l.getInsumo().getNome(),
                l.getInsumo().getUnidadePadrao().getSimbolo(),
                l.getCodigo(),
                l.getValidade(),
                l.getQuantidadeInicial(),
                l.getQuantidadeRestante(),
                l.getCustoUnitario(),
                l.getAtivo());
    }
}
