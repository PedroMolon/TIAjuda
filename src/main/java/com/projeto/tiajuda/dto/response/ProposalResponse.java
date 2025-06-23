package com.projeto.tiajuda.dto.response;

import com.projeto.tiajuda.entity.enums.ProposalStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record ProposalResponse(

        Long id,
        Long serviceRequestId,
        String serviceRequestTitle,
        Long technicianId,
        String technicianName,
        BigDecimal price,
        String description,
        ProposalStatus status,
        Instant createdAt,
        Instant updatedAt

) {
}
