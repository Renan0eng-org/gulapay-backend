package br.unipar.foodservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Resolve o usuário corrente para preencher os campos de auditoria
 * (criadoPor / atualizadoPor) das entidades que estendem BaseEntity.
 */
@Configuration
public class JpaAuditingConfig {

    private static final String SISTEMA = "system";

    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return Optional.of(SISTEMA);
            }
            return Optional.of(auth.getName());
        };
    }
}
