package com.projeto.tiajuda.dto.request;

import com.projeto.tiajuda.entity.enums.ServiceCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ServiceRequestCreate {

    @NotBlank(message = "O título do serviço é obrigatório")
    @Size(min = 5, max = 100, message = "O título deve ter entre 5 e 100 caracteres")
    private String title;

    @NotBlank(message = "A descrição do serviço é obrigatória")
    @Size(min = 20, max = 1000, message = "A descrição deve ter entre 20 e 1000 caracteres")
    private String description;

    @NotNull(message = "A categoria do serviço é obrigatória")
    private ServiceCategory category;

    @NotBlank(message = "A localização do serviço é obrigatória")
    @Size(min = 5, max = 255, message = "A localização deve ter entre 5 e 255 caracteres")
    private String location;

    public ServiceRequestCreate() {
    }

    public ServiceRequestCreate(String title, String description, ServiceCategory category, String location) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
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

}
