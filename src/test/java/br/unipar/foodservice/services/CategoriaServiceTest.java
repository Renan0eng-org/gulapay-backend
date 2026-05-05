package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.CategoriaCreateRequest;
import br.unipar.foodservice.entities.Categoria;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.repositories.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repository;

    @InjectMocks
    private CategoriaService service;

    @Test
    void criar_categoriaInedita_deveSalvar() {
        CategoriaCreateRequest req = new CategoriaCreateRequest("Bebidas", "Refrigerantes, sucos e cervejas");
        when(repository.existsByNomeIgnoreCase("Bebidas")).thenReturn(false);
        when(repository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        Categoria salva = service.criar(req);

        assertThat(salva.getNome()).isEqualTo("Bebidas");
        assertThat(salva.getAtivo()).isTrue();
    }

    @Test
    void criar_categoriaDuplicada_deveLancar() {
        CategoriaCreateRequest req = new CategoriaCreateRequest("Bebidas", null);
        when(repository.existsByNomeIgnoreCase("Bebidas")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Bebidas");
    }
}
