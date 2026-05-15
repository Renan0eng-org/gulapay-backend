package br.unipar.foodservice.dtos;

import br.unipar.foodservice.enums.SetorProducao;
import br.unipar.foodservice.enums.TipoProduto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Payload de {@code POST /produtos}.
 *
 * <p><strong>Regras dos campos {@code insumoId} e {@code insumo}</strong> (ver seção 4.5.2
 * do {@code CLAUDE.md}):
 *
 * <ul>
 *   <li>{@code UNITARIO} — informe <em>exatamente um</em> dos dois:
 *     <ul>
 *       <li>{@code insumoId} para reusar um insumo já cadastrado, ou</li>
 *       <li>{@code insumo} para criar um insumo-espelho na mesma transação.</li>
 *     </ul>
 *   </li>
 *   <li>{@code COMPOSTO} e {@code COMBO} — ambos devem ficar {@code null}. O consumo
 *       de estoque virá da {@code FichaTecnica} (Sprint 5) ou dos {@code ItemCombo}
 *       (Sprint 5), não do produto direto.</li>
 * </ul>
 *
 * <p>A validação de mutex e obrigatoriedade fica em
 * {@code ProdutoService.resolverInsumoNaCriacao()} — retorna {@code 400 Bad Request}
 * via {@code InvalidRequestException} se a combinação for inválida.
 */
public record ProdutoCreateRequest(
        @NotBlank @Size(min = 2, max = 120)
        String nome,

        @Size(max = 500)
        String descricao,

        @NotNull @DecimalMin(value = "0.00", message = "preco deve ser >= 0")
        BigDecimal preco,

        @NotNull
        TipoProduto tipoProduto,

        @NotNull
        SetorProducao setorProducao,

        @NotNull
        Long categoriaId,

        /**
         * FK para um Insumo já existente. Caminho de "reuso" para UNITARIO. Proibido
         * em COMPOSTO/COMBO. Mutuamente exclusivo com {@link #insumo()}.
         */
        Long insumoId,

        /**
         * Dados de um Insumo a ser criado na mesma transação. Caminho de "embutir" para
         * UNITARIO. Proibido em COMPOSTO/COMBO. Mutuamente exclusivo com {@link #insumoId()}.
         */
        @Valid
        InsumoEmbutidoRequest insumo
) {
}
