package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.CatalogoCategoriaResponse;
import br.unipar.foodservice.dtos.CatalogoItemResponse;
import br.unipar.foodservice.entities.Produto;
import br.unipar.foodservice.repositories.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatalogoService {

    private final ProdutoRepository produtoRepository;

    /**
     * Catálogo público (RF11) — apenas produtos ativos, agrupados por categoria.
     * Consumido por front-ends externos sem necessidade de autenticação.
     */
    @Transactional(readOnly = true)
    public List<CatalogoCategoriaResponse> montar() {
        Map<Long, List<Produto>> agrupados = produtoRepository.findByAtivoTrue().stream()
                .filter(p -> Boolean.TRUE.equals(p.getCategoria().getAtivo()))
                .collect(Collectors.groupingBy(p -> p.getCategoria().getId()));

        return agrupados.values().stream()
                .map(produtos -> {
                    var ref = produtos.getFirst().getCategoria();
                    var itens = produtos.stream()
                            .sorted(Comparator.comparing(Produto::getNome))
                            .map(CatalogoItemResponse::from)
                            .toList();
                    return new CatalogoCategoriaResponse(ref.getId(), ref.getNome(), itens);
                })
                .sorted(Comparator.comparing(CatalogoCategoriaResponse::categoria))
                .toList();
    }
}
