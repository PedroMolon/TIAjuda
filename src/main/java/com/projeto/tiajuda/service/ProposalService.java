package com.projeto.tiajuda.service;

import com.projeto.tiajuda.configuration.security.JWTUserData;
import com.projeto.tiajuda.dto.request.ProposalCreate;
import com.projeto.tiajuda.dto.request.ProposalUpdate;
import com.projeto.tiajuda.dto.response.ProposalResponse;
import com.projeto.tiajuda.entity.Proposal;
import com.projeto.tiajuda.entity.ServiceRequest;
import com.projeto.tiajuda.entity.User;
import com.projeto.tiajuda.entity.enums.ProposalStatus;
import com.projeto.tiajuda.entity.enums.Role;
import com.projeto.tiajuda.entity.enums.ServiceStatus;
import com.projeto.tiajuda.exceptions.CustomAuthenticationException;
import com.projeto.tiajuda.mapper.ProposalMapper;
import com.projeto.tiajuda.repository.ProposalRepository;
import com.projeto.tiajuda.repository.ServiceRequestRepository;
import com.projeto.tiajuda.repository.UserRepository;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProposalService {

    private static final Logger logger = LoggerFactory.getLogger(ProposalService.class);

    private final ProposalRepository proposalRepository;
    private final ProposalMapper proposalMapper;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;

    public ProposalService(ProposalRepository proposalRepository, ProposalMapper proposalMapper, ServiceRequestRepository serviceRequestRepository, UserRepository userRepository) {
        this.proposalRepository = proposalRepository;
        this.proposalMapper = proposalMapper;
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ProposalResponse createProposal(ProposalCreate proposalCreate) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomAuthenticationException("Nenhum usuário autenticado encontrado.");
        }

        String authenticatedUserEmail;
        Object principal = authentication.getPrincipal();
        if (principal instanceof JWTUserData jwtUserData) {
            authenticatedUserEmail = jwtUserData.email();
        } else {
            throw new CustomAuthenticationException("Tipo de principal inesperado no contexto de segurança.");
        }

        User technician = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Técnico autenticado não encontrado no banco de dados."));


        if (!technician.getRoles().contains(Role.TECHNICIAN)) {
            throw new ValidationException("Apenas técnicos podem enviar propostas.");
        }

        ServiceRequest serviceRequest = serviceRequestRepository.findById(proposalCreate.serviceRequestId())
                .orElseThrow(() -> new CustomAuthenticationException("Serviço não encontrado com o ID"));

        if (serviceRequest.getStatus() != ServiceStatus.OPEN) {
            throw new ValidationException("Não é possível enviar proposta para um serviço com status: " + serviceRequest.getStatus());
        }

        if (serviceRequest.getClient().getId().equals(technician.getId())) {
            throw new ValidationException("Técnico não pode enviar proposta para seu próprio serviço.");
        }

        if (proposalRepository.findByServiceRequestIdAndTechnicianId(serviceRequest.getId(), technician.getId()).isPresent()) {
            throw new ValidationException("Você já enviou uma proposta para este serviço.");
        }

        Proposal proposal = proposalMapper.toEntity(proposalCreate, serviceRequest, technician);
        Proposal savedProposal = proposalRepository.save(proposal);

        return proposalMapper.toResponse(savedProposal);
    }

    @Transactional(readOnly = true)
    public List<ProposalResponse> getAllProposals() {
        List<Proposal> proposalList = proposalRepository.findAll();
        return proposalList.stream()
                .map(proposalMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProposalResponse> getProposalsByServiceId(Long serviceId) {
        ServiceRequest serviceRequest = serviceRequestRepository.findById(serviceId)
                .orElseThrow(() -> new CustomAuthenticationException("Serviço não encontrado com o ID: " + serviceId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail;
        if (authentication != null && authentication.getPrincipal() instanceof JWTUserData jwtUserData) {
            authenticatedUserEmail = jwtUserData.email();
        } else {
            throw new CustomAuthenticationException("Nenhum usuário autenticado encontrado para verificar permissões.");
        }

        User authenticatedUser = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Usuário autenticado não encontrado no banco de dados."));

        boolean isServiceOwner = serviceRequest.getClient().getId().equals(authenticatedUser.getId());
        boolean hasMadeProposal = proposalRepository.findByServiceRequestIdAndTechnicianId(serviceId, authenticatedUser.getId()).isPresent();

        if (!isServiceOwner && !hasMadeProposal && !authenticatedUser.getRoles().contains(Role.ADMIN)) {
            throw new ValidationException("Você não tem permissão para visualizar as propostas deste serviço.");
        }

        List<Proposal> proposals = proposalRepository.findByServiceRequestId(serviceId);
        return proposals.stream()
                .map(proposalMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProposalResponse> getMyProposals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail;
        if (authentication != null && authentication.getPrincipal() instanceof JWTUserData jwtUserData) {
            authenticatedUserEmail = jwtUserData.email();
        } else {
            throw new CustomAuthenticationException("Nenhum usuário autenticado encontrado.");
        }

        User technician = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Técnico autenticado não encontrado."));

        if (!technician.getRoles().contains(Role.TECHNICIAN)) {
            throw new ValidationException("Apenas técnicos podem visualizar suas próprias propostas.");
        }

        List<Proposal> proposals = proposalRepository.findByTechnicianId(technician.getId());
        return proposals.stream()
                .map(proposalMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProposalResponse getProposalById(Long id) {
        Proposal proposal = proposalRepository.findById(id)
                .orElseThrow(() -> new CustomAuthenticationException("Proposta não encontrada com o ID: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail;
        if (authentication != null && authentication.getPrincipal() instanceof JWTUserData jwtUserData) {
            authenticatedUserEmail = jwtUserData.email();
        } else {
            throw new CustomAuthenticationException("Nenhum usuário autenticado encontrado para verificar permissões.");
        }

        User authenticatedUser = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Usuário autenticado não encontrado no banco de dados."));

        boolean isProposalOwner = proposal.getTechnician().getId().equals(authenticatedUser.getId());
        boolean isServiceOwner = proposal.getServiceRequest().getClient().getId().equals(authenticatedUser.getId());

        if (!isProposalOwner && !isServiceOwner && !authenticatedUser.getRoles().contains(Role.ADMIN)) {
            throw new ValidationException("Você não tem permissão para visualizar esta proposta.");
        }

        return proposalMapper.toResponse(proposal);
    }

    @Transactional
    public ProposalResponse updateProposal(Long id, ProposalUpdate proposalUpdate) {
        Proposal existingProposal = proposalRepository.findById(id)
                .orElseThrow(() -> new CustomAuthenticationException("Proposta não encontrada com o ID: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail;
        if (authentication != null && authentication.getPrincipal() instanceof JWTUserData jwtUserData) {
            authenticatedUserEmail = jwtUserData.email();
        } else {
            throw new CustomAuthenticationException("Nenhum usuário autenticado encontrado para atualizar proposta.");
        }

        User authenticatedUser = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Usuário autenticado não encontrado."));

        if (!existingProposal.getTechnician().getId().equals(authenticatedUser.getId())) {
            throw new ValidationException("Você não tem permissão para atualizar esta proposta.");
        }

        if (existingProposal.getStatus() != ProposalStatus.PENDING) {
            throw new ValidationException("Não é possível atualizar uma proposta com status: " + existingProposal.getStatus());
        }

        existingProposal.setPrice(proposalUpdate.price());
        existingProposal.setDescription(proposalUpdate.description());

        Proposal updatedProposal = proposalRepository.save(existingProposal);
        logger.info("Proposta ID {} atualizada com sucesso pelo técnico ID {}", id, authenticatedUser.getId());
        return proposalMapper.toResponse(updatedProposal);
    }

    @Transactional
    public void deleteProposal(Long id) {
        Proposal existingProposal = proposalRepository.findById(id)
                .orElseThrow(() -> new CustomAuthenticationException("Proposta não encontrada com o ID: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail;
        if (authentication != null && authentication.getPrincipal() instanceof JWTUserData jwtUserData) {
            authenticatedUserEmail = jwtUserData.email();
        } else {
            throw new CustomAuthenticationException("Nenhum usuário autenticado encontrado para deletar proposta.");
        }

        User authenticatedUser = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Usuário autenticado não encontrado."));

        boolean isProposalOwner = existingProposal.getTechnician().getId().equals(authenticatedUser.getId());
        boolean isAdmin = authenticatedUser.getRoles().contains(Role.ADMIN);

        if (!isProposalOwner && !isAdmin) {
            throw new ValidationException("Você não tem permissão para deletar esta proposta.");
        }

        if (existingProposal.getStatus() != ProposalStatus.PENDING) {
            if (!isAdmin) {
                throw new ValidationException("Não é possível deletar uma proposta com status: " + existingProposal.getStatus());
            }
        }

        proposalRepository.delete(existingProposal);
        logger.info("Proposta ID {} deletada com sucesso pelo usuário ID {}", id, authenticatedUser.getId());
    }

    @Transactional
    public ProposalResponse acceptProposal(Long proposalId) {
        Proposal proposalToAccept = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new CustomAuthenticationException("Proposta não encontrada com o ID: " + proposalId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail;
        if (authentication != null && authentication.getPrincipal() instanceof JWTUserData jwtUserData) {
            authenticatedUserEmail = jwtUserData.email();
        } else {
            throw new CustomAuthenticationException("Nenhum usuário autenticado encontrado para aceitar proposta.");
        }

        User authenticatedClient = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Usuário autenticado não encontrado."));

        if (!proposalToAccept.getServiceRequest().getClient().getId().equals(authenticatedClient.getId())) {
            throw new ValidationException("Apenas o cliente dono do serviço pode aceitar esta proposta.");
        }

        if (proposalToAccept.getStatus() != ProposalStatus.PENDING) {
            throw new ValidationException("Não é possível aceitar uma proposta com status: " + proposalToAccept.getStatus());
        }

        proposalToAccept.setStatus(ProposalStatus.ACCEPTED);
        Proposal acceptedProposal = proposalRepository.save(proposalToAccept);
        logger.info("Proposta ID {} aceita para o serviço ID {} pelo cliente ID {}",
                proposalId, acceptedProposal.getServiceRequest().getId(), authenticatedClient.getId());

        ServiceRequest serviceRequest = acceptedProposal.getServiceRequest();
        serviceRequest.setStatus(ServiceStatus.IN_NEGOTIATION);
        serviceRequestRepository.save(serviceRequest);
        logger.info("Status do serviço ID {} atualizado para IN_NEGOTIATION.", serviceRequest.getId());

        List<Proposal> otherProposals = proposalRepository.findByServiceRequestId(serviceRequest.getId());
        for (Proposal p : otherProposals) {
            if (!p.getId().equals(proposalToAccept.getId()) && p.getStatus() == ProposalStatus.PENDING) {
                p.setStatus(ProposalStatus.REJECTED);
                proposalRepository.save(p);
                logger.info("Proposta ID {} rejeitada automaticamente para o serviço ID {}.", p.getId(), serviceRequest.getId());
            }
        }

        return proposalMapper.toResponse(acceptedProposal);
    }

    @Transactional
    public ProposalResponse rejectProposal(Long proposalId) {
        Proposal proposalToReject = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new CustomAuthenticationException("Proposta não encontrada com o ID: " + proposalId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail;
        if (authentication != null && authentication.getPrincipal() instanceof JWTUserData jwtUserData) {
            authenticatedUserEmail = jwtUserData.email();
        } else {
            throw new CustomAuthenticationException("Nenhum usuário autenticado encontrado para rejeitar proposta.");
        }

        User authenticatedClient = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Usuário autenticado não encontrado."));

        if (!proposalToReject.getServiceRequest().getClient().getId().equals(authenticatedClient.getId())) {
            throw new ValidationException("Apenas o cliente dono do serviço pode rejeitar esta proposta.");
        }

        if (proposalToReject.getStatus() != ProposalStatus.PENDING) {
            throw new ValidationException("Não é possível rejeitar uma proposta com status: " + proposalToReject.getStatus());
        }

        proposalToReject.setStatus(ProposalStatus.REJECTED);
        Proposal rejectedProposal = proposalRepository.save(proposalToReject);
        logger.info("Proposta ID {} rejeitada manualmente para o serviço ID {} pelo cliente ID {}",
                proposalId, rejectedProposal.getServiceRequest().getId(), authenticatedClient.getId());

        return proposalMapper.toResponse(rejectedProposal);
    }
}
