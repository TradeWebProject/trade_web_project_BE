package com.github.tradewebproject.domain;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
@NoArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(columnDefinition = "LONGTEXT", nullable = true)
    private String description;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "category", length = 255, nullable = true)
    private String category;

    @Column(name = "product_quality", length = 255, nullable = true)
    private String productQuality;

    @Column(name = "image_url", length = 255, nullable = true)
    private String imageUrl;

    @Column(name = "product_status", nullable = true)
    private Integer productStatus;

    @Column(name = "end_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "start_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(nullable = false)
    private Integer stock;

    @Column(name = "product_option", nullable = false, length = 255)
    private String productOption;

    @Column(name = "payment_date", nullable = true)
    @Temporal(TemporalType.DATE)
    private Date paymentDate;

    @ElementCollection
    @CollectionTable(name = "ProductImage", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_path")
    private List<String> imagePaths = new ArrayList<>();

    public boolean canAddImage() {
        return imagePaths.size() < 10;
    }

    public void addImage(String imagePath) {
        if (canAddImage()) {
            imagePaths.add(imagePath);
        } else {
            throw new IllegalStateException("사진은 10개까지 등록 가능합니다.");
        }
    }

    public void clearImages() {
        this.imagePaths.clear();
    }
}