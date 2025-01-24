package com.example.AutumnMall.controller;

import com.example.AutumnMall.domain.Product;
import com.example.AutumnMall.domain.RecentProduct;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.service.RecentProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recentProducts")
@RequiredArgsConstructor
public class RecentProductController {
    private final RecentProductService recentProductService;

    // 최근 본 상품 추가
    @PostMapping("/{productId}")
    public ResponseEntity<String> addRecentProduct(@IfLogin LoginUserDto loginUserDto,
                                                   @PathVariable Long productId){
        if(loginUserDto.getMemberId() == null || productId == null){
            return ResponseEntity.badRequest().body("최근 본 상품을 찾을 수 없습니다.");
        }

        recentProductService.addRecentProduct(loginUserDto.getMemberId(), productId);
        return ResponseEntity.ok("최근 본 상품에 등록했습니다");
    }

    @GetMapping
    public ResponseEntity<List<Product>> getRecentProduct(@IfLogin LoginUserDto loginUserDto){
        if(loginUserDto.getMemberId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Product> recentProducts = recentProductService.getRecentProducts(loginUserDto.getMemberId());

        if(recentProducts.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(recentProducts);
    }
}
