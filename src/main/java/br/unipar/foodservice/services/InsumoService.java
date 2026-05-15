package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.InsumoCreateRequest;
import br.unipar.foodservice.dtos.InsumoPatchRequest;
import br.unipar.foodservice.dtos.InsumoUpdateRequest;
import br.unipar.foodservice.entities.Insumo;
import br.unipar.foodservice.entities.UnidadeMedida;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.exceptions.InvalidRequestException;
import br.unipar.foodservice.exceptions.ResourceNotFoundException;
import br.unipar.foodservice.repositories.InsumoRepository;
import br.unipar.foodservice.repositories.LoteRepository;
import br.unipar.foodservice.repositories.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InsumoService {

    private final InsumoRepository repository;
    private final UnidadeMedidaRepository unidadeRepository;
    private final LoteRepository loteRepository;

    @Transactional
    public Insumo criar(InsumoCreateRequest request) {
        UnidadeMedida unidade = unidadeRepository.findById(request.unidadePadraoId())
                .orElseThrow(() -> new InvalidRequestException("UnidadeMedida não encontrada: " + request.unidadePadraoId()));
        if (!Boolean.TRUE.equals(unidade.getAtivo())) {
            throw new BusinessException("UnidadeMedida '" + unidade.getSimbolo() + "' está inativa.");
        }
        Insumo insumo = Insumo.builder()
                .nome(request.nome())
                .unidadePadrao(unidade)
                .estoqueMinimo(request.estoqueMinimo())
                .ativo(true)
                .build();
        return repository.save(insumo);
    }

    @Transactional(readOnly = true)
    public List<Insumo> listar(boolean apenasAtivos) {
        return apenasAtivos ? repository.findByAtivoTrue() : repository.findAll();
    }

    @Transactional(readOnly = true)
    public Insumo buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Insumo não encontrado: " + id));
    }

    @Transactional
    public Insumo atualizar(Long id, InsumoUpdateRequest request) {
        Insumo insumo = buscarPorId(id);
        UnidadeMedida unidade = unidadeRepository.findById(request.unidadePadraoId())
                .orElseThrow(() -> new InvalidRequestException("UnidadeMedida não encontrada: " + request.unidadePadraoId()));

        if (!insumo.getUnidadePadrao().getId().equals(unidade.getId())) {
            // Trocar a unidade-padrão de um insumo com lotes existentes pode invalidar
            // os saldos. Bloqueamos no MVP — para mudar, faça ajuste de inventário antes.
            if (loteRepository.existsByInsumoIdAndAtivoTrue(insumo.getId())) {
                throw new BusinessException("Não é possível trocar a unidade padrão: o insumo possui lotes ativos.");
            }
            if (insumo.getUnidadePadrao().getTipoMedida() != unidade.getTipoMedida()) {
                throw new BusinessException("Nova unidade padrão é de tipo "
                        + unidade.getTipoMedida() + " enquanto a atual é "
                        + insumo.getUnidadePadrao().getTipoMedida() + ".");
            }
        }
        insumo.setNome(request.nome());
        insumo.setUnidadePadrao(unidade);
        insumo.setEstoqueMinimo(request.estoqueMinimo());
        insumo.setAtivo(request.ativo());
        return insumo;
    }

    @Transactional
    public void inativar(Long id) {
        Insumo insumo = buscarPorId(id);
        insumo.setAtivo(false);
    }

    @Transactional
    public Insumo patch(Long id, InsumoPatchRequest req) {
        Insumo insumo = buscarPorId(id);
        if (req.nome() != null) insumo.setNome(req.nome());
        if (req.unidadePadraoId() != null
                && !insumo.getUnidadePadrao().getId().equals(req.unidadePadraoId())) {
            UnidadeMedida nova = unidadeRepository.findById(req.unidadePadraoId())
                    .orElseThrow(() -> new InvalidRequestException("UnidadeMedida não encontrada: " + req.unidadePadraoId()));
            if (loteRepository.existsByInsumoIdAndAtivoTrue(insumo.getId())) {
                throw new BusinessException("Não é possível trocar a unidade padrão: o insumo possui lotes ativos.");
            }
            if (insumo.getUnidadePadrao().getTipoMedida() != nova.getTipoMedida()) {
                throw new BusinessException("Nova unidade é de tipo " + nova.getTipoMedida()
                        + " enquanto a atual é " + insumo.getUnidadePadrao().getTipoMedida() + ".");
            }
            insumo.setUnidadePadrao(nova);
        }
        if (req.estoqueMinimo() != null) insumo.setEstoqueMinimo(req.estoqueMinimo());
        if (req.ativo() != null) insumo.setAtivo(req.ativo());
        return insumo;
    }

    /**
     * Estoque atual = soma das quantidadesRestantes dos lotes ATIVOS do insumo.
     * Sempre retornado na unidadePadrao do insumo.
     */
    @Transactional(readOnly = true)
    public BigDecimal estoqueAtual(Long insumoId) {
        BigDecimal soma = loteRepository.somarQuantidadeRestantePorInsumoAtivo(insumoId);
        return soma == null ? BigDecimal.ZERO : soma;
    }

    /** Lista insumos cujo estoque atual está abaixo do mínimo (alerta operacional). */
    @Transactional(readOnly = true)
    public List<Insumo> listarAbaixoDoMinimo() {
        return repository.findByAtivoTrue().stream()
                .filter(i -> estoqueAtual(i.getId()).compareTo(i.getEstoqueMinimo()) < 0)
                .toList();
    }
}
