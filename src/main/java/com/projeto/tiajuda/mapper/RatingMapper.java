package com.projeto.tiajuda.mapper;

import com.projeto.tiajuda.dto.response.RatingResponse;
import com.projeto.tiajuda.entity.Rating;
import org.springframework.stereotype.Component;

@Component
public class RatingMapper {

    public RatingResponse toResponse(Rating rating) {
        if (rating == null) {
            return null;
        }

        String serviceRequestTitle = (rating.getRequest() != null) ? rating.getRequest().getTitle() : null;
        Long serviceRequestId = (rating.getRequest() != null) ? rating.getRequest().getId() : null;

        String technicianName = (rating.getTechnician() != null) ? rating.getTechnician().getName() : null;
        Long technicianId = (rating.getTechnician() != null) ? rating.getTechnician().getId() : null;

        String clientName = (rating.getClient() != null) ? rating.getClient().getName() : null;
        Long clientId = (rating.getClient() != null) ? rating.getClient().getId() : null;

        return new RatingResponse(
                rating.getId(),
                rating.getScore(),
                rating.getComment(),
                serviceRequestId,
                serviceRequestTitle,
                technicianId,
                technicianName,
                clientId,
                clientName,
                rating.getCreatedAt()
        );
    }

}
