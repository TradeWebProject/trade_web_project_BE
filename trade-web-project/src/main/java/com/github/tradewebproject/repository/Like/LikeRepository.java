package com.github.tradewebproject.repository.Like;

import com.github.tradewebproject.domain.Like;
import com.github.tradewebproject.domain.Product;
import com.github.tradewebproject.domain.Purchase;
import com.github.tradewebproject.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Page<Like> findByUserUserId(Long userId, Pageable pageable);
    List<Like> findByProductName(String productName);

    boolean existsByUserAndProduct_ProductId(User user, Long productId);


}