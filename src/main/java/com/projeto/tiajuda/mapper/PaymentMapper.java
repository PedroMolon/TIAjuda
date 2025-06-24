package com.projeto.tiajuda.mapper;

import com.projeto.tiajuda.dto.response.PaymentResponse;
import com.projeto.tiajuda.entity.Payment;

public class PaymentMapper {

    public static PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getRequest().getId(),
            payment.getAmount(),
            payment.getPaymentDate(),
            payment.getStatus(),
            payment.getTransactionId(),
            payment.getPaymentMethod()
        );
    }

}
