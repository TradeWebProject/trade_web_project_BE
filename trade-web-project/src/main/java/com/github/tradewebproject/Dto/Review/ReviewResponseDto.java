package com.github.tradewebproject.Dto.Review;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private String reviewTitle;
    private String reviewContent;
    private Long productId;
    private Long userId;
    private Double rating;
    private Long reviewId;



}