package com.projeto.tiajuda.controller;

import com.projeto.tiajuda.dto.request.PaymentRequest;
import com.projeto.tiajuda.dto.response.PaymentResponse;
import com.projeto.tiajuda.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tiajuda/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @PreAuthorize("harRole('CLIENT')")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/service-request/{serviceRequestId}")
    @PreAuthorize("hasRole('CLIENT', 'TECHNICIAN', 'ADMIN')")
    public ResponseEntity<PaymentResponse> getPaymentByServiceRequestId(@PathVariable Long id) {
        PaymentResponse response = paymentService.getPaymentByServiceRequestId(id);
        return ResponseEntity.ok(response);
    }

}
