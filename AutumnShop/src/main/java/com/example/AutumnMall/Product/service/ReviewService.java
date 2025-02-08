package com.example.AutumnMall.Product.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Product.domain.Rating;
import com.example.AutumnMall.Product.domain.Review;
import com.example.AutumnMall.Product.dto.ReviewResponseDto;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Product.repository.ProductRepository;
import com.example.AutumnMall.Product.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    // 상품명 등록
    public ReviewResponseDto addReview(Long memberId, Long productId, String content, int rating){
        try {
            log.info("리뷰 등록 요청. 회원 ID: {}, 상품 ID: {}", memberId, productId);  // 리뷰 등록 요청 로그

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원 ID {}를 찾을 수 없습니다.", memberId);  // 오류 로그
                        return new RuntimeException("멤버를 찾을 수 없습니다.");
                    });
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                        log.error("상품 ID {}를 찾을 수 없습니다.", productId);  // 오류 로그
                        return new RuntimeException("물품을 찾을 수 없습니다.");
                    });

            Review review = Review.builder()
                    .product(product)
                    .member(member)
                    .content(content)
                    .rating(rating)
                    .createdAt(LocalDateTime.now())
                    .build();
            reviewRepository.save(review);

            log.info("리뷰 등록 완료. 리뷰 ID: {}", review.getId());  // 리뷰 등록 완료 로그

            // 제품의 평점 업데이트
            List<Review> reviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);

            double averageRating = reviews.stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);
            Rating productRating = product.getRating();
            productRating.setRate(averageRating);

            productRepository.save(product);

            log.info("상품 ID {}의 평점 업데이트 완료. 새로운 평점: {}", productId, averageRating);  // 평점 업데이트 로그

            return new ReviewResponseDto(
                    review.getId(),
                    review.getContent(),
                    review.getRating(),
                    review.getMember().getName(),
                    review.getCreatedAt(),
                    review.getMember().getMemberId()
            );
        } catch (RuntimeException e) {
            log.error("상품평 추가 실패: {}", e.getMessage(), e);  // 오류 발생 로그
            throw e;
        }
    }

    @Transactional(readOnly = true)
    // 해당 상품의 상품평 목록 가져오기
    public List<ReviewResponseDto> getReview(Long productId){
        try {
            log.info("상품 ID {}의 상품평 목록 조회 요청", productId);  // 상품평 목록 조회 로그

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                        log.error("상품 ID {}를 찾을 수 없습니다.", productId);  // 오류 로그
                        return new RuntimeException("물품을 찾을 수 없습니다.");
                    });
            List<Review> reviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);

            log.info("상품 ID {}의 상품평 {}개 조회 완료", productId, reviews.size());  // 상품평 조회 완료 로그

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
        } catch (RuntimeException e) {
            log.error("해당 상품의 상품평 조회 실패: {}", e.getMessage(), e);  // 오류 발생 로그
            throw e;
        }
    }
}
