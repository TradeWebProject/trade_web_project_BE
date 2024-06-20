package com.github.tradewebproject.Dto.Product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.tradewebproject.Dto.Review.ReviewResponseDto;
import com.github.tradewebproject.Dto.Review.SellerReviewResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// 전체 product 조회용 dto

public class DetailProductDto {

    private Long productId;
    private String productName;
    private String description;
    private Integer price;
    private String category;
    private String productQuality;
    private String userNickName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date startDate;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date endDate;
    private Integer productStatus;
    private String thumbnailUrl;
    private List<String> imagePathUrl =  new ArrayList<>();
    private List<String> imagePaths = new ArrayList<>();
    private List<SellerReviewResponseDto> reviews;
    private int totalLikes;
    private int totalRatings;
    private int totalSales;
    private double averageRating;
    private Long sellerId;

    public DetailProductDto(Long productId, String productName, String description, Integer price, String category, String userNickName,Long sellerId, Integer productStatus,String productQuality, Date startDate, Date endDate ,String thumbNail, List<String> imagePaths) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.userNickName = userNickName;
        this.sellerId = sellerId;
        this.productStatus = productStatus;
        this.productQuality = productQuality;
        this.thumbnailUrl = thumbNail;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imagePaths = imagePaths;


    }

}