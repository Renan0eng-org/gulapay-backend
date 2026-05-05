package br.unipar.foodservice.enums;

/**
 * Tipos de produto (segregação 4.4 do CLAUDE.md):
 *  - UNITARIO: produto vendido como está (bebida, sobremesa industrializada). Saída direta de estoque.
 *  - COMPOSTO: tem ficha técnica (BOM); ao vender, baixa insumos automaticamente.
 *  - COMBO:    agrupamento de outros produtos (UNITARIO e/ou COMPOSTO).
 */
public enum TipoProduto {
    UNITARIO,
    COMPOSTO,
    COMBO
}
