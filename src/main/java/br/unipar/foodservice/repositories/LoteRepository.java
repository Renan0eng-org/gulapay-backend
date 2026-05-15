package br.unipar.foodservice.repositories;

import br.unipar.foodservice.entities.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    List<Lote> findByInsumoIdAndAtivoTrueOrderByValidadeAscIdAsc(Long insumoId);

    /** Lotes ativos com quantidade restante > 0 ordenados por validade (FEFO). */
    @Query("""
        SELECT l FROM Lote l
        WHERE l.insumo.id = :insumoId
          AND l.ativo = true
          AND l.quantidadeRestante > 0
        ORDER BY l.validade ASC, l.id ASC
        """)
    List<Lote> findFefo(@Param("insumoId") Long insumoId);

    @Query("""
        SELECT COALESCE(SUM(l.quantidadeRestante), 0) FROM Lote l
        WHERE l.insumo.id = :insumoId AND l.ativo = true
        """)
    BigDecimal somarQuantidadeRestantePorInsumoAtivo(@Param("insumoId") Long insumoId);

    boolean existsByInsumoIdAndAtivoTrue(Long insumoId);
}
