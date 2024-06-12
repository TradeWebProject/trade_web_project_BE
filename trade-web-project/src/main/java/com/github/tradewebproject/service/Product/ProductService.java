package com.github.tradewebproject.service.Product;

import com.github.tradewebproject.Dto.Product.ProductDTO;
import com.github.tradewebproject.Dto.Product.ProductPageResponseDto;
import com.github.tradewebproject.Dto.Product.ProductResponseDto;
import com.github.tradewebproject.domain.Product;
import com.github.tradewebproject.domain.User;
import com.github.tradewebproject.repository.Product.ProductRepository;
import com.github.tradewebproject.repository.User.UserJpaRepository;
import com.github.tradewebproject.repository.User.UserRepository;
import com.github.tradewebproject.util.FileStorageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {


    @Value("${UPLOAD_DIR}")
    private String uploadDir;

    @Autowired
    private FileStorageUtil fileStorageUtil;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    //    @Autowired
//    private CartItemRepository cartItemRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;



//    public ProductPageResponseDto getAvailableProducts(int page, int size, String sort) {
//        // 정렬 조건 설정
//        Sort sortBy = Sort.by("price");
//        if (sort.equals("asc")) {
//            sortBy = Sort.by(Sort.Direction.ASC, "price");
//        }
//        else if ("desc".equalsIgnoreCase(sort)) {
//            sortBy = Sort.by(Sort.Direction.DESC, "price");
//        }
//        else if ("enddate".equalsIgnoreCase(sort)) {
//            // enddate 가 가장 얼마 남지 않은 순 (오름차순)
//            sortBy = Sort.by(Sort.Direction.ASC, "endDate");
//        }
//        // Pageable 객체 생성 (페이지, 사이즈, 정렬 조건)
//        Pageable pageable = PageRequest.of(page - 1, size, sortBy);
//
//        // 데이터베이스에서 정렬 및 페이징 처리된 결과 조회
//        Page<Product> productsPage = productRepository.findAllByStockGreaterThanAndProductStatus(0, 1, pageable);
//
//        // Product 엔티티를 ProductResponseDto로 변환
//        List<ProductResponseDto> productDtos = productsPage.getContent().stream().map(product -> {
//            ProductResponseDto dto = new ProductResponseDto();
//            dto.setProductId(product.getProductId());
//            dto.setProductName(product.getProductName());
//            dto.setDescription(product.getDescription());
//            dto.setPrice(product.getPrice());
//            dto.setStartDate(product.getStartDate());
//            dto.setEndDate(product.getEndDate());
//            dto.setStock(product.getStock());
//            dto.setProductOption(product.getProductOption());
//            dto.setImageUrl(product.getImageUrl());
//            dto.setProductStatus(product.getProductStatus());
//            dto.setUserNickName(product.getUser().getUser_nickname());
//
//            // 이미지 파일을 Base64로 변환
//            try {
//                Path filePath = Paths.get(uploadDir).resolve(product.getImageUrl()).normalize();
//                byte[] imageBytes = Files.readAllBytes(filePath);
//                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//                dto.setImageBase64(base64Image);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            return dto;
//        }).collect(Collectors.toList());
//
//
//        ProductPageResponseDto responseDto = new ProductPageResponseDto();
//        responseDto.setProducts(productDtos);
//        responseDto.setTotalPages(productsPage.getTotalPages());
//
//        return responseDto;
//    }


//    public DetailProductDto getProductById(Long productId) {
//        return productRepository.findById(productId)
//                .filter(product -> product.getStock() > 0)
//                .map(product -> {
//                    // DetailProductDto 생성자를 사용해 초기화
//                    DetailProductDto dto = new DetailProductDto(
//                            product.getProductId(),
//                            product.getProductName(),
//                            product.getDescription(),
//                            product.getPrice(),
//                            product.getStock(),
//                            product.getUser().getUser_nickname(), // getUser_nickname() -> getUserNickName() 수정
//                            product.getProductOption(),
//                            product.getProductStatus(), // 순서 변경 적용
//                            product.getImageUrl(), // thumbNail -> imageUrl 수정
//                            product.getStartDate(),
//                            product.getEndDate(),
//                            product.getImagePaths()
//                    );
//
//                    List<String> base64images = new ArrayList<>();
//                    if (product.getImagePaths() != null && !product.getImagePaths().isEmpty()) {
//                        for (String imagePath : product.getImagePaths()) {
//                            try {
//                                Path filePath = Paths.get(uploadDir).resolve(imagePath).normalize();
//                                byte[] imageBytes = Files.readAllBytes(filePath);
//                                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//                                base64images.add(base64Image);
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        // 첫 번째 이미지를 썸네일 URL로 설정, 이미지가 존재하는 경우에만 설정
//                        if (!base64images.isEmpty()) {
//                            dto.setThumbnailUrl(base64images.get(0));
//                        }
//                    }
//                    dto.setBase64images(base64images); // Base64로 인코딩된 이미지 목록을 DTO에 설정
//
//                    return dto;
//                })
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)); // 상품을 찾을 수 없는 경우 예외 발생
//    }

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
            //dto.setStock(product.getStock());
            dto.setUserNickName(product.getUser().getUserNickname());
            //dto.setProductOption(product.getProductOption());
            dto.setProductStatus(product.getProductStatus());
            dto.setStartDate(product.getStartDate());
            dto.setEndDate(product.getEndDate());

            // 이미지 파일 경로 설정
            String imageUrl = Paths.get(uploadDir).resolve(product.getImageUrl()).normalize().toString();
            dto.setImageUrl(imageUrl);

            return dto;
        }).collect(Collectors.toList());

        ProductPageResponseDto responseDto = new ProductPageResponseDto();
        responseDto.setProducts(productDtoList);
        responseDto.setTotalPages(productsPage.getTotalPages());

        return responseDto;
    }





    public ProductDTO registerProduct(String email,ProductDTO productDTO) throws IOException {
        validateProductInfo(productDTO);
        User user = userRepository.findByEmail2(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

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
        //product.setStock(productDTO.getStock());
        product.setStartDate(productDTO.getStartDate());
        product.setEndDate(productDTO.getEndDate());
        product.setDescription(productDTO.getDescription());
        //product.setProductOption(productDTO.getProductOption());
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

        // 상품 정보 업데이트
        product.setProductName(productDTO.getProductName());
        product.setPrice(productDTO.getPrice());
        product.setDescription(productDTO.getDescription());
        //product.setStock(productDTO.getStock());
        product.setStartDate(productDTO.getStartDate());
        product.setEndDate(productDTO.getEndDate());
        //product.setProductOption(productDTO.getProductOption());
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

    //    public void deleteProduct(Long productId, String email, String password) {
//        // 사용자 찾기 (이메일로 조회)
//        UserEntity user = userRepository.findByEmail2(email)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // 비밀번호 확인
//        if (!passwordEncoder.matches(password, user.getUser_password())) {
//            throw new RuntimeException("Incorrect password.");
//        }
//
//        // 상품 찾기
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
//
//        // 상품 소유자 확인
//        if (!product.getUser().getUserId().equals(user.getUserId())) {
//            throw new RuntimeException("You do not have permission to delete this product.");
//        }
//
//        // 상품 삭제
//        productRepository.deleteById(productId);
//    }
}