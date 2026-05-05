package br.unipar.foodservice.repositories;

import br.unipar.foodservice.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByTelefone(String telefone);

    boolean existsByTelefone(String telefone);

    List<Cliente> findByAtivoTrue();
}
