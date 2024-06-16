package com.github.tradewebproject.repository.Purchase;

import com.github.tradewebproject.domain.Product;
import com.github.tradewebproject.domain.Purchase;
import com.github.tradewebproject.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    Page<Purchase> findByUserUserId(Long userId, Pageable pageable);

    Optional<Purchase> findByUserAndProduct(User user, Product product);

    int countBySellerId(Long sellerId);
}