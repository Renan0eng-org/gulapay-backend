package br.unipar.foodservice.enums;

/**
 * Status do ciclo de vida da Mesa (decisão 5.3 do CLAUDE.md):
 * uma Mesa está OCUPADA enquanto possuir ao menos uma Comanda aberta.
 */
public enum StatusMesa {
    LIVRE,
    OCUPADA,
    AGUARDANDO_PAGAMENTO,
    FECHADA
}
