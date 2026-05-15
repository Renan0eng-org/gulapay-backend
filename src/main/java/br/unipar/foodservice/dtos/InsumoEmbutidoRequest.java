package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Bloco de dados de um {@code Insumo} embutido dentro do {@link ProdutoCreateRequest}.
 *
 * <p>Permite que o cliente cadastre um produto {@code UNITARIO} e seu insumo-espelho
 * em uma única requisição {@code POST /produtos}. Ver seção 4.5.2 do {@code CLAUDE.md}.
 *
 * <p>Diferenças em relação ao {@link InsumoCreateRequest} usado por {@code POST /insumos}:
 * <ul>
 *   <li>{@code nome} é <strong>opcional</strong> aqui — se omitido, o backend usa o
 *       mesmo nome do produto que está sendo criado.</li>
 *   <li>{@code unidadePadraoId} e {@code estoqueMinimo} continuam obrigatórios.</li>
 * </ul>
 *
 * <p>É mutuamente exclusivo com {@link ProdutoCreateRequest#insumoId()}. A validação
 * dessa exclusividade fica em {@code ProdutoService.resolverInsumoNaCriacao()}.
 */
public record InsumoEmbutidoRequest(
        @Size(min = 2, max = 120,
                message = "Se informado, nome deve ter entre 2 e 120 caracteres")
        String nome,

        @NotNull
        Long unidadePadraoId,

        @NotNull @DecimalMin(value = "0.000", message = "estoqueMinimo deve ser >= 0")
        BigDecimal estoqueMinimo
) {
}
