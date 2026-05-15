package br.unipar.foodservice.enums;

/**
 * Tipos de movimentação de estoque.
 *
 * Entradas aumentam saldo no Lote (ENTRADA_COMPRA/TROCA).
 * Saídas reduzem saldo, geralmente seguindo FEFO (First Expire First Out).
 * SAIDA_VENDA é gerada automaticamente pelo fechamento de comanda (Sprint de Comanda).
 * AJUSTE_INVENTARIO permite correções pós-contagem física.
 */
public enum TipoMovimentacao {
    ENTRADA_COMPRA,
    ENTRADA_TROCA,
    SAIDA_VENDA,
    SAIDA_PERDA_VALIDADE,
    SAIDA_PERDA_QUEBRA,
    AJUSTE_INVENTARIO;

    public boolean ehEntrada() {
        return this == ENTRADA_COMPRA || this == ENTRADA_TROCA;
    }

    public boolean ehSaida() {
        return this == SAIDA_VENDA || this == SAIDA_PERDA_VALIDADE || this == SAIDA_PERDA_QUEBRA;
    }
}
