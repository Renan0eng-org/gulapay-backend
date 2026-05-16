package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.CategoriaCreateRequest;
import br.unipar.foodservice.dtos.CategoriaPatchRequest;
import br.unipar.foodservice.dtos.CategoriaUpdateRequest;
import br.unipar.foodservice.entities.Categoria;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.exceptions.ResourceNotFoundException;
import br.unipar.foodservice.repositories.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository repository;

    @Transactional
    public Categoria criar(CategoriaCreateRequest request) {
        if (repository.existsByNomeIgnoreCase(request.nome())) {
            throw new BusinessException("Já existe uma categoria com o nome '" + request.nome() + "'.");
        }
        Categoria categoria = Categoria.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .ativo(true)
                .build();
        return repository.save(categoria);
    }

    @Transactional(readOnly = true)
    public List<Categoria> listar(boolean apenasAtivas) {
        return apenasAtivas ? repository.findByAtivoTrue() : repository.findAll();
    }

    @Transactional(readOnly = true)
    public Categoria buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + id));
    }

    @Transactional
    public Categoria atualizar(Long id, CategoriaUpdateRequest request) {
        Categoria categoria = buscarPorId(id);
        repository.findByNomeIgnoreCase(request.nome())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new BusinessException("Já existe outra categoria com o nome '" + request.nome() + "'.");
                });
        categoria.setNome(request.nome());
        categoria.setDescricao(request.descricao());
        categoria.setAtivo(request.ativo());
        return categoria;
    }

    @Transactional
    public void inativar(Long id) {
        Categoria categoria = buscarPorId(id);
        categoria.setAtivo(false);
    }

    /**
     * Atualização parcial. Cada campo nulo no request mantém o valor atual.
     * Útil para reativar via {@code PATCH /categorias/{id}} com {@code {"ativo": true}}.
     */
    @Transactional
    public Categoria patch(Long id, CategoriaPatchRequest req) {
        Categoria categoria = buscarPorId(id);
        if (req.nome() != null && !req.nome().equalsIgnoreCase(categoria.getNome())) {
            repository.findByNomeIgnoreCase(req.nome())
                    .filter(c -> !c.getId().equals(id))
                    .ifPresent(c -> {
                        throw new BusinessException("Já existe outra categoria com o nome '" + req.nome() + "'.");
                    });
            categoria.setNome(req.nome());
        }
        if (req.descricao() != null) categoria.setDescricao(req.descricao());
        if (req.ativo() != null) categoria.setAtivo(req.ativo());
        return categoria;
    }
}
