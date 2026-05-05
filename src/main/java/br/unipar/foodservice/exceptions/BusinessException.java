package br.unipar.foodservice.exceptions;

/**
 * Erro previsível de regra de negócio (ex.: login já existe). Mapeado para HTTP 422.
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
