package com.github.tradewebproject.Dto.Like;

import com.github.tradewebproject.Dto.Product.ProductResponseDto;
import com.github.tradewebproject.Dto.Purchase.PurchaseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LikePageDto {
    private List<LikeProductDto> products;
    private int totalPages;
}
