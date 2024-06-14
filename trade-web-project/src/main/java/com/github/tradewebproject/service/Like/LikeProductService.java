package com.github.tradewebproject.service.Like;

import com.github.tradewebproject.Dto.Like.LikePageDto;
import com.github.tradewebproject.Dto.Like.LikeProductDto;
import com.github.tradewebproject.Dto.Purchase.PurchaseDto;
import com.github.tradewebproject.Dto.Purchase.PurchasePageDto;
import com.github.tradewebproject.domain.Like;
import com.github.tradewebproject.domain.Product;
import com.github.tradewebproject.domain.Purchase;
import com.github.tradewebproject.domain.User;
import com.github.tradewebproject.repository.Like.LikeRepository;
import com.github.tradewebproject.repository.Product.ProductRepository;
import com.github.tradewebproject.repository.User.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LikeProductService {

    @Value("${UPLOAD_DIR}") // 환경변수로부터 업로드 디렉토리 경로를 가져옵니다.
    private String uploadDir;

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final LikeRepository likeRepository;

    public LikeProductService(UserRepository userRepository, ProductRepository productRepository, LikeRepository likeRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.likeRepository = likeRepository;
    }

    public LikePageDto getLikeByUserId(Long userId, int page, int size, String sort) {

        Sort sortBy = Sort.by("price");
        if (sort.equals("asc")) {
            sortBy = Sort.by(Sort.Direction.ASC, "price");
        } else if ("desc".equalsIgnoreCase(sort)) {
            sortBy = Sort.by(Sort.Direction.DESC, "price");
        }

        Pageable pageable = PageRequest.of(page - 1, size, sortBy);
        Page<Like> likePage = likeRepository.findByUserUserId(userId,pageable);

        List<LikeProductDto> likeProductDtoList = likePage.stream().map(product -> {
            LikeProductDto dto = new LikeProductDto();
            dto.setLikeProductId(product.getLikeProductId());
            dto.setUserId(product.getUser().getUserId());
            dto.setProductId(product.getProduct().getProductId());
            dto.setProductName(product.getProduct().getProductName());
            dto.setPrice(product.getPrice());
            dto.setSellerNickname(product.getProduct().getUser().getUserNickname());
            dto.setProductStatus(product.getProductStatus());
            // 이미지 파일 경로 설정
            String imageUrl = product.getImageUrl();
            dto.setImageUrl(imageUrl);

            return dto;
        }).collect(Collectors.toList());

        LikePageDto responseDto = new LikePageDto();
        responseDto.setProducts(likeProductDtoList);
        responseDto.setTotalPages(likePage.getTotalPages());

        return responseDto;
    }

    @Transactional
    public Long toggleLikeProduct(String email, Long productId) {
        User user = userRepository.findByEmail2(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("자신의 상품은 찜할 수 없습니다.");
        }

        if (product.getProductStatus() != 1) {
            throw new RuntimeException("판매중인 상품만 찜할 수 있습니다.");
        }

        Optional<Like> existingLike = likeRepository.findByUserAndProduct_ProductId(user, productId);
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            return -1L; // 찜이 해제됨을 나타내기 위해 음수 값을 반환
        } else {
            Like like = new Like();
            like.setUser(user);
            like.setProductName(product.getProductName());
            like.setPrice(product.getPrice());
            like.setProductStatus(product.getProductStatus());
            like.setProduct(product);
            String imageUrl = "/images/" + product.getImageUrl();
            like.setImageUrl(imageUrl);

            like = likeRepository.save(like);
            return like.getLikeProductId(); // 찜이 추가된 경우 likeProductId를 반환
        }
    }


}