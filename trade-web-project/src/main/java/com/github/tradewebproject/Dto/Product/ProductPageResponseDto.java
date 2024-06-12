package com.github.tradewebproject.Dto.Product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProductPageResponseDto {
    private List<ProductResponseDto> products;
    private int totalPages;


}