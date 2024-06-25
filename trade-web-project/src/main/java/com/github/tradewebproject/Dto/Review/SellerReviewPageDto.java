package com.github.tradewebproject.Dto.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerReviewPageDto {
    private List<SellerReviewResponseDto> reviews;
    private int totalSales;
    private int totalReviews;
    private String sellerNickname;
    private String sellerProfileImageUrl;
    private Double averageRating;
}