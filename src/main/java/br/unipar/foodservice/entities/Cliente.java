package br.unipar.foodservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    /** Apenas dígitos (DDI+DDD+número), normalizado pelo service. */
    @Column(nullable = false, unique = true, length = 20)
    private String telefone;

    @Column(length = 120)
    private String email;

    @Column(name = "endereco_logradouro", length = 150)
    private String enderecoLogradouro;

    @Column(name = "endereco_numero", length = 20)
    private String enderecoNumero;

    @Column(name = "endereco_complemento", length = 80)
    private String enderecoComplemento;

    @Column(name = "endereco_bairro", length = 80)
    private String enderecoBairro;

    @Column(name = "endereco_cidade", length = 80)
    private String enderecoCidade;

    @Column(name = "endereco_uf", length = 2)
    private String enderecoUf;

    @Column(name = "endereco_cep", length = 10)
    private String enderecoCep;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;
}
