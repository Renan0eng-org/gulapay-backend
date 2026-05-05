package br.unipar.foodservice.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ClienteUpdateRequest(
        @NotBlank @Size(min = 2, max = 120)
        String nome,

        @NotBlank
        @Pattern(regexp = "^\\+?[0-9 ()\\-]{8,20}$")
        String telefone,

        @Email @Size(max = 120)
        String email,

        @Valid
        EnderecoDto endereco,

        @NotNull
        Boolean ativo
) {
}
