package com.github.tradewebproject.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "purchase")
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_id")
    private Long purchaseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "purchase_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date purchaseDate;

    @Column(nullable = false)
    private Integer price;

    @Column(name = "product_name", nullable = false, length = 255)
    private String productName;

    @Column(name = "image_url", length = 255, nullable = true)
    private String imageUrl;

    @Column(name = "seller_nickname", length = 255, nullable = false)
    private String sellerNickname;
}