package br.unipar.foodservice.dtos;

import br.unipar.foodservice.entities.Entregador;

public record EntregadorResponse(
        Long id,
        String nome,
        String telefone,
        Boolean ativo
) {
    public static EntregadorResponse from(Entregador e) {
        return new EntregadorResponse(e.getId(), e.getNome(), e.getTelefone(), e.getAtivo());
    }
}
