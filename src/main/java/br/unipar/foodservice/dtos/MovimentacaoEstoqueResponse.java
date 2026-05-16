package br.unipar.foodservice.dtos;

import br.unipar.foodservice.entities.MovimentacaoEstoque;
import br.unipar.foodservice.enums.TipoMovimentacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimentacaoEstoqueResponse(
        Long id,
        TipoMovimentacao tipo,
        Long insumoId,
        String insumoNome,
        Long loteId,
        Long unidadeId,
        String unidadeSimbolo,
        BigDecimal quantidade,
        BigDecimal quantidadeUnidadePadrao,
        String unidadePadraoSimbolo,
        BigDecimal custoUnitario,
        String justificativa,
        LocalDateTime dataHora,
        String responsavel
) {
    public static MovimentacaoEstoqueResponse from(MovimentacaoEstoque m) {
        return new MovimentacaoEstoqueResponse(
                m.getId(),
                m.getTipo(),
                m.getInsumo().getId(),
                m.getInsumo().getNome(),
                m.getLote() == null ? null : m.getLote().getId(),
                m.getUnidade().getId(),
                m.getUnidade().getSimbolo(),
                m.getQuantidade(),
                m.getQuantidadeUnidadePadrao(),
                m.getInsumo().getUnidadePadrao().getSimbolo(),
                m.getCustoUnitario(),
                m.getJustificativa(),
                m.getDataHora(),
                m.getResponsavel());
    }
}
