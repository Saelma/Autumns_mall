package com.example.AutumnMall.controller;

import com.example.AutumnMall.domain.Review;
import com.example.AutumnMall.dto.ReviewRequestDto;
import com.example.AutumnMall.dto.ReviewResponseDto;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products/{productId}/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResponseDto> addReview(
            @IfLogin LoginUserDto loginUserDto,
            @PathVariable Long productId,
            @RequestBody ReviewRequestDto reviewRequestDto
            ){
        if(loginUserDto.getMemberId() == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Review review = reviewService.addReview(
                loginUserDto.getMemberId(),
                productId,
                reviewRequestDto.getContent(),
                reviewRequestDto.getRating());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ReviewResponseDto(review));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponseDto>> getReviews(@PathVariable Long productId){
        List<ReviewResponseDto> reviews = reviewService.getReview(productId);
        return ResponseEntity.ok(reviews);
    }
}
