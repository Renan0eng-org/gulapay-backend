package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.EntregadorCreateRequest;
import br.unipar.foodservice.dtos.EntregadorPatchRequest;
import br.unipar.foodservice.dtos.EntregadorUpdateRequest;
import br.unipar.foodservice.entities.Entregador;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.exceptions.ResourceNotFoundException;
import br.unipar.foodservice.repositories.EntregadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntregadorService {

    private final EntregadorRepository repository;

    @Transactional
    public Entregador criar(EntregadorCreateRequest request) {
        Entregador novo = Entregador.builder()
                .nome(request.nome())
                .telefone(normalizarTelefone(request.telefone()))
                .ativo(true)
                .build();
        return repository.save(novo);
    }

    @Transactional(readOnly = true)
    public List<Entregador> listar(boolean apenasAtivos) {
        return apenasAtivos ? repository.findByAtivoTrue() : repository.findAll();
    }

    @Transactional(readOnly = true)
    public Entregador buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entregador não encontrado: " + id));
    }

    @Transactional
    public Entregador atualizar(Long id, EntregadorUpdateRequest request) {
        Entregador entregador = buscarPorId(id);
        entregador.setNome(request.nome());
        entregador.setTelefone(normalizarTelefone(request.telefone()));
        entregador.setAtivo(request.ativo());
        return entregador;
    }

    @Transactional
    public void inativar(Long id) {
        Entregador entregador = buscarPorId(id);
        entregador.setAtivo(false);
    }

    @Transactional
    public Entregador patch(Long id, EntregadorPatchRequest req) {
        Entregador entregador = buscarPorId(id);
        if (req.nome() != null) entregador.setNome(req.nome());
        if (req.telefone() != null) entregador.setTelefone(normalizarTelefone(req.telefone()));
        if (req.ativo() != null) entregador.setAtivo(req.ativo());
        return entregador;
    }

    private String normalizarTelefone(String entrada) {
        String somenteDigitos = entrada == null ? "" : entrada.replaceAll("\\D", "");
        if (somenteDigitos.length() < 8 || somenteDigitos.length() > 15) {
            throw new BusinessException("Telefone inválido após normalização: '" + somenteDigitos + "'.");
        }
        return somenteDigitos;
    }
}
