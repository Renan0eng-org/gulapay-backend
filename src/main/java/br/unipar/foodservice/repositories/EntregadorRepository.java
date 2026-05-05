package br.unipar.foodservice.repositories;

import br.unipar.foodservice.entities.Entregador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntregadorRepository extends JpaRepository<Entregador, Long> {

    List<Entregador> findByAtivoTrue();
}
