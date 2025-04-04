package com.example.AutumnMall.Product.controller;

import com.example.AutumnMall.utils.dto.ResponseWrapper;
import com.example.AutumnMall.Product.dto.ReviewRequestDto;
import com.example.AutumnMall.Product.dto.ReviewResponseDto;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.Payment.service.PaymentService;
import com.example.AutumnMall.Product.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ResponseWrapper<ReviewResponseDto>> addReview(
            @IfLogin LoginUserDto loginUserDto,
            @PathVariable Long productId,
            @RequestBody ReviewRequestDto reviewRequestDto
            ){
        if(loginUserDto.getMemberId() == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseWrapper<>(false, null ,"로그인이 필요합니다."));
        }

        boolean isPurchased = paymentService.purchasedProduct(loginUserDto.getMemberId(), productId);
        if(!isPurchased){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ResponseWrapper<>(false, null, "구매한 사용자만 상품평을 등록할 수 있습니다."));
        }


        ReviewResponseDto responseDto = reviewService.addReview(
                loginUserDto.getMemberId(),
                productId,
                reviewRequestDto);

        return ResponseEntity.ok(new ResponseWrapper<>(true, responseDto, "상품평이 등록되었습니다."));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@PathVariable Long productId){
        List<ReviewResponseDto> reviews = reviewService.getReview(productId);
        return ResponseEntity.ok(reviews);
    }
}
