package br.unipar.foodservice.enums;

/**
 * Setor responsável pela produção/preparação do produto. Usado para roteamento
 * da impressão térmica do pedido (cada setor recebe sua via).
 */
public enum SetorProducao {
    COZINHA,
    BAR,
    BALCAO
}
