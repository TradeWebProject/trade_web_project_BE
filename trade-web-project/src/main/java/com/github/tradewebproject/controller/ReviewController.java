package com.github.tradewebproject.controller;


import com.github.tradewebproject.Dto.Review.ReviewResponseDto;
import com.github.tradewebproject.service.Review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping("/reviews")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "리뷰 작성", description = "사용자가 리뷰를 작성합니다.")
    public ResponseEntity<ReviewResponseDto> createReview(Principal principal,
                                             @RequestParam("productId") Long productId,
                                             @RequestParam("reviewContent") String reviewContent,
                                             @RequestParam("rating") Double rating,
                                             @RequestParam("title")String reviewTitle) {
        String email = principal.getName();
        ReviewResponseDto responseDTO = reviewService.createReview(email, productId, reviewContent, rating, reviewTitle);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/reviews/{userId}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "사용자 리뷰 조회", description = "특정 사용자의 리뷰를 조회합니다.")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByUserId(@PathVariable Long userId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByUserId(userId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/reviews/product/{productId}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "제품 리뷰 조회", description = "특정 제품의 리뷰를 조회합니다.")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByProductId(@PathVariable Long productId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }
}