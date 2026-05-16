package br.unipar.foodservice.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Atualização parcial de Cliente. Semântica:
 * - Cada campo top-level: null = "não altera".
 * - endereco: se null, endereço não é tocado; se não-null, cada campo dentro
 *   também segue a regra "null = não altera".
 */
public record ClientePatchRequest(
        @Size(min = 2, max = 120)
        String nome,

        @Pattern(regexp = "^\\+?[0-9 ()\\-]{8,20}$",
                 message = "telefone deve conter de 8 a 20 caracteres válidos")
        String telefone,

        @Email @Size(max = 120)
        String email,

        @Valid
        EnderecoDto endereco,

        Boolean ativo
) {
}
