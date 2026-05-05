package br.unipar.foodservice.dtos;

import br.unipar.foodservice.enums.Perfil;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record UsuarioCreateRequest(
        @NotBlank @Size(min = 3, max = 60)
        String login,

        @NotBlank @Size(min = 3, max = 120)
        String nome,

        @NotBlank @Size(min = 6, max = 100, message = "senha deve ter no mínimo 6 caracteres")
        String senha,

        @NotNull
        Perfil perfil,

        @DecimalMin(value = "0.00", message = "percentualComissao deve ser >= 0")
        @DecimalMax(value = "100.00", message = "percentualComissao deve ser <= 100")
        BigDecimal percentualComissao
) {
}
