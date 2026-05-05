package br.unipar.foodservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

/**
 * Geração e validação de JWT (HS256). Token contém:
 *   - subject = login do usuário
 *   - claim "perfil" = perfil do usuário
 *   - claim "uid" = id do usuário
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final JwtProperties properties;
    private final SecretKey secretKey;

    public JwtTokenProvider(JwtProperties properties) {
        this.properties = properties;
        this.secretKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generate(String login, Long usuarioId, String perfil) {
        Instant agora = Instant.now();
        Instant expiracao = agora.plus(properties.expirationMinutes(), ChronoUnit.MINUTES);
        return Jwts.builder()
                .subject(login)
                .issuer(properties.issuer())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(expiracao))
                .claim("uid", usuarioId)
                .claim("perfil", perfil)
                .signWith(secretKey)
                .compact();
    }

    public Optional<Claims> parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(properties.issuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Optional.of(claims);
        } catch (JwtException ex) {
            log.debug("JWT inválido: {}", ex.getMessage());
            return Optional.empty();
        }
    }
}
