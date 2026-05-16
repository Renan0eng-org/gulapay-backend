package br.unipar.foodservice.exceptions;

/**
 * Erro semântico no corpo de uma requisição que o servidor entendeu. Use
 * quando a URI existe e o JSON é bem-formado, mas o conteúdo tem um problema
 * de entrada — por exemplo:
 *
 * <ul>
 *   <li>Uma FK do body referencia uma entidade inexistente
 *       ({@code categoriaId}, {@code insumoId}, {@code loteId}, ...).</li>
 *   <li>Um parâmetro foi enviado em um contexto em que não se aplica.</li>
 * </ul>
 *
 * <p>Não use esta exception quando:
 * <ul>
 *   <li>O ID está na URI ({@code GET /produtos/999}) — use
 *       {@link ResourceNotFoundException} (404).</li>
 *   <li>O conteúdo viola uma regra de negócio do domínio (insumo inativo,
 *       tipo de produto não suportado, etc.) — use
 *       {@link BusinessException} (422).</li>
 * </ul>
 *
 * <p>Mapeada para HTTP 400 (Bad Request).
 */
public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
