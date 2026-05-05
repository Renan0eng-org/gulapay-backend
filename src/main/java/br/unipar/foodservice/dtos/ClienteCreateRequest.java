package br.unipar.foodservice.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteCreateRequest(
        @NotBlank @Size(min = 2, max = 120)
        String nome,

        @NotBlank
        @Pattern(regexp = "^\\+?[0-9 ()\\-]{8,20}$",
                 message = "telefone deve conter de 8 a 20 caracteres válidos (dígitos, +, -, espaços, parênteses)")
        String telefone,

        @Email @Size(max = 120)
        String email,

        @Valid
        EnderecoDto endereco
) {
}
