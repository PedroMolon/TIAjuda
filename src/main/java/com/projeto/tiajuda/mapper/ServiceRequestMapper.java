package com.projeto.tiajuda.mapper;

import com.projeto.tiajuda.dto.request.ServiceRequestCreate;
import com.projeto.tiajuda.dto.response.ServiceRequestResponse;
import com.projeto.tiajuda.entity.ServiceRequest;
import org.springframework.stereotype.Component;

@Component
public class ServiceRequestMapper {

    public ServiceRequest toEntity(ServiceRequestCreate requestCreate) {
        if (requestCreate == null) {
            return null;
        }

        ServiceRequest request = new ServiceRequest();
        request.setTitle(requestCreate.getTitle());
        request.setDescription(requestCreate.getDescription());
        request.setCategory(requestCreate.getCategory());
        request.setLocation(requestCreate.getLocation());

        return request;
    }

    public static ServiceRequestResponse toResponse(ServiceRequest request) {
        if (request == null) {
            return null;
        }
        return new ServiceRequestResponse(request);
    }

}
