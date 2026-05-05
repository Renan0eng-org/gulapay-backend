package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.ProdutoCreateRequest;
import br.unipar.foodservice.dtos.ProdutoUpdateRequest;
import br.unipar.foodservice.entities.Categoria;
import br.unipar.foodservice.entities.Produto;
import br.unipar.foodservice.enums.TipoProduto;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.exceptions.ResourceNotFoundException;
import br.unipar.foodservice.repositories.CategoriaRepository;
import br.unipar.foodservice.repositories.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository repository;
    private final CategoriaRepository categoriaRepository;

    @Transactional
    public Produto criar(ProdutoCreateRequest request) {
        Categoria categoria = categoriaRepository.findById(request.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + request.categoriaId()));
        validarTipoSuportado(request.tipoProduto());

        Produto produto = Produto.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .preco(request.preco())
                .tipoProduto(request.tipoProduto())
                .setorProducao(request.setorProducao())
                .categoria(categoria)
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
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + request.categoriaId()));
        validarTipoSuportado(request.tipoProduto());

        produto.setNome(request.nome());
        produto.setDescricao(request.descricao());
        produto.setPreco(request.preco());
        produto.setTipoProduto(request.tipoProduto());
        produto.setSetorProducao(request.setorProducao());
        produto.setCategoria(categoria);
        produto.setAtivo(request.ativo());
        return produto;
    }

    @Transactional
    public void inativar(Long id) {
        Produto produto = buscarPorId(id);
        produto.setAtivo(false);
    }

    /**
     * Etapa 1.1 entrega apenas produtos UNITARIO. COMPOSTO depende da Sprint 5
     * (ficha técnica) e COMBO depende de itens internos. Bloqueia tipos não
     * suportados ainda para evitar entrar em estado inconsistente.
     */
    private void validarTipoSuportado(TipoProduto tipo) {
        if (tipo != TipoProduto.UNITARIO) {
            throw new BusinessException("Tipo de produto " + tipo + " ainda não é suportado nesta versão da API. " +
                    "Apenas UNITARIO está disponível.");
        }
    }
}
