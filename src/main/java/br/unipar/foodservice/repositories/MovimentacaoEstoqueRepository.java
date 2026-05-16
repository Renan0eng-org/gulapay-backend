package br.unipar.foodservice.repositories;

import br.unipar.foodservice.entities.MovimentacaoEstoque;
import br.unipar.foodservice.enums.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimentacaoEstoqueRepository extends JpaRepository<MovimentacaoEstoque, Long> {

    List<MovimentacaoEstoque> findByInsumoIdOrderByDataHoraDesc(Long insumoId);

    List<MovimentacaoEstoque> findByTipoAndDataHoraBetweenOrderByDataHoraDesc(
            TipoMovimentacao tipo, LocalDateTime inicio, LocalDateTime fim);
}
