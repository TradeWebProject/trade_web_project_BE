package com.github.tradewebproject.Dto.Review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerReviewResponseDto {
    private String productName;
    private Date reviewDate;
    private String reviewerNickname;
    private String reviewerProfileImageUrl;
    private Double rating;
    private String reviewContent;
    private String reviewTitle;
}