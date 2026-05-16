package br.unipar.foodservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import java.time.LocalDate;

@Entity
@Table(name = "lote")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    /** Código de rastreio do lote (NF, lote do fabricante, etc.). Opcional. */
    @Column(length = 60)
    private String codigo;

    @Column(nullable = false)
    private LocalDate validade;

    /** Quantidade quando o lote entrou — armazenada na unidadePadrao do insumo. */
    @Column(name = "quantidade_inicial", nullable = false, precision = 18, scale = 3)
    private BigDecimal quantidadeInicial;

    /** Quantidade ainda disponível — sempre na unidadePadrao do insumo. */
    @Column(name = "quantidade_restante", nullable = false, precision = 18, scale = 3)
    private BigDecimal quantidadeRestante;

    /** Custo de aquisição por unidade (na unidadePadrao do insumo). */
    @Column(name = "custo_unitario", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal custoUnitario = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;
}
