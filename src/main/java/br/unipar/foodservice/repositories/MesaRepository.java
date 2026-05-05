package br.unipar.foodservice.repositories;

import br.unipar.foodservice.entities.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    Optional<Mesa> findByNumero(String numero);

    boolean existsByNumero(String numero);

    List<Mesa> findByAtivoTrue();
}
