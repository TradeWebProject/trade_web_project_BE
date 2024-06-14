package com.github.tradewebproject.service.Review;

import com.github.tradewebproject.Dto.Review.ReviewCreationResponseDto;
import com.github.tradewebproject.Dto.Review.ReviewResponseDto;
import com.github.tradewebproject.domain.Product;
import com.github.tradewebproject.domain.Purchase;
import com.github.tradewebproject.domain.Review;
import com.github.tradewebproject.domain.User;
import com.github.tradewebproject.repository.Product.ProductRepository;
import com.github.tradewebproject.repository.Purchase.PurchaseRepository;
import com.github.tradewebproject.repository.Review.ReviewRepository;
import com.github.tradewebproject.repository.User.UserJpaRepository;
import com.github.tradewebproject.repository.User.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @Transactional
    public ReviewResponseDto createReview(String email, Long productId, String reviewContent, Double rating , String reviewTitle) {
        User user = userRepository.findByEmail2(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Purchase purchase = purchaseRepository.findByUserAndProduct(user, product)
                .orElseThrow(() -> new RuntimeException("해당 상품에 대한 구매 내역이 없습니다."));

        Review review = new Review();
        review.setPurchase(purchase);
        review.setProduct(product);
        review.setReviewerNickname(user.getUserNickname());
        review.setRating(rating);
        review.setReviewContent(reviewContent);
        review.setReviewDate(new Date());
        review.setReviewTitle(reviewTitle);
        review.setUser(user);
        review = reviewRepository.save(review);

        return new ReviewResponseDto(reviewTitle, reviewContent, productId, user.getUserId(), rating, review.getReviewId());
    }

    @Transactional
    public List<ReviewResponseDto> getReviewsByUserId(Long userId) {
        User user = userJpaRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Review> reviews = reviewRepository.findByUser(user);
        return reviews.stream()
                .map(review -> new ReviewResponseDto(
                        review.getReviewTitle(),
                        review.getReviewContent(),
                        review.getProduct().getProductId(),
                        review.getUser().getUserId(),
                        review.getRating(),
                        review.getReviewId()

                ))
                .collect(Collectors.toList());
    }


}