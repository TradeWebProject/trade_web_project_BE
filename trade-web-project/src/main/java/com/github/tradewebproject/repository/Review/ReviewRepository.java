package com.github.tradewebproject.repository.Review;

import com.github.tradewebproject.domain.Product;
import com.github.tradewebproject.domain.Review;
import com.github.tradewebproject.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser(User user);
    List<Review> findByProduct(Product product);
}