package com.projeto.tiajuda.service;

import com.projeto.tiajuda.configuration.security.JWTUserData;
import com.projeto.tiajuda.dto.request.PaymentRequest;
import com.projeto.tiajuda.dto.response.PaymentResponse;
import com.projeto.tiajuda.entity.Payment;
import com.projeto.tiajuda.entity.ServiceRequest;
import com.projeto.tiajuda.entity.User;
import com.projeto.tiajuda.entity.enums.PaymentStatus;
import com.projeto.tiajuda.entity.enums.Role;
import com.projeto.tiajuda.entity.enums.ServiceStatus;
import com.projeto.tiajuda.exceptions.CustomAuthenticationException;
import com.projeto.tiajuda.mapper.PaymentMapper;
import com.projeto.tiajuda.repository.PaymentRepository;
import com.projeto.tiajuda.repository.ServiceRequestRepository;
import com.projeto.tiajuda.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository, ServiceRequestRepository serviceRequestRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.serviceRequestRepository = serviceRequestRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail;

        Object principal = authentication.getPrincipal();
        if (principal instanceof JWTUserData jwtUserData) {
            authenticatedUserEmail = jwtUserData.email();
        } else if (principal instanceof User userDetails) {
            authenticatedUserEmail = userDetails.getUsername();
        } else {
            throw new CustomAuthenticationException("Usuário não autenticado");
        }

        User client = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Usuário não encontrado"));

        if (!client.getRoles().contains(Role.CLIENT)) {
            throw new CustomAuthenticationException("Acesso negado: usuário não é um cliente");
        }

        ServiceRequest serviceRequest = serviceRequestRepository.findById(request.serviceRequestId())
                .orElseThrow(() -> new CustomAuthenticationException("Solicitação de serviço não encontrada"));

        if (!serviceRequest.getClient().getId().equals(client.getId())) {
            throw new CustomAuthenticationException("Usuário não tem permissão para para esta solicitação de serviço");
        }

        if (serviceRequest.getStatus() != ServiceStatus.COMPLETED) {
            throw new CustomAuthenticationException("A solicitação de serviço não está concluída");
        }

        if (serviceRequest.isPaid()) {
            throw new CustomAuthenticationException("A solicitação de serviço já foi paga");
        }

        BigDecimal amountToPay = serviceRequest.getPayment().getAmount();

        Payment payment = new Payment(serviceRequest, amountToPay);
        payment.setPaymentMethod(request.paymentMethod());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId("SIMULATED_TXN_" + System.currentTimeMillis());

        Payment savedPayment = paymentRepository.save(payment);

        serviceRequest.setPaid(true);
        serviceRequest.setStatus(ServiceStatus.PAID);
        serviceRequest.setPayment(savedPayment);
        serviceRequestRepository.save(serviceRequest);

        return PaymentMapper.toResponse(savedPayment);

    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByServiceRequestId(Long requestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUserEmail;
        Object principal = authentication.getPrincipal();

        if (principal instanceof JWTUserData jwtUserData) {
            authenticatedUserEmail = jwtUserData.email();
        } else if (principal instanceof User userDetails) {
            authenticatedUserEmail = userDetails.getUsername();
        } else {
            throw new CustomAuthenticationException("Usuário não encontrado.");
        }

        User currentUser = userRepository.findByEmail(authenticatedUserEmail)
                .orElseThrow(() -> new CustomAuthenticationException("Usuário autenticado não encontrado."));

        ServiceRequest serviceRequest = serviceRequestRepository.findById(requestId)
                .orElseThrow(() -> new CustomAuthenticationException("Solicitação de serviço não encontrada com o ID: " + requestId));

        boolean isServiceOwner = serviceRequest.getClient().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().contains(Role.ADMIN);

        if (!isServiceOwner && !isAdmin) {
            throw new CustomAuthenticationException("Você não tem permissão para acessar esta solicitação de serviço.");
        }

        Payment payment = paymentRepository.findByRequestId(requestId)
                .orElseThrow(() -> new CustomAuthenticationException("Pagamento não encontrado para a solicitação de serviço com ID: " + requestId));

        return PaymentMapper.toResponse(payment);
    }

}
