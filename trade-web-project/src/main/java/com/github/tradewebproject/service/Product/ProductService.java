package com.github.tradewebproject.service.Product;

import com.github.tradewebproject.Dto.Product.DetailProductDto;
import com.github.tradewebproject.Dto.Product.ProductDTO;
import com.github.tradewebproject.Dto.Product.ProductPageResponseDto;
import com.github.tradewebproject.Dto.Product.ProductResponseDto;
import com.github.tradewebproject.Dto.Review.ReviewResponseDto;
import com.github.tradewebproject.Dto.Review.SellerReviewResponseDto;
import com.github.tradewebproject.domain.Product;
import com.github.tradewebproject.domain.ProductSpecification;
import com.github.tradewebproject.domain.Review;
import com.github.tradewebproject.domain.User;
import com.github.tradewebproject.repository.Like.LikeRepository;
import com.github.tradewebproject.repository.Product.ProductRepository;
import com.github.tradewebproject.repository.Purchase.PurchaseRepository;
import com.github.tradewebproject.repository.Review.ReviewRepository;
import com.github.tradewebproject.repository.User.UserJpaRepository;
import com.github.tradewebproject.repository.User.UserRepository;
import com.github.tradewebproject.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {


    @Value("${UPLOAD_DIR}")
    private String uploadDir;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PurchaseRepository purchaseRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ReviewRepository reviewRepository;


    public ProductPageResponseDto getProductsByUserId(Long userId, int page, int size, String sort) {

        Sort sortBy = Sort.by("price");
        if (sort.equals("asc")) {
            sortBy = Sort.by(Sort.Direction.ASC, "price");
        } else if ("desc".equalsIgnoreCase(sort)) {
            sortBy = Sort.by(Sort.Direction.DESC, "price");
        } else if ("enddate".equalsIgnoreCase(sort)) {
            // enddate 가 가장 얼마 남지 않은 순 (오름차순)
            sortBy = Sort.by(Sort.Direction.ASC, "endDate");
        }

        Pageable pageable = PageRequest.of(page - 1, size, sortBy);
        Page<Product> productsPage = productRepository.findByUserUserIdAndProductStatus(userId, 1, pageable);

        List<ProductResponseDto> productDtoList = productsPage.stream().map(product -> {
            ProductResponseDto dto = new ProductResponseDto();
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setUserNickName(product.getUser().getUserNickname());
            dto.setCategory(product.getCategory());
            dto.setProductStatus(product.getProductStatus());
            dto.setTotalLikes(likeRepository.countByProductProductId(product.getProductId()));
            dto.setProductQuality(product.getProductQuality());
            dto.setStartDate(product.getStartDate());
            dto.setEndDate(product.getEndDate());

            // 이미지 파일 경로 설정
            String imageUrl = "/images/" + product.getImageUrl();
            dto.setImageUrl(imageUrl);

            return dto;
        }).collect(Collectors.toList());

        ProductPageResponseDto responseDto = new ProductPageResponseDto();
        responseDto.setProducts(productDtoList);
        responseDto.setTotalPages(productsPage.getTotalPages());

        return responseDto;
    }


    public ProductDTO registerProduct(String email, ProductDTO productDTO) throws IOException {
        validateProductInfo(productDTO);
        User user = userRepository.findByEmail2(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<String> validCategories = Arrays.asList("전자기기", "의류", "가전", "문구", "도서", "신발", "여행용품", "스포츠");
        if (!validCategories.contains(productDTO.getCategory())) {
            throw new RuntimeException("카테고리명을 적절하게 입력해주세요 (전자기기 , 의류, 가전 , 문구 , 도서 ,신발 ,여행용품 , 스포츠 중 하나) 현재 입력한 카테고리 명 : " + productDTO.getCategory());
        }
        List<String> validQualities = Arrays.asList("새상품", "중고상품");
        if (!validQualities.contains(productDTO.getProductQuality())) {
            throw new RuntimeException("상품 상태를 적절하게 입력해주세요 (새상품 , 중고상품 중 하나) 현재 입력한 상품 상태 명 : " + productDTO.getProductQuality());
        }

        List<MultipartFile> files = productDTO.getFiles() != null ? productDTO.getFiles() : new ArrayList<>();

        List<String> imagePaths = files.stream()
                .map(file -> {
                    try {
                        return fileStorageUtil.storeFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
                    }
                })
                .collect(Collectors.toList());


        // 상품 정보 설정
        Product product = new Product();
        product.setProductName(productDTO.getProductName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setStartDate(productDTO.getStartDate());
        product.setEndDate(productDTO.getEndDate());
        product.setDescription(productDTO.getDescription());
        product.setCategory(productDTO.getCategory());
        product.setProductQuality(productDTO.getProductQuality());
        product.setUser(user);
        productDTO.setUserNickName(productDTO.getUserNickName());

        Date now = new Date();
        product.setProductStatus(product.getEndDate().compareTo(now) >= 0 ? 1 : 0);


        // 첫 번째 이미지 경로를 imageUrl에 설정
        if (!imagePaths.isEmpty()) {
            product.setImageUrl(imagePaths.get(0)); // 첫 번째 이미지 경로를 imageUrl에 설정
        }

        imagePaths.forEach(product::addImage);  // 각 이미지 경로를 Product에 추가
        // 상품 등록
        Product registeredProduct = productRepository.save(product);
        // 등록된 상품 정보를 DTO로 변환하여 반환
        return new ProductDTO(registeredProduct);
    }

    private void validateProductInfo(ProductDTO productDTO) {
        // 필요한 모든 상품 정보가 입력되었는지 확인하는 로직 추가
        // 예: productName, price, startDate, endDate, description 등
        // 필수 정보가 빠진 경우 예외를 던지도록 구현
        if (productDTO.getProductName() == null || productDTO.getProductName().isEmpty()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (productDTO.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0");
        }
        if (productDTO.getStartDate() == null || productDTO.getEndDate() == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }
        if (productDTO.getDescription() == null || productDTO.getDescription().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
    }


    public ProductDTO updateProduct(Long productId, String email, ProductDTO productDTO, String password) throws IOException {
        // 사용자 찾기 (이메일 통해 조회)
        User user = userRepository.findByEmail2(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 비밀번호 확인
        if (!passwordEncoder.matches(password, user.getUserPassword())) {
            throw new RuntimeException("Incorrect password.");
        }

        // 상품 찾기
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 상품 소유자 확인
        if (!product.getUser().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You do not have permission to update this product.");
        }

        List<String> validCategories = Arrays.asList("전자기기", "의류", "가전", "문구", "도서", "신발", "여행용품", "스포츠");
        if (!validCategories.contains(productDTO.getCategory())) {
            throw new RuntimeException("카테고리명을 적절하게 입력해주세요 (전자기기 , 의류, 가전 , 문구 , 도서 ,신발 ,여행용품 , 스포츠 중 하나) 현재 입력한 카테고리 명 : " + productDTO.getCategory());
        }

        List<String> validQualities = Arrays.asList("새상품", "중고상품");
        if (!validQualities.contains(productDTO.getProductQuality())) {
            throw new RuntimeException("상품 상태를 적절하게 입력해주세요 (새상품 , 중고상품 중 하나) 현재 입력한 상품 상태 명 : " + productDTO.getProductQuality());
        }

        // 상품 정보 업데이트
        product.setProductName(productDTO.getProductName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        product.setStartDate(productDTO.getStartDate());
        product.setEndDate(productDTO.getEndDate());
        product.setCategory(productDTO.getCategory());
        product.setProductQuality(productDTO.getProductQuality());

        Date now = new Date();
        product.setProductStatus(product.getEndDate().compareTo(now) >= 0 ? 1 : 0);

        // 이미지 처리
        List<MultipartFile> files = productDTO.getFiles() != null ? productDTO.getFiles() : new ArrayList<>();
        List<String> imagePaths = files.stream()
                .map(file -> {
                    try {
                        return fileStorageUtil.storeFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to store file: " + file.getOriginalFilename(), e);
                    }
                })
                .collect(Collectors.toList());

        // 기존 이미지 정보 제거 후 새 이미지 정보 추가
        product.clearImages();
        imagePaths.forEach(product::addImage);

        // 첫 번째 이미지를 메인 이미지로 설정
        if (!imagePaths.isEmpty()) {
            product.setImageUrl(imagePaths.get(0));
        }

        // 상품 정보 저장
        Product updatedProduct = productRepository.save(product);

        // 업데이트된 상품 정보를 DTO로 변환하여 반환
        return new ProductDTO(updatedProduct);
    }

    // 전체 상품조회 (토큰 필요x)
    public ProductPageResponseDto getAllProducts(int page, int size, String sort) {
        Sort sortBy = Sort.by("price");
        if ("asc".equalsIgnoreCase(sort)) {
            sortBy = Sort.by(Sort.Direction.ASC, "price");
        } else if ("desc".equalsIgnoreCase(sort)) {
            sortBy = Sort.by(Sort.Direction.DESC, "price");
        } else if ("enddate".equalsIgnoreCase(sort)) {
            sortBy = Sort.by(Sort.Direction.ASC, "endDate");
        }

        Pageable pageable = PageRequest.of(page - 1, size, sortBy);
        Page<Product> productsPage = productRepository.findAllByProductStatus(1, pageable);

        List<ProductResponseDto> productDtos = productsPage.stream().map(product -> {
            ProductResponseDto dto = new ProductResponseDto();
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setUserNickName(product.getUser().getUserNickname());
            dto.setProductStatus(product.getProductStatus());
            dto.setTotalLikes(likeRepository.countByProductProductId(product.getProductId()));
            dto.setProductQuality(product.getProductQuality());
            dto.setCategory(product.getCategory());
            dto.setStartDate(product.getStartDate());
            dto.setEndDate(product.getEndDate());

            String imageUrl = "/images/" + product.getImageUrl();
            dto.setImageUrl(imageUrl);

            return dto;
        }).collect(Collectors.toList());

        ProductPageResponseDto responseDto = new ProductPageResponseDto();
        responseDto.setProducts(productDtos);
        responseDto.setTotalPages(productsPage.getTotalPages());

        return responseDto;
    }

    // 상품 상세 조회
    public DetailProductDto getProductById(Long productId) {
        return productRepository.findById(productId)
                .map(product -> {
                    List<String> imagePathUrl = new ArrayList<>();
                    for (String imagePath : product.getImagePaths()) {
                        String imageUrl = "/images/" + imagePath;
                        imagePathUrl.add(imageUrl);
                    }

                    String thumbnailUrl = imagePathUrl.isEmpty() ? null : imagePathUrl.get(0);

                    // 리뷰 리스트 생성
                    List<SellerReviewResponseDto> reviews = new ArrayList<>();
                    String baseImageUrl = "/images/";
                    List<Review> productReviews = reviewRepository.findByProduct(product);
                    for (Review review : productReviews) {
                        reviews.add(new SellerReviewResponseDto(
                                product.getProductName(),
                                review.getReviewDate(),
                                review.getUser().getUserNickname(),
                                baseImageUrl + review.getUser().getUserImg(),
                                review.getRating(),
                                review.getReviewContent(),
                                review.getReviewTitle()
                        ));
                    }

                    // 총 찜 수 계산
                    int totalLikes = likeRepository.countByProductProductId(productId);

                    // 해당 물건을 등록한 유저의 총 리뷰 수 계산
                    Long userId = product.getUser().getUserId();

                    // Seller ID가 userId와 일치하는 리뷰 수 계산
                    int totalUserReviews = reviewRepository.countBySellerId(userId);

                    // Seller ID가 userId와 일치하는 리뷰들의 별점 평균 계산
                    double averageRating = reviewRepository.findBySellerId(userId).stream()
                            .mapToDouble(Review::getRating)
                            .average()
                            .orElse(0);

                    // 해당 물건을 등록한 유저의 총 판매 수 계산
                    int totalSales = purchaseRepository.countBySellerId(userId);

                    DetailProductDto dto = new DetailProductDto(
                            product.getProductId(),
                            product.getProductName(),
                            product.getDescription(),
                            product.getPrice(),
                            product.getCategory(),
                            product.getUser().getUserNickname(),
                            product.getUser().getUserId(),
                            product.getProductStatus(),
                            product.getProductQuality(),
                            product.getStartDate(),
                            product.getEndDate(),
                            thumbnailUrl,
                            product.getImagePaths()
                    );

                    dto.setImagePathUrl(imagePathUrl);
                    dto.setReviews(reviews); // reviews 리스트를 설정
                    dto.setTotalLikes(totalLikes);
                    dto.setTotalRatings(totalUserReviews);
                    dto.setTotalSales(totalSales);
                    dto.setAverageRating(averageRating);

                    return dto;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // 상품 검색
    public ProductPageResponseDto searchProducts(String keyword, int page, int size, String sort, Integer minPrice, Integer maxPrice, String category, String status) {
        Sort sortBy = Sort.by("price");
        if ("asc".equalsIgnoreCase(sort)) {
            sortBy = Sort.by(Sort.Direction.ASC, "price");
        } else if ("desc".equalsIgnoreCase(sort)) {
            sortBy = Sort.by(Sort.Direction.DESC, "price");
        } else if ("enddate".equalsIgnoreCase(sort)) {
            sortBy = Sort.by(Sort.Direction.ASC, "endDate");
        }

        Pageable pageable = PageRequest.of(page - 1, size, sortBy);
        Specification<Product> spec = ProductSpecification.filterProducts(keyword, minPrice, maxPrice, category, status);
        Page<Product> productsPage = productRepository.findAll(spec, pageable);

        List<ProductResponseDto> productDtos = productsPage.stream().map(product -> {
            ProductResponseDto dto = new ProductResponseDto();
            dto.setProductId(product.getProductId());
            dto.setProductName(product.getProductName());
            dto.setDescription(product.getDescription());
            dto.setPrice(product.getPrice());
            dto.setUserNickName(product.getUser().getUserNickname());
            dto.setProductStatus(product.getProductStatus());
            dto.setTotalLikes(likeRepository.countByProductProductId(product.getProductId()));
            dto.setProductQuality(product.getProductQuality());
            dto.setCategory(product.getCategory());
            dto.setStartDate(product.getStartDate());
            dto.setEndDate(product.getEndDate());

            String imageUrl = "/images/" + product.getImageUrl();
            dto.setImageUrl(imageUrl);

            return dto;
        }).collect(Collectors.toList());

        ProductPageResponseDto responseDto = new ProductPageResponseDto();
        responseDto.setProducts(productDtos);
        responseDto.setTotalPages(productsPage.getTotalPages());

        return responseDto;
    }


    public ProductPageResponseDto getProductsByUserInterests(String email, int page, int size, String sort) {
        User user = userRepository.findByEmail2(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<String> userInterests = user.getUserInterests();

        // 정렬 방식 설정
        Sort sortBy = Sort.by("price");
        if (sort.equalsIgnoreCase("asc")) {
            sortBy = Sort.by(Sort.Direction.ASC, "price");
        } else if (sort.equalsIgnoreCase("desc")) {
            sortBy = Sort.by(Sort.Direction.DESC, "price");
        }

        Pageable pageable = PageRequest.of(page - 1, size, sortBy);
        Page<Product> productsPage = productRepository.findByCategoryInAndProductStatus(userInterests, 1, pageable);

        List<ProductResponseDto> productResponseDtos = productsPage.stream()
                .map(product -> {
                    String imageUrl = "/images/" + product.getImageUrl();
                    int totalLikes = likeRepository.countByProductProductId(product.getProductId());
                    return new ProductResponseDto(
                            product.getProductId(),
                            product.getProductName(),
                            product.getDescription(),
                            product.getPrice(),
                            imageUrl,
                            product.getUser().getUserNickname(),
                            product.getCategory(),
                            product.getProductStatus(),
                            totalLikes,
                            product.getProductQuality(),
                            product.getStartDate(),
                            product.getEndDate(),
                            product.getPaymentDate()
                    );
                })
                .collect(Collectors.toList());

        ProductPageResponseDto productPageResponseDto = new ProductPageResponseDto();
        productPageResponseDto.setProducts(productResponseDtos);
        productPageResponseDto.setTotalPages(productsPage.getTotalPages());

        return productPageResponseDto;
    }

//    public List<ProductResponseDto> getProductsByUserInterests(String email) {
//        User user = userRepository.findByEmail2(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//        List<String> userInterests = user.getUserInterests();
//        List<Product> products = productRepository.findByCategoryIn(userInterests);
//
//        return products.stream()
//                .map(product -> {
//                    String imageUrl = "/images/" + product.getImageUrl();
//                    return new ProductResponseDto(
//                            product.getProductId(),
//                            product.getProductName(),
//                            product.getDescription(),
//                            product.getPrice(),
//                            imageUrl,
//                            product.getUser().getUserNickname(),
//                            product.getCategory(),
//                            product.getProductStatus(),
//                            product.getStartDate(),
//                            product.getEndDate(),
//                            product.getPaymentDate()
//                    );
//                })
//                .collect(Collectors.toList());
//    }

}

