package br.unipar.foodservice.dtos;

import br.unipar.foodservice.entities.Mesa;
import br.unipar.foodservice.enums.StatusMesa;

public record MesaResponse(
        Long id,
        String numero,
        String descricao,
        Integer capacidade,
        StatusMesa status,
        Boolean ativo
) {
    public static MesaResponse from(Mesa m) {
        return new MesaResponse(
                m.getId(), m.getNumero(), m.getDescricao(),
                m.getCapacidade(), m.getStatus(), m.getAtivo());
    }
}
