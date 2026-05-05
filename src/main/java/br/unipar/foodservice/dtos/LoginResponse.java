package br.unipar.foodservice.dtos;

import br.unipar.foodservice.enums.Perfil;

public record LoginResponse(
        String accessToken,
        String tokenType,
        long expiresInMinutes,
        Long usuarioId,
        String login,
        String nome,
        Perfil perfil
) {
}
