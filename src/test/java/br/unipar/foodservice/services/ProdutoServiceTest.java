package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.InsumoEmbutidoRequest;
import br.unipar.foodservice.dtos.ProdutoCreateRequest;
import br.unipar.foodservice.entities.Categoria;
import br.unipar.foodservice.entities.Insumo;
import br.unipar.foodservice.entities.Produto;
import br.unipar.foodservice.entities.UnidadeMedida;
import br.unipar.foodservice.enums.SetorProducao;
import br.unipar.foodservice.enums.TipoMedida;
import br.unipar.foodservice.enums.TipoProduto;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.exceptions.InvalidRequestException;
import br.unipar.foodservice.repositories.CategoriaRepository;
import br.unipar.foodservice.repositories.InsumoRepository;
import br.unipar.foodservice.repositories.ProdutoRepository;
import br.unipar.foodservice.repositories.UnidadeMedidaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock private ProdutoRepository repository;
    @Mock private CategoriaRepository categoriaRepository;
    @Mock private InsumoRepository insumoRepository;
    @Mock private UnidadeMedidaRepository unidadeRepository;

    @InjectMocks
    private ProdutoService service;

    // ----- Helpers de fixtures -----

    private Categoria categoria() {
        return Categoria.builder().id(3L).nome("Bebidas").ativo(true).build();
    }

    private UnidadeMedida unidadeUn() {
        return UnidadeMedida.builder()
                .id(7L).simbolo("un").nome("Unidade")
                .tipoMedida(TipoMedida.UNIDADE).fatorParaBase(BigDecimal.ONE)
                .ativo(true).build();
    }

    private Insumo cocaInsumo() {
        return Insumo.builder()
                .id(42L).nome("Coca-Cola 600ml")
                .unidadePadrao(unidadeUn()).estoqueMinimo(BigDecimal.ZERO)
                .ativo(true).build();
    }

    private ProdutoCreateRequest unitarioComInsumoEmbutido(String nomeProduto, String nomeInsumo) {
        return new ProdutoCreateRequest(
                nomeProduto, "Refrigerante PET 600ml", new BigDecimal("8.50"),
                TipoProduto.UNITARIO, SetorProducao.BAR, 3L,
                null,
                new InsumoEmbutidoRequest(nomeInsumo, 7L, new BigDecimal("5.000"))
        );
    }

    private ProdutoCreateRequest unitarioComInsumoId(Long insumoId) {
        return new ProdutoCreateRequest(
                "Coca-Cola 600ml (combo)", null, new BigDecimal("7.00"),
                TipoProduto.UNITARIO, SetorProducao.BAR, 3L,
                insumoId, null
        );
    }

    // ----- Caminho A: insumo embutido cria Insumo + Produto -----

    @Test
    void criar_unitarioComInsumoEmbutido_deveCriarInsumoEProduto() {
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoria()));
        when(unidadeRepository.findById(7L)).thenReturn(Optional.of(unidadeUn()));
        when(insumoRepository.save(any(Insumo.class))).thenAnswer(inv -> {
            Insumo i = inv.getArgument(0);
            i.setId(99L);
            return i;
        });
        when(repository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProdutoCreateRequest req = unitarioComInsumoEmbutido("Coca-Cola 600ml", "Coca-Cola 600ml");
        Produto produto = service.criar(req);

        // Verifica que o Insumo foi criado com os dados corretos.
        ArgumentCaptor<Insumo> insumoCaptor = ArgumentCaptor.forClass(Insumo.class);
        verify(insumoRepository).save(insumoCaptor.capture());
        Insumo insumoSalvo = insumoCaptor.getValue();
        assertThat(insumoSalvo.getNome()).isEqualTo("Coca-Cola 600ml");
        assertThat(insumoSalvo.getUnidadePadrao().getId()).isEqualTo(7L);
        assertThat(insumoSalvo.getEstoqueMinimo()).isEqualByComparingTo("5");
        assertThat(insumoSalvo.getAtivo()).isTrue();

        // Verifica que o Produto foi criado linkado ao Insumo recém-criado.
        assertThat(produto.getNome()).isEqualTo("Coca-Cola 600ml");
        assertThat(produto.getInsumo()).isSameAs(insumoSalvo);
        assertThat(produto.getInsumo().getId()).isEqualTo(99L);
        assertThat(produto.getAtivo()).isTrue();
    }

    @Test
    void criar_unitarioComInsumoEmbutidoSemNome_deveHerdarNomeDoProduto() {
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoria()));
        when(unidadeRepository.findById(7L)).thenReturn(Optional.of(unidadeUn()));
        when(insumoRepository.save(any(Insumo.class))).thenAnswer(inv -> inv.getArgument(0));
        when(repository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProdutoCreateRequest req = unitarioComInsumoEmbutido("Coca-Cola 600ml", null);
        service.criar(req);

        ArgumentCaptor<Insumo> insumoCaptor = ArgumentCaptor.forClass(Insumo.class);
        verify(insumoRepository).save(insumoCaptor.capture());
        assertThat(insumoCaptor.getValue().getNome()).isEqualTo("Coca-Cola 600ml");
    }

    @Test
    void criar_unitarioComInsumoEmbutidoNomeEmBranco_deveHerdarNomeDoProduto() {
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoria()));
        when(unidadeRepository.findById(7L)).thenReturn(Optional.of(unidadeUn()));
        when(insumoRepository.save(any(Insumo.class))).thenAnswer(inv -> inv.getArgument(0));
        when(repository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        ProdutoCreateRequest req = unitarioComInsumoEmbutido("Cerveja Heineken 600ml", "   ");
        service.criar(req);

        ArgumentCaptor<Insumo> insumoCaptor = ArgumentCaptor.forClass(Insumo.class);
        verify(insumoRepository).save(insumoCaptor.capture());
        assertThat(insumoCaptor.getValue().getNome()).isEqualTo("Cerveja Heineken 600ml");
    }

    // ----- Caminho B: insumoId reutiliza insumo existente -----

    @Test
    void criar_unitarioComInsumoId_deveReusarInsumoExistente() {
        Insumo existente = cocaInsumo();
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoria()));
        when(insumoRepository.findById(42L)).thenReturn(Optional.of(existente));
        when(repository.save(any(Produto.class))).thenAnswer(inv -> inv.getArgument(0));

        Produto produto = service.criar(unitarioComInsumoId(42L));

        verify(insumoRepository, never()).save(any(Insumo.class));
        assertThat(produto.getInsumo()).isSameAs(existente);
    }

    // ----- Erros 400 (InvalidRequestException) -----

    @Test
    void criar_unitarioSemInsumoIdNemInsumo_deveLancar400() {
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoria()));

        ProdutoCreateRequest req = new ProdutoCreateRequest(
                "Coca-Cola 600ml", null, new BigDecimal("8.50"),
                TipoProduto.UNITARIO, SetorProducao.BAR, 3L,
                null, null
        );

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("informe insumoId ou os dados do insumo");
    }

    @Test
    void criar_unitarioComAmbosInsumoIdEInsumo_deveLancar400() {
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoria()));

        ProdutoCreateRequest req = new ProdutoCreateRequest(
                "Coca-Cola 600ml", null, new BigDecimal("8.50"),
                TipoProduto.UNITARIO, SetorProducao.BAR, 3L,
                42L,
                new InsumoEmbutidoRequest(null, 7L, BigDecimal.ZERO)
        );

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("apenas insumoId");
    }

    @Test
    void criar_unitarioComInsumoIdInexistente_deveLancar400() {
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoria()));
        when(insumoRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(unitarioComInsumoId(999L)))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Insumo não encontrado: 999");
    }

    @Test
    void criar_unitarioComUnidadeInexistente_deveLancar400() {
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoria()));
        when(unidadeRepository.findById(7L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(unitarioComInsumoEmbutido("Coca-Cola 600ml", null)))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("UnidadeMedida não encontrada: 7");
    }

    @Test
    void criar_unitarioComCategoriaInexistente_deveLancar400() {
        when(categoriaRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.criar(unitarioComInsumoEmbutido("Coca-Cola 600ml", null)))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("Categoria não encontrada");
    }

    // ----- Erros 422 (BusinessException) -----

    @Test
    void criar_unitarioComInsumoInativo_deveLancar422() {
        Insumo inativo = cocaInsumo();
        inativo.setAtivo(false);
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoria()));
        when(insumoRepository.findById(42L)).thenReturn(Optional.of(inativo));

        assertThatThrownBy(() -> service.criar(unitarioComInsumoId(42L)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("está inativo");
    }

    @Test
    void criar_unitarioComUnidadeInativa_deveLancar422() {
        UnidadeMedida inativa = unidadeUn();
        inativa.setAtivo(false);
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoria()));
        when(unidadeRepository.findById(7L)).thenReturn(Optional.of(inativa));

        assertThatThrownBy(() -> service.criar(unitarioComInsumoEmbutido("Coca-Cola 600ml", null)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("está inativa");
    }

    @Test
    void criar_compostoOuCombo_deveSerBloqueadoNaSprintAtual() {
        // Hoje validarTipoSuportado() barra COMPOSTO/COMBO com 422 antes mesmo
        // de chegar em resolverInsumoNaCriacao. Quando Sprint 5 entrar e desbloquear,
        // este teste vira "COMPOSTO/COMBO + insumo/insumoId = 400".
        when(categoriaRepository.findById(3L)).thenReturn(Optional.of(categoria()));

        ProdutoCreateRequest req = new ProdutoCreateRequest(
                "Hambúrguer Artesanal", "Pão + carne + queijo", new BigDecimal("32.00"),
                TipoProduto.COMPOSTO, SetorProducao.COZINHA, 3L,
                null, null
        );

        assertThatThrownBy(() -> service.criar(req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("COMPOSTO");
    }
}
