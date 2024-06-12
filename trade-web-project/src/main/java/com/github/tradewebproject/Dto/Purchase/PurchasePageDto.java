package com.github.tradewebproject.Dto.Purchase;

import com.github.tradewebproject.Dto.Product.ProductResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PurchasePageDto {
    private List<PurchaseDto> products;
    private int totalPages;
}
