package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EntregadorCreateRequest(
        @NotBlank @Size(min = 2, max = 120)
        String nome,

        @NotBlank
        @Pattern(regexp = "^\\+?[0-9 ()\\-]{8,20}$")
        String telefone
) {
}
