package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.Size;

/**
 * Endereço usado tanto em request quanto em response do Cliente.
 * Todos os campos são opcionais (cliente pode ser cadastrado sem endereço completo).
 */
public record EnderecoDto(
        @Size(max = 150) String logradouro,
        @Size(max = 20)  String numero,
        @Size(max = 80)  String complemento,
        @Size(max = 80)  String bairro,
        @Size(max = 80)  String cidade,
        @Size(min = 2, max = 2) String uf,
        @Size(max = 10)  String cep
) {
    public static EnderecoDto vazio() {
        return new EnderecoDto(null, null, null, null, null, null, null);
    }
}
