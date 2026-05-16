package br.unipar.foodservice.repositories;

import br.unipar.foodservice.entities.UnidadeMedida;
import br.unipar.foodservice.enums.TipoMedida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadeMedidaRepository extends JpaRepository<UnidadeMedida, Long> {

    List<UnidadeMedida> findByAtivoTrue();

    List<UnidadeMedida> findByTipoMedidaAndAtivoTrue(TipoMedida tipoMedida);

    Optional<UnidadeMedida> findBySimboloIgnoreCase(String simbolo);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsBySimboloIgnoreCase(String simbolo);

    Optional<UnidadeMedida> findByTipoMedidaAndFatorParaBase(TipoMedida tipoMedida, BigDecimal fator);
}
