package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.InsumoEmbutidoRequest;
import br.unipar.foodservice.dtos.ProdutoCreateRequest;
import br.unipar.foodservice.dtos.ProdutoPatchRequest;
import br.unipar.foodservice.dtos.ProdutoUpdateRequest;
import br.unipar.foodservice.entities.Categoria;
import br.unipar.foodservice.entities.Insumo;
import br.unipar.foodservice.entities.Produto;
import br.unipar.foodservice.entities.UnidadeMedida;
import br.unipar.foodservice.enums.TipoProduto;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.exceptions.InvalidRequestException;
import br.unipar.foodservice.exceptions.ResourceNotFoundException;
import br.unipar.foodservice.repositories.CategoriaRepository;
import br.unipar.foodservice.repositories.InsumoRepository;
import br.unipar.foodservice.repositories.ProdutoRepository;
import br.unipar.foodservice.repositories.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final InsumoRepository insumoRepository;
    private final UnidadeMedidaRepository unidadeRepository;

    @Transactional
    public Produto criar(ProdutoCreateRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new InvalidRequestException("Categoria não encontrada: " + request.categoriaId()));
        validarTipoSuportado(request.tipoProduto());
        Insumo insumo = resolverInsumoNaCriacao(request);

        Produto produto = Produto.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .preco(request.preco())
                .tipoProduto(request.tipoProduto())
                .setorProducao(request.setorProducao())
                .categoria(categoria)
                .insumo(insumo)
                .ativo(true)
                .build();
        return repository.save(produto);
    }

    @Transactional(readOnly = true)
    public List<Produto> listar(boolean apenasAtivos) {
        return apenasAtivos ? repository.findByAtivoTrue() : repository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Produto> listarPorCategoria(Long categoriaId) {
        return repository.findByCategoriaIdAndAtivoTrue(categoriaId);
    }

    @Transactional(readOnly = true)
    public Produto buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + id));
    }

    @Transactional
    public Produto atualizar(Long id, ProdutoUpdateRequest request) {
        Produto produto = buscarPorId(id);
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new InvalidRequestException("Categoria não encontrada: " + request.categoriaId()));
        validarTipoSuportado(request.tipoProduto());
        Insumo insumo = resolverInsumo(request.tipoProduto(), request.insumoId());

        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setTipoProduto(request.tipoProduto());
        produto.setSetorProducao(request.setorProducao());
        produto.setCategoria(categoria);
        produto.setInsumo(insumo);
        produto.setAtivo(request.ativo());
        return produto;
    }

    @Transactional
    public void inativar(Long id) {
        Produto produto = buscarPorId(id);
        produto.setAtivo(false);
    }

    /**
     * Atualização parcial. Quando tipoProduto ou insumoId mudam, valida a regra
     * UNITARIO ⇄ insumoId. Para "limpar" o insumo (ex.: virar COMPOSTO), use PUT.
     */
    @Transactional
    public Produto patch(Long id, ProdutoPatchRequest req) {
        Produto produto = buscarPorId(id);

        if (req.nome() != null) produto.setNome(req.nome());
        if (req.descricao() != null) produto.setDescricao(req.descricao());
        if (req.preco() != null) produto.setPreco(req.preco());
        if (req.setorProducao() != null) produto.setSetorProducao(req.setorProducao());
        if (req.categoriaId() != null) {
            Categoria nova = categoriaRepository.findById(req.categoriaId())
                    .orElseThrow(() -> new InvalidRequestException("Categoria não encontrada: " + req.categoriaId()));
            produto.setCategoria(nova);
        }
        if (req.tipoProduto() != null || req.insumoId() != null) {
            TipoProduto tipoFinal = req.tipoProduto() != null ? req.tipoProduto() : produto.getTipoProduto();
            Long insumoIdFinal = req.insumoId() != null
                    ? req.insumoId()
                    : (produto.getInsumo() == null ? null : produto.getInsumo().getId());
            validarTipoSuportado(tipoFinal);
            produto.setTipoProduto(tipoFinal);
            produto.setInsumo(resolverInsumo(tipoFinal, insumoIdFinal));
        }
        if (req.ativo() != null) produto.setAtivo(req.ativo());
        return produto;
    }

    /**
     * Etapa 1.1 entrega apenas produtos UNITARIO. COMPOSTO depende da Sprint 5
     * (ficha técnica) e COMBO depende de itens internos.
     */
    private void validarTipoSuportado(TipoProduto tipo) {
        if (tipo != TipoProduto.UNITARIO) {
            throw new BusinessException("Tipo de produto " + tipo + " ainda não é suportado nesta versão da API. " +
                    "Apenas UNITARIO está disponível.");
        }
    }

    /**
     * Específico do {@code POST /produtos}. Estende {@link #resolverInsumo(TipoProduto, Long)}
     * para aceitar também os dados do insumo embutidos no request (caminho de criar
     * insumo na mesma transação). Regras:
     *
     * <ul>
     *   <li>{@code COMPOSTO} / {@code COMBO}: nem {@code insumoId} nem {@code insumo} são
     *       aceitos. Retorna {@code null}.</li>
     *   <li>{@code UNITARIO} com {@code insumoId} preenchido: reaproveita a validação
     *       de {@link #resolverInsumo(TipoProduto, Long)}.</li>
     *   <li>{@code UNITARIO} com {@code insumo} preenchido: cria um Insumo novo, com
     *       {@code nome} caindo no nome do produto se omitido.</li>
     *   <li>{@code UNITARIO} com nenhum dos dois ou com os dois: {@code 400}.</li>
     * </ul>
     */
    private Insumo resolverInsumoNaCriacao(ProdutoCreateRequest req) {
        TipoProduto tipo = req.tipoProduto();

        if (tipo != TipoProduto.UNITARIO) {
            if (req.insumoId() != null || req.insumo() != null) {
                throw new InvalidRequestException(
                        "insumo/insumoId não são aplicáveis a tipoProduto = " + tipo + ".");
            }
            return null;
        }

        // UNITARIO daqui em diante.
        boolean temId = req.insumoId() != null;
        boolean temDados = req.insumo() != null;

        if (temId && temDados) {
            throw new InvalidRequestException(
                    "Informe apenas insumoId (reusar existente) OU insumo (criar novo), não ambos.");
        }
        if (!temId && !temDados) {
            throw new InvalidRequestException(
                    "Para tipoProduto = UNITARIO informe insumoId ou os dados do insumo a criar.");
        }
        if (temId) {
            return resolverInsumo(tipo, req.insumoId());
        }

        // Caminho do insumo embutido — cria na mesma transação do produto.
        InsumoEmbutidoRequest dados = req.insumo();
        UnidadeMedida unidade = unidadeRepository.findById(dados.unidadePadraoId())
                .orElseThrow(() -> new InvalidRequestException(
                        "UnidadeMedida não encontrada: " + dados.unidadePadraoId()));
        if (!Boolean.TRUE.equals(unidade.getAtivo())) {
            throw new BusinessException(
                    "UnidadeMedida '" + unidade.getSimbolo() + "' está inativa.");
        }
        String nomeFinal = (dados.nome() == null || dados.nome().isBlank())
                ? req.nome()
                : dados.nome();
        Insumo novo = Insumo.builder()
                .nome(nomeFinal)
                .unidadePadrao(unidade)
                .estoqueMinimo(dados.estoqueMinimo())
                .ativo(true)
                .build();
        return insumoRepository.save(novo);
    }

    /**
     * Resolve o Insumo conforme regra 4.5.2:
     *   UNITARIO  -> insumoId obrigatório, valida existência e atividade.
     *   COMPOSTO  -> insumoId proibido (estoque vem da ficha técnica).
     *   COMBO     -> insumoId proibido (estoque vem dos itens do combo).
     *
     * <p>Usado por {@code atualizar()} e {@code patch()} — o caminho de criar
     * insumo embutido só vale no {@code POST}; PUT/PATCH continuam exigindo
     * {@code insumoId} explícito.
     */
    private Insumo resolverInsumo(TipoProduto tipo, Long insumoId) {
        if (tipo == TipoProduto.UNITARIO) {
            if (insumoId == null) {
                throw new BusinessException("insumoId é obrigatório para tipoProduto = UNITARIO.");
            }
            Insumo insumo = insumoRepository.findById(insumoId)
                    .orElseThrow(() -> new InvalidRequestException("Insumo não encontrado: " + insumoId));
            if (!Boolean.TRUE.equals(insumo.getAtivo())) {
                throw new BusinessException("Insumo '" + insumo.getNome() + "' está inativo.");
            }
            return insumo;
        }
        if (insumoId != null) {
            throw new BusinessException("insumoId não é aplicável a tipoProduto = " + tipo + ".");
        }
        return null;
    }
}
