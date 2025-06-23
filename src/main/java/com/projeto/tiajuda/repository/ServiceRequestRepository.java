package com.projeto.tiajuda.repository;

import com.projeto.tiajuda.entity.ServiceRequest;
import com.projeto.tiajuda.entity.User;
import com.projeto.tiajuda.entity.enums.ServiceCategory;
import com.projeto.tiajuda.entity.enums.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {

    List<ServiceRequest> findByClient(User client);

    List<ServiceRequest> findByStatus(ServiceStatus status);

    List<ServiceRequest> findByClientAndStatus(User client, ServiceStatus status);

    List<ServiceRequest> findByCategory(ServiceCategory category);

    List<ServiceRequest> findByLocationContainingIgnoreCase(String location);

}
