package br.unipar.foodservice.services;

import br.unipar.foodservice.entities.UnidadeMedida;
import br.unipar.foodservice.enums.TipoMedida;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.repositories.InsumoRepository;
import br.unipar.foodservice.repositories.UnidadeMedidaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class UnidadeMedidaServiceTest {

    @Mock
    private UnidadeMedidaRepository repository;
    @Mock
    private InsumoRepository insumoRepository;

    @InjectMocks
    private UnidadeMedidaService service;

    private UnidadeMedida grama() {
        return UnidadeMedida.builder().id(1L).nome("Grama").simbolo("g")
                .tipoMedida(TipoMedida.MASSA).fatorParaBase(BigDecimal.ONE).ativo(true).build();
    }

    private UnidadeMedida quilo() {
        return UnidadeMedida.builder().id(2L).nome("Quilograma").simbolo("kg")
                .tipoMedida(TipoMedida.MASSA).fatorParaBase(new BigDecimal("1000")).ativo(true).build();
    }

    private UnidadeMedida litro() {
        return UnidadeMedida.builder().id(3L).nome("Litro").simbolo("L")
                .tipoMedida(TipoMedida.VOLUME).fatorParaBase(new BigDecimal("1000")).ativo(true).build();
    }

    @Test
    void converter_deKgParaG_multiplicaPor1000() {
        BigDecimal r = service.converter(new BigDecimal("2"), quilo(), grama());
        assertThat(r).isEqualByComparingTo("2000");
    }

    @Test
    void converter_deGParaKg_divideePor1000() {
        BigDecimal r = service.converter(new BigDecimal("250"), grama(), quilo());
        assertThat(r).isEqualByComparingTo("0.25");
    }

    @Test
    void converter_mesmaUnidade_retornaQuantidadeOriginal() {
        BigDecimal r = service.converter(new BigDecimal("3.5"), quilo(), quilo());
        assertThat(r).isEqualByComparingTo("3.5");
    }

    @Test
    void converter_tiposDiferentes_lancaBusinessException() {
        assertThatThrownBy(() -> service.converter(new BigDecimal("1"), quilo(), litro()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Conversão impossível");
    }

    @Test
    void converterParaBase_kgParaG_retornaValorEmGramas() {
        BigDecimal r = service.converterParaBase(new BigDecimal("0.5"), quilo());
        assertThat(r).isEqualByComparingTo("500.0");
    }
}
