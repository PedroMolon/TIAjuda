package com.projeto.tiajuda.dto.request;

import jakarta.validation.constraints.NotNull;

public record PaymentRequest(
        @NotNull(message = "O ID do serviço é obrigatório")
        Long serviceRequestId,

        @NotNull(message = "O método de pagamento é obrigatório")
        String paymentMethod
) {}
