package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.MesaCreateRequest;
import br.unipar.foodservice.dtos.MesaUpdateRequest;
import br.unipar.foodservice.entities.Mesa;
import br.unipar.foodservice.enums.StatusMesa;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.exceptions.ResourceNotFoundException;
import br.unipar.foodservice.repositories.MesaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MesaService {

    private final MesaRepository repository;

    @Transactional
    public Mesa criar(MesaCreateRequest request) {
        if (repository.existsByNumero(request.numero())) {
            throw new BusinessException("Já existe uma mesa com o número '" + request.numero() + "'.");
        }
        Mesa mesa = Mesa.builder()
                .numero(request.numero())
                .descricao(request.descricao())
                .capacidade(request.capacidade())
                .status(StatusMesa.LIVRE)
                .ativo(true)
                .build();
        return repository.save(mesa);
    }

    @Transactional(readOnly = true)
    public List<Mesa> listar(boolean apenasAtivas) {
        return apenasAtivas ? repository.findByAtivoTrue() : repository.findAll();
    }

    @Transactional(readOnly = true)
    public Mesa buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mesa não encontrada: " + id));
    }

    @Transactional
    public Mesa atualizar(Long id, MesaUpdateRequest request) {
        Mesa mesa = buscarPorId(id);
        if (!mesa.getNumero().equals(request.numero()) && repository.existsByNumero(request.numero())) {
            throw new BusinessException("Já existe outra mesa com o número '" + request.numero() + "'.");
        }
        mesa.setNumero(request.numero());
        mesa.setDescricao(request.descricao());
        mesa.setCapacidade(request.capacidade());
        mesa.setAtivo(request.ativo());
        return mesa;
    }

    @Transactional
    public void inativar(Long id) {
        Mesa mesa = buscarPorId(id);
        if (mesa.getStatus() != StatusMesa.LIVRE && mesa.getStatus() != StatusMesa.FECHADA) {
            throw new BusinessException("Não é possível inativar uma mesa com status " + mesa.getStatus()
                    + ". Encerre as comandas vinculadas antes.");
        }
        mesa.setAtivo(false);
    }
}
