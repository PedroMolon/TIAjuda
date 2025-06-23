package com.projeto.tiajuda.repository;

import com.projeto.tiajuda.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProposalRepository extends JpaRepository<Proposal, Long> {

    List<Proposal> findByServiceRequestId(Long serviceRequestId);

    List<Proposal> findByTechnicianId(Long technicianId);

    Optional<Proposal> findByServiceRequestIdAndTechnicianId(Long serviceRequestId, Long technicianId);

}
