package com.example.AutumnMall.dto;

import com.example.AutumnMall.domain.Review;
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

    public ReviewResponseDto(Review review){
        this.id = review.getId();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.authorName = review.getMember().getName();
        this.createdAt = review.getCreatedAt();
    }
}
