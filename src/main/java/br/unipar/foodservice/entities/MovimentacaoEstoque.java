package br.unipar.foodservice.entities;

import br.unipar.foodservice.enums.TipoMovimentacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Registro imutável de movimentação de estoque. Cada Lote afetado gera um
 * registro próprio (uma saída via FEFO que toca 3 lotes gera 3 movimentações).
 */
@Entity
@Table(name = "movimentacao_estoque")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimentacaoEstoque extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoMovimentacao tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unidade_id", nullable = false)
    private UnidadeMedida unidade;

    /** Quantidade conforme informado pelo usuário, na unidade dele. */
    @Column(nullable = false, precision = 18, scale = 3)
    private BigDecimal quantidade;

    /** Quantidade convertida para a unidadePadrao do insumo. Esta é a que mexeu o saldo. */
    @Column(name = "quantidade_unidade_padrao", nullable = false, precision = 18, scale = 3)
    private BigDecimal quantidadeUnidadePadrao;

    /** Custo no caso de entrada (se aplicável). */
    @Column(name = "custo_unitario", precision = 18, scale = 4)
    private BigDecimal custoUnitario;

    @Column(length = 500)
    private String justificativa;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    /** Login do usuário responsável pela operação (cópia, para preservar o histórico). */
    @Column(length = 60)
    private String responsavel;
}
