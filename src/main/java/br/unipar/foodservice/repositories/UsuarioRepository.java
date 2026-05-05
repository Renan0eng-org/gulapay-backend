package br.unipar.foodservice.repositories;

import br.unipar.foodservice.entities.Usuario;
import br.unipar.foodservice.enums.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

    boolean existsByLogin(String login);

    List<Usuario> findByPerfil(Perfil perfil);
}
