package com.projeto.tiajuda.mapper;

import com.projeto.tiajuda.dto.request.ProposalCreate;
import com.projeto.tiajuda.dto.response.ProposalResponse;
import com.projeto.tiajuda.entity.Proposal;
import com.projeto.tiajuda.entity.ServiceRequest;
import com.projeto.tiajuda.entity.User;
import com.projeto.tiajuda.entity.enums.ProposalStatus;
import org.springframework.stereotype.Component;

@Component
public class ProposalMapper {

    public Proposal toEntity(ProposalCreate proposalCreate, ServiceRequest request, User technician) {
        return new Proposal(
            proposalCreate.price(),
            proposalCreate.description(),
            request,
            technician
        );
    }

    public ProposalResponse toResponse(Proposal proposal) {
        return new ProposalResponse(
            proposal.getId(),
            proposal.getServiceRequest().getId(),
            proposal.getServiceRequest().getTitle(),
            proposal.getTechnician().getId(),
            proposal.getTechnician().getName(),
            proposal.getPrice(),
            proposal.getDescription(),
            proposal.getStatus(),
            proposal.getCreatedAt(),
            proposal.getUpdatedAt()
        );
    }

}
