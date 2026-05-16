package br.unipar.foodservice.dtos;

import br.unipar.foodservice.enums.TipoMovimentacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Request de movimentação manual. SAIDA_VENDA não é aceito por aqui — só pelo
 * fechamento de comanda na Sprint de Comanda.
 *
 * Para entradas (ENTRADA_COMPRA / ENTRADA_TROCA):
 *   - loteId opcional. Se ausente, um novo Lote é criado (use validade + custoUnitario).
 *   - Se loteId informado, a quantidade é somada ao lote existente.
 *
 * Para saídas (SAIDA_PERDA_*):
 *   - loteId opcional. Se informado, baixa só do lote indicado. Senão, FEFO.
 *
 * Para AJUSTE_INVENTARIO:
 *   - loteId obrigatório. quantidade é a NOVA quantidade restante (substitui).
 */
public record MovimentacaoEstoqueCreateRequest(
        @NotNull
        TipoMovimentacao tipo,

        @NotNull
        Long insumoId,

        Long loteId,

        @NotNull
        Long unidadeId,

        @NotNull @DecimalMin(value = "0.001", message = "quantidade deve ser > 0")
        BigDecimal quantidade,

        @DecimalMin(value = "0.0000")
        BigDecimal custoUnitario,

        java.time.LocalDate validade,

        @Size(max = 60)
        String codigoLote,

        @Size(max = 500)
        String justificativa
) {
}
