package com.github.tradewebproject.Dto.Review;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SellerReviewResponseDto {
    private String productName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date reviewDate;

    private String reviewerNickname;
    private String reviewerProfileImageUrl;
    private Double rating;
    private String reviewContent;
    private String reviewTitle;
}