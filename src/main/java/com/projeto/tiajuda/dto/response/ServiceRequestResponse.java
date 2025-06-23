package com.projeto.tiajuda.dto.response;

import com.projeto.tiajuda.entity.ServiceRequest;
import com.projeto.tiajuda.entity.enums.ServiceCategory;
import com.projeto.tiajuda.entity.enums.ServiceStatus;

import java.time.Instant;

public class ServiceRequestResponse {

    private Long id;
    private String title;
    private String description;
    private ServiceCategory category;
    private String location;
    private ServiceStatus status;
    private Long clientId;
    private String clientName;
    private Instant createdAt;
    private Instant updatedAt;

    public ServiceRequestResponse(ServiceRequest serviceRequest) {
        this.id = serviceRequest.getId();
        this.title = serviceRequest.getTitle();
        this.description = serviceRequest.getDescription();
        this.category = serviceRequest.getCategory();
        this.location = serviceRequest.getLocation();
        this.status = serviceRequest.getStatus();
        this.createdAt = serviceRequest.getCreatedAt();
        this.updatedAt = serviceRequest.getUpdatedAt();

        if (serviceRequest.getClient() != null) {
            this.clientId = serviceRequest.getClient().getId();
            this.clientName = serviceRequest.getClient().getName();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ServiceCategory getCategory() {
        return category;
    }

    public void setCategory(ServiceCategory category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
