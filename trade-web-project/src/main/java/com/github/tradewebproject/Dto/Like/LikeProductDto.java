package com.github.tradewebproject.Dto.Like;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LikeProductDto {
    private Long likeProductId;
    private Long userId;
    private Long productId;
    private String productName;
    private Integer price;
    private String imageUrl;
    private String sellerNickname;
    private Integer productStatus;

}
