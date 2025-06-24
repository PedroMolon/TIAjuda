package com.projeto.tiajuda.controller;

import com.projeto.tiajuda.dto.request.ServiceRequestCreate;
import com.projeto.tiajuda.dto.response.ServiceRequestResponse;
import com.projeto.tiajuda.service.RatingService;
import com.projeto.tiajuda.service.ServiceRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tiajuda/service-request")
public class ServiceRequestController {

    private final ServiceRequestService serviceRequestService;
    private final RatingService ratingService;

    public ServiceRequestController(ServiceRequestService serviceRequestService, RatingService ratingService) {
        this.serviceRequestService = serviceRequestService;
        this.ratingService = ratingService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ServiceRequestResponse> createServiceRequest(@RequestBody @Valid ServiceRequestCreate serviceRequestCreate) {
        ServiceRequestResponse createdService = serviceRequestService.createServiceRequest(serviceRequestCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
    }

    @GetMapping
    public ResponseEntity<List<ServiceRequestResponse>> getAllServiceRequests() {
        List<ServiceRequestResponse> serviceRequestResponses = serviceRequestService.getAllServiceRequests();
        return ResponseEntity.ok(serviceRequestResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceRequestResponse> getServiceRequestById(@PathVariable Long id) {
        ServiceRequestResponse serviceRequestResponse = serviceRequestService.getServiceRequestById(id);
        return ResponseEntity.ok(serviceRequestResponse);
    }

    @PutMapping("/{id}/complete")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ServiceRequestResponse> completeServiceRequest(@PathVariable Long id) {
        ServiceRequestResponse completedService = ratingService.completeServiceRequest(id);
        return ResponseEntity.ok(completedService);
    }

}
