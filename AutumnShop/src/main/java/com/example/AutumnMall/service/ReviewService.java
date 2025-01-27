package com.example.AutumnMall.service;

import com.example.AutumnMall.domain.Member;
import com.example.AutumnMall.domain.Product;
import com.example.AutumnMall.domain.Rating;
import com.example.AutumnMall.domain.Review;
import com.example.AutumnMall.dto.ReviewResponseDto;
import com.example.AutumnMall.repository.MemberRepository;
import com.example.AutumnMall.repository.ProductRepository;
import com.example.AutumnMall.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    // 상품명 등록
    public ReviewResponseDto addReview(Long memberId, Long productId, String content, int rating){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("물품을 찾을 수 없습니다."));

        Review review = Review.builder()
                .product(product)
                .member(member)
                .content(content)
                .rating(rating)
                .createdAt(LocalDateTime.now())
                .build();
        reviewRepository.save(review);


        // 제품의 평점 업데이트
        List<Review> reviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);

        double averageRating = reviews.stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
        Rating productRating = product.getRating();
        productRating.setRate(averageRating);

        productRepository.save(product);

        return new ReviewResponseDto(
                review.getId(),
                review.getContent(),
                review.getRating(),
                review.getMember().getName(),
                review.getCreatedAt(),
                review.getMember().getMemberId()
        );
    }

    @Transactional(readOnly = true)
    // 해당 상품의 상품평 목록 가져오기
    public List<ReviewResponseDto> getReview(Long productId){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("물품을 찾을 수 없습니다."));
        List<Review> reviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);

        return reviews.stream()
                .map(review -> new ReviewResponseDto(
                        review.getId(),
                        review.getContent(),
                        review.getRating(),
                        review.getMember().getName(),
                        review.getCreatedAt(),
                        review.getMember().getMemberId()
                ))
                .collect(Collectors.toList());
    }
}
