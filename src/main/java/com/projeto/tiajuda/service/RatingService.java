package com.projeto.tiajuda.service;

import com.projeto.tiajuda.configuration.security.JWTUserData;
import com.projeto.tiajuda.dto.request.RatingRequest;
import com.projeto.tiajuda.dto.response.RatingResponse;
import com.projeto.tiajuda.dto.response.ServiceRequestResponse;
import com.projeto.tiajuda.entity.Proposal;
import com.projeto.tiajuda.entity.Rating;
import com.projeto.tiajuda.entity.ServiceRequest;
import com.projeto.tiajuda.entity.User;
import com.projeto.tiajuda.entity.enums.ProposalStatus;
import com.projeto.tiajuda.entity.enums.ServiceStatus;
import com.projeto.tiajuda.exceptions.CustomAuthenticationException;
import com.projeto.tiajuda.mapper.RatingMapper;
import com.projeto.tiajuda.mapper.ServiceRequestMapper;
import com.projeto.tiajuda.repository.RatingRepository;
import com.projeto.tiajuda.repository.ServiceRequestRepository;
import com.projeto.tiajuda.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;
    private final RatingMapper ratingMapper;
    private final ServiceRequestMapper serviceRequestMapper;

    public RatingService(RatingRepository ratingRepository, ServiceRequestRepository serviceRequestRepository, UserRepository userRepository, RatingMapper ratingMapper, ServiceRequestMapper serviceRequestMapper) {
        this.ratingRepository = ratingRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
        this.ratingMapper = ratingMapper;
        this.serviceRequestMapper = serviceRequestMapper;
    }

    @Transactional
    public ServiceRequestResponse completeServiceRequest(Long serviceRequestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail;
        if (authentication != null && authentication.getPrincipal() instanceof JWTUserData) {
            JWTUserData jwtUserData = (JWTUserData) authentication.getPrincipal();
            authenticatedUserEmail = jwtUserData.email();
        } else {
            throw new CustomAuthenticationException("Nenhum usuário autenticado encontrado");
        }

        User client = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Cliente autenticado não encontrado"));

        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceRequestId)
                .orElseThrow(() -> new CustomAuthenticationException("Solicitação de serviço não encontrada"));

        if (!serviceRequest.getClient().getId().equals(client.getId())) {
            throw new ValidationException("Apenas o cliente que criou a solicitação de serviço pode completá-la");
        }

        if (serviceRequest.getStatus() != ServiceStatus.IN_NEGOTIATION) {
            throw new ValidationException("Service Request não pode ser completada");
        }

        serviceRequest.setStatus(ServiceStatus.COMPLETED);
        ServiceRequest updatedServiceRequest = serviceRequestRepository.save(serviceRequest);

        return ServiceRequestMapper.toResponse(updatedServiceRequest);
    }

    @Transactional
    public RatingResponse createRating(RatingRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail;
        if (authentication != null && authentication.getPrincipal() instanceof JWTUserData) {
            JWTUserData jwtUserData = (JWTUserData) authentication.getPrincipal();
            authenticatedUserEmail = jwtUserData.email();
        } else {
            throw new CustomAuthenticationException("Nenhum usuário autenticado encontrado");
        }

        User client = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Cliente autenticado não encontrado"));

        ServiceRequest serviceRequest = serviceRequestRepository.findById(request.getServiceRequestId())
                .orElseThrow(() -> new CustomAuthenticationException("Solicitação de serviço não encontrada"));

        if (!serviceRequest.getClient().getId().equals(client.getId())) {
            throw new ValidationException("Apenas o cliente dono do Service Request pode criar uma avaliação para ele.");
        }

        if (serviceRequest.getStatus() != ServiceStatus.COMPLETED) {
            throw new ValidationException("Service Request não pode ser avaliado, pois não está COMPLETED. Status atual: " + serviceRequest.getStatus());
        }

        if (ratingRepository.findByServiceRequestIdAndClientId(serviceRequest.getId(), client.getId()).isPresent()) {
            throw new ValidationException("Este Service Request já foi avaliado por este cliente.");
        }

        User technician = serviceRequest.getProposals().stream()
                .filter(proposal -> proposal.getStatus() == ProposalStatus.ACCEPTED)
                .map(Proposal::getTechnician)
                .findFirst()
                .orElseThrow(() -> new ValidationException("Nenhum técnico encontrado para esta solicitação de serviço."));

        Rating rating = new Rating();
        rating.setScore(request.getScore());
        rating.setComment(request.getComment());
        rating.setRequest(serviceRequest);
        rating.setClient(client);
        rating.setTechnician(technician);

        Rating savedRating = ratingRepository.save(rating);

        return ratingMapper.toResponse(savedRating);
    }

}
