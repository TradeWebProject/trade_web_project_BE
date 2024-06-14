package com.github.tradewebproject.Dto.Product;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.github.tradewebproject.domain.Product;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    // 상품 등록을 위한 DTO
    //userId는 토큰 입력하면 자동으로 받아와줌
    private Long productId;


    @NotEmpty(message = "상품 이름은 필수입니다.")
    private String productName;

    @NotNull(message = "가격은 필수입니다.")
    private int price;

//    @NotNull(message = "재고 입력은 필수입니다.")
//    private int stock;

//    @NotNull(message = "상품 옵션은 필수입니다.")
//    private String productOption;

    @NotNull(message = "유저 닉네임은 필수입니다.")
    private String userNickName;

    @NotNull(message = "카테고리 입력은 필수입니다.")
    private String category;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    @NotNull(message="상품 상태 입력은 필수입니다.")
    private String productQuality;

    @Column(name = "product_status", nullable = true)
    private Integer productStatus;

    private String description;
    private String imageUrl;
    private List<MultipartFile> files = new ArrayList<>();
    private List<String> imagePaths = new ArrayList<>(); // 이미지 경로 추가


    public ProductDTO(Product product) {
        this.productId = product.getProductId();
        this.productName = product.getProductName();
        this.price = product.getPrice();
        this.startDate = product.getStartDate();
        this.endDate = product.getEndDate();
        this.description = product.getDescription();
        this.productQuality = product.getProductQuality();
        this.category = product.getCategory();
        this.userNickName = product.getUser().getUserNickname();
        this.productStatus = product.getProductStatus();
        this.imageUrl = product.getImageUrl();
        this.imagePaths = product.getImagePaths();
    }

}