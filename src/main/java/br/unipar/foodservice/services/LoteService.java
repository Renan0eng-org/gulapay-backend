package br.unipar.foodservice.services;

import br.unipar.foodservice.dtos.LoteCreateRequest;
import br.unipar.foodservice.dtos.LotePatchRequest;
import br.unipar.foodservice.dtos.LoteUpdateRequest;
import br.unipar.foodservice.entities.Insumo;
import br.unipar.foodservice.entities.Lote;
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
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository repository;
    private final InsumoRepository insumoRepository;
    private final UnidadeMedidaRepository unidadeRepository;
    private final UnidadeMedidaService unidadeService;

    @Transactional
    public Lote criar(LoteCreateRequest request) {
        Insumo insumo = insumoRepository.findById(request.insumoId())
                .orElseThrow(() -> new InvalidRequestException("Insumo não encontrado: " + request.insumoId()));
        UnidadeMedida unidadeInformada = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new InvalidRequestException("UnidadeMedida não encontrada: " + request.unidadeId()));
        validarCompatibilidade(insumo, unidadeInformada);
        if (request.validade().isBefore(LocalDate.now())) {
            throw new BusinessException("Validade do lote não pode estar no passado.");
        }

        BigDecimal qtdNaUnidadePadrao = unidadeService.converter(
                request.quantidadeInicial(), unidadeInformada, insumo.getUnidadePadrao());

        Lote lote = Lote.builder()
                .insumo(insumo)
                .codigo(request.codigo())
                .validade(request.validade())
                .quantidadeInicial(qtdNaUnidadePadrao)
                .quantidadeRestante(qtdNaUnidadePadrao)
                .custoUnitario(request.custoUnitario())
                .ativo(true)
                .build();
        return repository.save(lote);
    }

    @Transactional(readOnly = true)
    public List<Lote> listarPorInsumo(Long insumoId) {
        return repository.findByInsumoIdAndAtivoTrueOrderByValidadeAscIdAsc(insumoId);
    }

    @Transactional(readOnly = true)
    public Lote buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lote não encontrado: " + id));
    }

    @Transactional
    public Lote atualizar(Long id, LoteUpdateRequest request) {
        Lote lote = buscarPorId(id);
        lote.setCodigo(request.codigo());
        lote.setValidade(request.validade());
        lote.setAtivo(request.ativo());
        return lote;
    }

    @Transactional
    public void inativar(Long id) {
        Lote lote = buscarPorId(id);
        if (lote.getQuantidadeRestante().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Não é possível inativar um lote com saldo. " +
                    "Use AJUSTE_INVENTARIO ou SAIDA_PERDA antes.");
        }
        lote.setAtivo(false);
    }

    @Transactional
    public Lote patch(Long id, LotePatchRequest req) {
        Lote lote = buscarPorId(id);
        if (req.codigo() != null) lote.setCodigo(req.codigo());
        if (req.validade() != null) lote.setValidade(req.validade());
        if (req.ativo() != null) {
            if (Boolean.FALSE.equals(req.ativo())
                    && lote.getQuantidadeRestante().compareTo(BigDecimal.ZERO) > 0) {
                throw new BusinessException("Não é possível inativar um lote com saldo.");
            }
            lote.setAtivo(req.ativo());
        }
        return lote;
    }

    private void validarCompatibilidade(Insumo insumo, UnidadeMedida unidadeInformada) {
        if (insumo.getUnidadePadrao().getTipoMedida() != unidadeInformada.getTipoMedida()) {
            throw new BusinessException("Unidade " + unidadeInformada.getSimbolo()
                    + " (" + unidadeInformada.getTipoMedida() + ") não é compatível com a unidade padrão do insumo "
                    + insumo.getNome() + " (" + insumo.getUnidadePadrao().getSimbolo() + ").");
        }
    }
}
