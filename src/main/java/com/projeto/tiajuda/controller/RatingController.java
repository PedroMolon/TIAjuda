package com.projeto.tiajuda.controller;

import com.projeto.tiajuda.dto.request.RatingRequest;
import com.projeto.tiajuda.dto.response.RatingResponse;
import com.projeto.tiajuda.service.RatingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tiajuda/ratings")
public class RatingController {

    private final RatingService ratingService;

    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<RatingResponse> createRating(@RequestBody @Valid RatingRequest request) {
        RatingResponse createdRating = ratingService.createRating(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRating);
    }
}
