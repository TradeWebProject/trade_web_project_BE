package com.github.tradewebproject.Dto.Purchase;


import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class PurchaseDto {
    private Long purchaseId;
    private Long productId;
    private Date purchaseDate;
    private String productName;
    private String imageUrl;
    private Integer price;
    private String sellerNickname;
    private Long sellerId;
}
