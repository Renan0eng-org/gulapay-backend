package br.unipar.foodservice.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configurações do JWT (carregadas de application.yml em app.security.jwt.*).
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public record JwtProperties(
        String secret,
        long expirationMinutes,
        String issuer
) {
}
