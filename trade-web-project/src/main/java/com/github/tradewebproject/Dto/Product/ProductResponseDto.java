package com.github.tradewebproject.Dto.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

// 전체 product 조회용 dto

public class ProductResponseDto {

    private Long productId;
    private String productName;
    private String description;
    private Integer price;
    private String imageUrl;
    private String userNickName;
    private String category;
    private Integer productStatus;
    private Date startDate;
    private Date endDate;
    private Date paymentDate;



}