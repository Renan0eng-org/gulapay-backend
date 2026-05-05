package br.unipar.foodservice.dtos;

import br.unipar.foodservice.entities.Usuario;
import br.unipar.foodservice.enums.Perfil;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String login,
        String nome,
        Perfil perfil,
        BigDecimal percentualComissao,
        Boolean ativo,
        LocalDateTime criadoEm
) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getLogin(),
                u.getNome(),
                u.getPerfil(),
                u.getPercentualComissao(),
                u.getAtivo(),
                u.getCriadoEm());
    }
}
