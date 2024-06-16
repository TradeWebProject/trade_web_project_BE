package com.github.tradewebproject.service.Purchase;

import com.github.tradewebproject.Dto.Product.ProductPageResponseDto;
import com.github.tradewebproject.Dto.Product.ProductResponseDto;
import com.github.tradewebproject.Dto.Purchase.PurchaseDto;
import com.github.tradewebproject.Dto.Purchase.PurchasePageDto;
import com.github.tradewebproject.domain.Like;
import com.github.tradewebproject.domain.Product;
import com.github.tradewebproject.domain.Purchase;
import com.github.tradewebproject.domain.User;
import com.github.tradewebproject.repository.Like.LikeRepository;
import com.github.tradewebproject.repository.Product.ProductRepository;
import com.github.tradewebproject.repository.Purchase.PurchaseRepository;
import com.github.tradewebproject.repository.User.UserJpaRepository;
import com.github.tradewebproject.repository.User.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseService {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Value("${UPLOAD_DIR}") // 환경변수로부터 업로드 디렉토리 경로를 가져옵니다.
    private String uploadDir;

    public PurchasePageDto getPurchaseByUserId(Long userId, int page, int size, String sort) {

        Sort sortBy = Sort.by("price");
        if (sort.equals("asc")) {
            sortBy = Sort.by(Sort.Direction.ASC, "price");
        } else if ("desc".equalsIgnoreCase(sort)) {
            sortBy = Sort.by(Sort.Direction.DESC, "price");
        }

        Pageable pageable = PageRequest.of(page - 1, size, sortBy);
        Page<Purchase> purchasePage = purchaseRepository.findByUserUserId(userId,pageable);

        List<PurchaseDto> purchaseDtoList = purchasePage.stream().map(product -> {
            PurchaseDto dto = new PurchaseDto();
            dto.setPurchaseId(product.getPurchaseId());
            dto.setProductId(product.getProduct().getProductId());
            dto.setPurchaseDate(product.getPurchaseDate());
            dto.setProductName(product.getProduct().getProductName());
            dto.setPrice(product.getPrice());
            dto.setSellerNickname(product.getSellerNickname());
            dto.setSellerId(product.getSellerId());


            // 이미지 파일 경로 설정
            String imageUrl = "/images/" + product.getImageUrl();
            dto.setImageUrl(imageUrl);

            return dto;
        }).collect(Collectors.toList());

        PurchasePageDto responseDto = new PurchasePageDto();
        responseDto.setProducts(purchaseDtoList);
        responseDto.setTotalPages(purchasePage.getTotalPages());

        return responseDto;
    }


    @Transactional
    public Long purchaseProduct(String email, Long productId) {
        User user = userRepository.findByEmail2(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 상품의 판매자와 구매자가 동일한지 확인
        if (product.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("자신의 상품은 구매할 수 없습니다.");
        }

        if (product.getProductStatus() == 1) {
            Purchase purchase = new Purchase();
            purchase.setUser(user);
            purchase.setProduct(product);
            Date purchaseDate = new Date();
            purchase.setPurchaseDate(purchaseDate);
            purchase.setProductName(product.getProductName());
            purchase.setPrice(product.getPrice());


            String imageUrl = product.getImageUrl();
            purchase.setImageUrl(imageUrl);

            purchase.setSellerNickname(product.getUser().getUserNickname());
            purchase = purchaseRepository.save(purchase);

            purchase.setSellerId(product.getUser().getUserId());
            purchase = purchaseRepository.save(purchase);

            product.setPaymentDate(purchaseDate);
            product.setProductStatus(0);
            productRepository.save(product);

            List<Like> likes = likeRepository.findByProductName(product.getProductName());
            for (Like like : likes) {
                like.setProductStatus(0);
            }
            likeRepository.saveAll(likes);

            return purchase.getPurchaseId();
        } else {
            throw new RuntimeException("판매중인 물건이 아닙니다.");
        }
    }
}