package com.github.tradewebproject.controller;

import com.github.tradewebproject.Dto.Like.LikePageDto;
import com.github.tradewebproject.Dto.Like.ProductLikeRequestDto;
import com.github.tradewebproject.Dto.Purchase.PurchasePageDto;
import com.github.tradewebproject.domain.Like;
import com.github.tradewebproject.service.Like.LikeProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class LikeProductController {

    private final LikeProductService likeProductService;

    public LikeProductController(LikeProductService likeProductService) {
        this.likeProductService = likeProductService;
    }

    @PostMapping("/likes")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "상품 찜하기/찜 해제", description = "사용자가 상품을 찜하거나 찜을 해제합니다.")
    public ResponseEntity<?> likeProduct(
            @RequestBody ProductLikeRequestDto request,
            Principal principal) {
        String userEmail = principal.getName();
        Long likeProductId = likeProductService.toggleLikeProduct(userEmail, request.getProductId());
        Map<String, Object> response = new HashMap<>();
        if (likeProductId == -1L) {
            response.put("message", "상품 찜이 해제되었습니다.");
        } else {
            response.put("message", "상품이 정상적으로 찜되었습니다.");
            response.put("likeProductId", likeProductId);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/likes/user/{userId}")
    @Operation(summary = "사용자의 찜 목록 조회", description = "특정 사용자의 찜한 상품 목록을 조회합니다.")
    public LikePageDto getPurchaseByUserId(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 당 상품 수(디폴트값 10개)") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 방식 (asc: 오름차순, desc: 내림차순)") @RequestParam(defaultValue = "asc") String sort) {
        return likeProductService.getLikeByUserId(userId, page, size, sort);
    }

    @GetMapping("/likes/product/{productId}")
    @Operation(summary = "상품의 찜 목록 조회", description = "특정 상품을 찜한 사용자 목록을 조회합니다.")
    public LikePageDto getLikesByProductId(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 당 사용자 수(디폴트값 10명)") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 방식 (asc: 오름차순, desc: 내림차순)") @RequestParam(defaultValue = "asc") String sort) {
        return likeProductService.getLikesByProductId(productId, page, size, sort);
    }
}