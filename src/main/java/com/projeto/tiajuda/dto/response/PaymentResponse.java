package com.projeto.tiajuda.dto.response;

import com.projeto.tiajuda.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(
        Long id,
        Long serviceRequestId,
        BigDecimal amount,
        Instant paymentDate,
        PaymentStatus status,
        String transactionalId,
        String paymentMethod
) {}
