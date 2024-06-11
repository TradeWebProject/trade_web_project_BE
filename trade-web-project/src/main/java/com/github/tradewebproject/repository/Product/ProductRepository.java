package com.github.tradewebproject.repository.Product;

import com.github.tradewebproject.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByStockGreaterThanAndProductStatus(int stock, int productStatus, Pageable pageable);
    Optional<Product> findById(Long productId);
    List<Product> findByUserUserId(Long userId);
    List<Product> findByEndDateAfterAndStartDateBeforeOrStartDate(Date endDate, Date startDate1, Date startDate2);


    Page<Product> findByUserUserIdAndStockGreaterThanAndProductStatus(Long userId, int i, int i1, Pageable pageable);
}
