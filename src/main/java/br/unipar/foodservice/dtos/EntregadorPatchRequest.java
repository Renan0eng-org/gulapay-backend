package br.unipar.foodservice.dtos;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EntregadorPatchRequest(
        @Size(min = 2, max = 120) String nome,
        @Pattern(regexp = "^\\+?[0-9 ()\\-]{8,20}$") String telefone,
        Boolean ativo
) {
}
