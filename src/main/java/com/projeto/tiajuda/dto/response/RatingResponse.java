package com.projeto.tiajuda.dto.response;

import java.time.LocalDateTime;

public class RatingResponse {

    private Long id;
    private Integer score;
    private String comment;
    private Long serviceRequestId;
    private String serviceRequestTitle;
    private Long technicianId;
    private String technicianName;
    private Long clientId;
    private String clientName;
    private LocalDateTime createdAt;

    public RatingResponse() {
    }

    public RatingResponse(Long id, Integer score, String comment, Long serviceRequestId, String serviceRequestTitle, Long technicianId, String technicianName, Long clientId, String clientName, LocalDateTime createdAt) {
        this.id = id;
        this.score = score;
        this.comment = comment;
        this.serviceRequestId = serviceRequestId;
        this.serviceRequestTitle = serviceRequestTitle;
        this.technicianId = technicianId;
        this.technicianName = technicianName;
        this.clientId = clientId;
        this.clientName = clientName;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Integer getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public Long getServiceRequestId() {
        return serviceRequestId;
    }

    public String getServiceRequestTitle() {
        return serviceRequestTitle;
    }

    public Long getTechnicianId() {
        return technicianId;
    }

    public String getTechnicianName() {
        return technicianName;
    }

    public Long getClientId() {
        return clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
