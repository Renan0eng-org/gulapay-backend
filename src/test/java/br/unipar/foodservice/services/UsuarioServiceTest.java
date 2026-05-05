package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.UsuarioCreateRequest;
import br.unipar.foodservice.entities.Usuario;
import br.unipar.foodservice.enums.Perfil;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.repositories.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService service;

    @Test
    void criar_garcomComComissao_deveSalvar() {
        UsuarioCreateRequest req = new UsuarioCreateRequest(
                "joao.garcom", "João Garçom", "senha123", Perfil.GARCOM, new BigDecimal("10.00"));
        when(repository.existsByLogin("joao.garcom")).thenReturn(false);
        when(passwordEncoder.encode("senha123")).thenReturn("HASH");
        when(repository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        Usuario salvo = service.criar(req);

        assertThat(salvo.getLogin()).isEqualTo("joao.garcom");
        assertThat(salvo.getSenha()).isEqualTo("HASH");
        assertThat(salvo.getPerfil()).isEqualTo(Perfil.GARCOM);
        assertThat(salvo.getPercentualComissao()).isEqualByComparingTo("10.00");
        verify(repository).save(any(Usuario.class));
    }

    @Test
    void criar_garcomSemComissao_deveLancar() {
        UsuarioCreateRequest req = new UsuarioCreateRequest(
                "joao", "João", "senha123", Perfil.GARCOM, null);
        when(repository.existsByLogin("joao")).thenReturn(false);

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("percentualComissao");
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void criar_caixaComComissao_deveLancar() {
        UsuarioCreateRequest req = new UsuarioCreateRequest(
                "caixa", "Caixa", "senha123", Perfil.CAIXA, new BigDecimal("5.00"));
        when(repository.existsByLogin("caixa")).thenReturn(false);

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("só é aplicável a perfil = GARCOM");
    }

    @Test
    void criar_loginDuplicado_deveLancar() {
        UsuarioCreateRequest req = new UsuarioCreateRequest(
                "admin", "Admin", "senha123", Perfil.ADMINISTRADOR, null);
        when(repository.existsByLogin("admin")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("admin");
    }

    @Test
    void buscarPorLogin_deveRetornarUsuarioQuandoExiste() {
        Usuario u = Usuario.builder().id(1L).login("admin").nome("Admin").senha("x")
                .perfil(Perfil.ADMINISTRADOR).ativo(true).build();
        when(repository.findByLogin("admin")).thenReturn(Optional.of(u));

        assertThat(service.buscarPorLogin("admin").getId()).isEqualTo(1L);
    }
}
