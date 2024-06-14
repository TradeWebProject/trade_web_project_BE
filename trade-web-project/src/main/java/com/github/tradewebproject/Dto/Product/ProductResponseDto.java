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


public class ProductResponseDto {

    private Long productId;
    private String productName;
    private String description;
    private Integer price;
    //private Integer stock;
    private String imageUrl;
    private String userNickName;
    //private String productOption;
    private String category;
    private Integer productStatus;
    private String productQuality;
    private Date startDate;
    private Date endDate;
    private Date paymentDate;

}