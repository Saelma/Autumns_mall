package com.example.AutumnMall.Product.dto;

import com.example.AutumnMall.Product.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long id;
    private String content;
    private int rating;
    private String authorName;
    private LocalDateTime createdAt;
    private Long memberId;
}
