package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.UnidadeMedidaCreateRequest;
import br.unipar.foodservice.dtos.UnidadeMedidaPatchRequest;
import br.unipar.foodservice.dtos.UnidadeMedidaUpdateRequest;
import br.unipar.foodservice.entities.UnidadeMedida;
import br.unipar.foodservice.enums.TipoMedida;
import br.unipar.foodservice.exceptions.BusinessException;
import br.unipar.foodservice.exceptions.ResourceNotFoundException;
import br.unipar.foodservice.repositories.InsumoRepository;
import br.unipar.foodservice.repositories.UnidadeMedidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UnidadeMedidaService {

    private static final MathContext MC = MathContext.DECIMAL64;

    private final UnidadeMedidaRepository repository;
    private final InsumoRepository insumoRepository;

    @Transactional
    public UnidadeMedida criar(UnidadeMedidaCreateRequest request) {
        if (repository.existsByNomeIgnoreCase(request.nome())) {
            throw new BusinessException("Já existe uma unidade com o nome '" + request.nome() + "'.");
        }
        if (repository.existsBySimboloIgnoreCase(request.simbolo())) {
            throw new BusinessException("Já existe uma unidade com o símbolo '" + request.simbolo() + "'.");
        }
        if (request.fatorParaBase().compareTo(BigDecimal.ONE) == 0) {
            // tentando cadastrar uma nova base — só pode existir uma por tipoMedida.
            repository.findByTipoMedidaAndFatorParaBase(request.tipoMedida(), BigDecimal.ONE)
                    .ifPresent(existente -> {
                        throw new BusinessException("Já existe a unidade-base de "
                                + request.tipoMedida() + ": " + existente.getSimbolo() + ".");
                    });
        }
        UnidadeMedida unidade = UnidadeMedida.builder()
                .nome(request.nome())
                .simbolo(request.simbolo())
                .tipoMedida(request.tipoMedida())
                .fatorParaBase(request.fatorParaBase())
                .ativo(true)
                .build();
        return repository.save(unidade);
    }

    @Transactional(readOnly = true)
    public List<UnidadeMedida> listar(boolean apenasAtivas, TipoMedida tipo) {
        if (tipo != null) {
            return repository.findByTipoMedidaAndAtivoTrue(tipo);
        }
        return apenasAtivas ? repository.findByAtivoTrue() : repository.findAll();
    }

    @Transactional(readOnly = true)
    public UnidadeMedida buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UnidadeMedida não encontrada: " + id));
    }

    @Transactional
    public UnidadeMedida atualizar(Long id, UnidadeMedidaUpdateRequest request) {
        UnidadeMedida unidade = buscarPorId(id);

        if (!unidade.getNome().equalsIgnoreCase(request.nome()) && repository.existsByNomeIgnoreCase(request.nome())) {
            throw new BusinessException("Já existe outra unidade com o nome '" + request.nome() + "'.");
        }
        if (!unidade.getSimbolo().equalsIgnoreCase(request.simbolo()) && repository.existsBySimboloIgnoreCase(request.simbolo())) {
            throw new BusinessException("Já existe outra unidade com o símbolo '" + request.simbolo() + "'.");
        }
        if (Boolean.TRUE.equals(unidade.getAtivo()) && Boolean.FALSE.equals(request.ativo())) {
            validarPodeInativar(unidade);
        }
        unidade.setNome(request.nome());
        unidade.setSimbolo(request.simbolo());
        unidade.setAtivo(request.ativo());
        return unidade;
    }

    @Transactional
    public void inativar(Long id) {
        UnidadeMedida unidade = buscarPorId(id);
        if (!Boolean.TRUE.equals(unidade.getAtivo())) {
            return;
        }
        validarPodeInativar(unidade);
        unidade.setAtivo(false);
    }

    @Transactional
    public UnidadeMedida patch(Long id, UnidadeMedidaPatchRequest req) {
        UnidadeMedida unidade = buscarPorId(id);
        if (req.nome() != null && !unidade.getNome().equalsIgnoreCase(req.nome())) {
            if (repository.existsByNomeIgnoreCase(req.nome())) {
                throw new BusinessException("Já existe outra unidade com o nome '" + req.nome() + "'.");
            }
            unidade.setNome(req.nome());
        }
        if (req.simbolo() != null && !unidade.getSimbolo().equalsIgnoreCase(req.simbolo())) {
            if (repository.existsBySimboloIgnoreCase(req.simbolo())) {
                throw new BusinessException("Já existe outra unidade com o símbolo '" + req.simbolo() + "'.");
            }
            unidade.setSimbolo(req.simbolo());
        }
        if (req.ativo() != null) {
            if (Boolean.TRUE.equals(unidade.getAtivo()) && Boolean.FALSE.equals(req.ativo())) {
                validarPodeInativar(unidade);
            }
            unidade.setAtivo(req.ativo());
        }
        return unidade;
    }

    /**
     * Algoritmo central de conversão (4.5.1 do CLAUDE.md):
     *   qtdEmBase = quantidade × deUnidade.fatorParaBase
     *   retorno   = qtdEmBase / paraUnidade.fatorParaBase
     * Pré-condição: tipoMedida das duas unidades precisa ser igual.
     */
    public BigDecimal converter(BigDecimal quantidade, UnidadeMedida deUnidade, UnidadeMedida paraUnidade) {
        if (deUnidade.getTipoMedida() != paraUnidade.getTipoMedida()) {
            throw new BusinessException("Conversão impossível: " + deUnidade.getSimbolo()
                    + " (" + deUnidade.getTipoMedida() + ") e "
                    + paraUnidade.getSimbolo() + " (" + paraUnidade.getTipoMedida() + ").");
        }
        if (quantidade == null) {
            throw new BusinessException("Quantidade obrigatória para conversão.");
        }
        BigDecimal qtdEmBase = quantidade.multiply(deUnidade.getFatorParaBase(), MC);
        return qtdEmBase.divide(paraUnidade.getFatorParaBase(), MC);
    }

    /** Conveniência: converte para a unidade-base do tipoMedida. */
    public BigDecimal converterParaBase(BigDecimal quantidade, UnidadeMedida unidade) {
        return quantidade.multiply(unidade.getFatorParaBase(), MC);
    }

    private void validarPodeInativar(UnidadeMedida unidade) {
        if (insumoRepository.existsByUnidadePadraoIdAndAtivoTrue(unidade.getId())) {
            throw new BusinessException("Não é possível inativar a unidade '" + unidade.getSimbolo()
                    + "': existem insumos ativos usando-a como unidade padrão.");
        }
        if (unidade.ehBase()) {
            boolean temOutrasDoMesmoTipo = repository.findByTipoMedidaAndAtivoTrue(unidade.getTipoMedida())
                    .stream().anyMatch(u -> !u.getId().equals(unidade.getId()));
            if (temOutrasDoMesmoTipo) {
                throw new BusinessException("Não é possível inativar a unidade-base de "
                        + unidade.getTipoMedida() + " enquanto houver outras unidades desse tipo ativas.");
            }
        }
    }
}
