package com.projeto.tiajuda.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProposalUpdate(

        @NotNull(message = "O valor da proposta não pode ser nulo")
        @DecimalMin(value = "0.01", message = "O valor da proposta deve ser maior que zero")
        BigDecimal price,

        @NotBlank(message = "A descrição da proposta não pode estar vazia")
        String description

) {
}
