package com.github.tradewebproject.domain;


import jakarta.persistence.*;
import lombok.*;
import java.util.Date;


@Getter
@Setter
@NoArgsConstructor
@Entity
@AllArgsConstructor
@Table(name = "review")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id", nullable = false)
    private Purchase purchase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "reviewer_nickname", nullable = false, length = 255)
    private String reviewerNickname;

    @Column(name = "rating", nullable = false)
    private Double rating; // 0.0 ~ 5.0 사이

    @Column(name = "review_content", nullable = false, columnDefinition = "TEXT")
    private String reviewContent;

    @Column(name = "review_title", nullable = false, columnDefinition = "TEXT")
    private String reviewTitle;

    @Column(name = "review_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date reviewDate;

    @Column(name = "seller_id")
    private Long sellerId; // 새로운 필드 추가


}