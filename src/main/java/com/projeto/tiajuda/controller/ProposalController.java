package com.projeto.tiajuda.controller;

import com.projeto.tiajuda.dto.request.ProposalCreate;
import com.projeto.tiajuda.dto.request.ProposalUpdate;
import com.projeto.tiajuda.dto.response.ProposalResponse;
import com.projeto.tiajuda.service.ProposalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tiajuda/proposals")
public class ProposalController {

    private final ProposalService proposalService;

    public ProposalController(ProposalService proposalService) {
        this.proposalService = proposalService;
    }

    @PostMapping
    public ResponseEntity<ProposalResponse> createProposal(@RequestBody @Valid ProposalCreate proposalCreate) {
        ProposalResponse proposalResponse = proposalService.createProposal(proposalCreate);
        return  ResponseEntity.status(HttpStatus.CREATED).body(proposalResponse);
    }

    @GetMapping
    public ResponseEntity<List<ProposalResponse>> getAllProposals() {
        List<ProposalResponse> proposalResponses = proposalService.getAllProposals();
        return ResponseEntity.ok(proposalResponses);
    }

    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<ProposalResponse>> getProposalsByServiceId(@PathVariable Long serviceId) {
        List<ProposalResponse> proposals = proposalService.getProposalsByServiceId(serviceId);
        return ResponseEntity.ok(proposals);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ProposalResponse>> getMyProposals() {
        List<ProposalResponse> proposals = proposalService.getMyProposals();
        return ResponseEntity.ok(proposals);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProposalResponse> getProposalById(@PathVariable Long id) {
        ProposalResponse proposal = proposalService.getProposalById(id);
        return ResponseEntity.ok(proposal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProposalResponse> updateProposal(@PathVariable Long id, @RequestBody @Valid ProposalUpdate proposalUpdate) {
        ProposalResponse response = proposalService.updateProposal(id, proposalUpdate);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProposal(@PathVariable Long id) {
        proposalService.deleteProposal(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<ProposalResponse> acceptProposal(@PathVariable Long id) {
        ProposalResponse response = proposalService.acceptProposal(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ProposalResponse> rejectProposal(@PathVariable Long id) {
        ProposalResponse response = proposalService.rejectProposal(id);
        return ResponseEntity.ok(response);
    }

}
