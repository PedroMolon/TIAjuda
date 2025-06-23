package com.projeto.tiajuda.dto.request;

import jakarta.validation.constraints.*;

public class RatingRequest {

    @NotNull(message = "Service Request ID é obrigatório")
    private Long serviceRequestId;

    @NotNull(message = "Pontuação é obrigatória")
    @Min(value = 1, message = "A pontuação mínima é 1")
    @Max(value = 5, message = "A pontuação máxima é 5")
    private Integer score;

    @Size(max = 500, message = "O comentário não pode exceder 500 caracteres")
    private String comment;

    public RatingRequest() {
    }

    public RatingRequest(Long serviceRequestId, Integer score, String comment) {
        this.serviceRequestId = serviceRequestId;
        this.score = score;
        this.comment = comment;
    }

    public Long getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(Long serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
