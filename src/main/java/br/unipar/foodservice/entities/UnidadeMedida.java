package br.unipar.foodservice.entities;

import br.unipar.foodservice.enums.TipoMedida;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "unidade_medida")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadeMedida extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String nome;

    @Column(nullable = false, unique = true, length = 10)
    private String simbolo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_medida", nullable = false, length = 20)
    private TipoMedida tipoMedida;

    @Column(name = "fator_para_base", nullable = false, precision = 18, scale = 6)
    private BigDecimal fatorParaBase;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    /** True se esta unidade é a unidade-base do seu tipoMedida (fator = 1). */
    public boolean ehBase() {
        return fatorParaBase != null && fatorParaBase.compareTo(BigDecimal.ONE) == 0;
    }
}
