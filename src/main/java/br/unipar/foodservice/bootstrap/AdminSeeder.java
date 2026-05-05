package br.unipar.foodservice.bootstrap;

import br.unipar.foodservice.entities.Usuario;
import br.unipar.foodservice.enums.Perfil;
import br.unipar.foodservice.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Cria um usuário ADMINISTRADOR padrão na primeira inicialização em dev,
 * apenas se nenhum usuário existir ainda. Em produção a criação do primeiro
 * admin deve ser feita manualmente (ou por outro processo de provisioning).
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }
        Usuario admin = Usuario.builder()
                .login("admin")
                .nome("Administrador do Sistema")
                .senha(passwordEncoder.encode("admin123"))
                .perfil(Perfil.ADMINISTRADOR)
                .ativo(true)
                .build();
        usuarioRepository.save(admin);
        log.warn("======================================================");
        log.warn("Usuário admin criado para DEV: login=admin / senha=admin123");
        log.warn("Troque a senha imediatamente em ambientes que não sejam de teste local.");
        log.warn("======================================================");
    }
}
