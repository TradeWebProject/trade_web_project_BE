package com.github.tradewebproject.repository.Product;

import com.github.tradewebproject.domain.Product;
import com.github.tradewebproject.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findById(Long productId);
    List<Product> findByUserUserId(Long userId);


    Page<Product> findAll(Pageable pageable);
    Page<Product> findByUserUserIdAndProductStatus(Long userId, int productStatus, Pageable pageable);

    Page<Product> findAllByProductStatus(int productStatus, Pageable pageable);



    Page<Product> findAll(Specification<Product> spec, Pageable pageable);



    Page<Product> findByCategoryInAndProductStatus(List<String> userInterests, int i, Pageable pageable);



}