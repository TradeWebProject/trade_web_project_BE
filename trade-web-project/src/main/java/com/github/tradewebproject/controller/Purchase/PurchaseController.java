package com.github.tradewebproject.controller.Purchase;

import com.github.tradewebproject.Dto.Purchase.PurchasePageDto;
import com.github.tradewebproject.Dto.Purchase.PurchaseRequestDto;
import com.github.tradewebproject.service.Purchase.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping("/purchase/user/{userId}")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "사용자 ID로 상품 조회", description = "사용자 ID에 해당하는 사용자가 등록한 상품 목록을 조회합니다. 페이지와 정렬 방식에 따라 조회할 수 있습니다.")
    public PurchasePageDto getPurchaseByUserId(
            @Parameter(description = "사용자 ID") @PathVariable Long userId,
            @Parameter(description = "페이지 번호 (1부터 시작)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지 당 상품 수(디폴트값 10개)") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "정렬 방식 (asc: 오름차순, desc: 내림차순)") @RequestParam(defaultValue = "asc") String sort) {
        return purchaseService.getPurchaseByUserId(userId, page, size, sort);
    }

    @PostMapping("/purchase")
    @SecurityRequirement(name = "BearerAuth")
    @Operation(summary = "상품 구매", description = "사용자가 상품을 구매합니다.")
    public ResponseEntity<?> purchaseProduct(
            @Parameter(description = "구매할 상품 ID", required = true) @RequestBody PurchaseRequestDto purchaseRequest,
            Principal principal) {
        String userEmail = principal.getName();
        Long purchaseId = purchaseService.purchaseProduct(userEmail, purchaseRequest.getProductId());
        Map<String, Object> response = new HashMap<>();
        response.put("message", "상품이 정상적으로 구매되었습니다.");
        response.put("purchaseId", purchaseId);
        return ResponseEntity.ok(response);
    }


}
