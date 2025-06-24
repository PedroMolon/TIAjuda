package com.projeto.tiajuda.service;

import com.projeto.tiajuda.configuration.security.JWTUserData;
import com.projeto.tiajuda.dto.request.ServiceRequestCreate;
import com.projeto.tiajuda.dto.response.ServiceRequestResponse;
import com.projeto.tiajuda.entity.ServiceRequest;
import com.projeto.tiajuda.entity.User;
import com.projeto.tiajuda.exceptions.CustomAuthenticationException;
import com.projeto.tiajuda.mapper.ServiceRequestMapper;
import com.projeto.tiajuda.repository.ServiceRequestRepository;
import com.projeto.tiajuda.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceRequestService {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(ServiceRequestService.class);

    private final ServiceRequestRepository serviceRequestRepository;
    private final ServiceRequestMapper serviceRequestMapper;
    private final UserRepository userRepository;

    public ServiceRequestService(ServiceRequestRepository serviceRequestRepository, ServiceRequestMapper serviceRequestMapper, UserRepository userRepository) {
        this.serviceRequestRepository = serviceRequestRepository;
        this.serviceRequestMapper = serviceRequestMapper;
        this.userRepository = userRepository;
    }

    @Transactional
    public ServiceRequestResponse createServiceRequest(ServiceRequestCreate serviceRequestCreate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String && "anonymousUser".equals(authentication.getPrincipal())) {
            throw new CustomAuthenticationException("Nenhum usuário autenticado encontrado no contexto de segurança");
        }

        String authenticatedUserEmail;

        Object principal = authentication.getPrincipal();
        if (principal instanceof JWTUserData jwtUserData) {
            authenticatedUserEmail = jwtUserData.email();
            logger.info("ServiceRequestService: Email extraído do JWTUserData: " + authenticatedUserEmail);
        } else if (principal instanceof User userDetails) {
            authenticatedUserEmail = userDetails.getUsername();
            logger.info("ServiceRequestService: Email extraído do UserDetails: " + authenticatedUserEmail);
        } else {
            authenticatedUserEmail = authentication.getName();
            logger.warn("ServiceRequestService: Principal inesperado no SecurityContext. Usando authentication.getName(): " + authenticatedUserEmail);
            if (authenticatedUserEmail.contains("JWTUserData")) {
                throw new CustomAuthenticationException("Erro ao extrair email do token JWT. Formato inesperado do principal.");
            }
        }

        User client = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Usuário autenticado não encontrado"));

        ServiceRequest serviceRequest = serviceRequestMapper.toEntity(serviceRequestCreate);

        serviceRequest.setClient(client);

        ServiceRequest savedServiceRequest = serviceRequestRepository.save(serviceRequest);

        return ServiceRequestMapper.toResponse(savedServiceRequest);
    }

    @Transactional(readOnly = true)
    public List<ServiceRequestResponse> getAllServiceRequests() {
        List<ServiceRequest> serviceRequests = serviceRequestRepository.findAll();
        return serviceRequests.stream()
                .map(ServiceRequestMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ServiceRequestResponse getServiceRequestById(Long id) {
        ServiceRequest serviceRequest = serviceRequestRepository.findById(id)
                .orElseThrow(() -> new CustomAuthenticationException("Serviço não encontrado com o Id"));
        return ServiceRequestMapper.toResponse(serviceRequest);
    }

}
