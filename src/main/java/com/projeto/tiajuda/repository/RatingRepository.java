package com.projeto.tiajuda.repository;

import com.projeto.tiajuda.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByServiceRequestIdAndClientId(Long serviceRequestId, Long clientId);

    List<Rating> findByTechnicianId(Long technicianId);

}
