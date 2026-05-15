package br.unipar.foodservice.repositories;

import br.unipar.foodservice.entities.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {

    List<Insumo> findByAtivoTrue();

    boolean existsByUnidadePadraoIdAndAtivoTrue(Long unidadeId);
}
