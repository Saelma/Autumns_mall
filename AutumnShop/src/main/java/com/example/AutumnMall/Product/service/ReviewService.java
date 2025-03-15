package com.example.AutumnMall.Product.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Product.domain.Rating;
import com.example.AutumnMall.Product.domain.Review;
import com.example.AutumnMall.Product.dto.ReviewRequestDto;
import com.example.AutumnMall.Product.dto.ReviewResponseDto;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Product.repository.ProductRepository;
import com.example.AutumnMall.Product.repository.ReviewRepository;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import com.example.AutumnMall.utils.CustomBean.CustomBeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    @Autowired
    private final CustomBeanUtils customBeanUtils;

    // 상품명 등록
    public ReviewResponseDto addReview(Long memberId, Long productId, ReviewRequestDto reviewRequestDto){
        try {
            log.info("리뷰 등록 요청. 회원 ID: {}, 상품 ID: {}", memberId, productId);  // 리뷰 등록 요청 로그

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", + memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });
            Product product = productRepository.findByIdWithLock(productId)  // 비관적 락 적용된 조회
                    .orElseThrow(() -> {
                        log.error("물품이 존재하지 않습니다. 물품Id: {}", productId);
                        return new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND);
                    });
            Optional<Review> findReview = reviewRepository.findByMemberAndProduct(member, product);

            findReview.ifPresent(review -> {
                log.error("사용자가 리뷰를 중복 등록하려고 시도했습니다. 회원Id: {}, 물품Id: {}", + memberId, productId);
                throw new BusinessLogicException(ExceptionCode.REVIEW_ALREADY_REGISTARTION);
            });

            Review review = Review.builder()
                    .product(product)
                    .member(member)
                    .content(reviewRequestDto.getContent())
                    .rating(reviewRequestDto.getRating())
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

            // ReviewResponseDto 객체 생성 및 필드 복사 ( id, content, rating, createdAt )
            ReviewResponseDto dto = new ReviewResponseDto();
            customBeanUtils.copyProperties(review, dto);  // 기본 필드 복사

            // 복잡한 속성 값 수동 설정
            dto.setAuthorName(review.getMember().getName());
            dto.setMemberId(review.getMember().getMemberId());

            return dto;
        } catch (BusinessLogicException e) {
            log.error("상품평 추가 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND);
        } catch (Exception e) {
            log.error("상품평 추가 실패 {} ", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    // 해당 상품의 상품평 목록 가져오기
    public List<ReviewResponseDto> getReview(Long productId){
        try {
            log.info("상품 ID {}의 상품평 목록 조회 요청", productId);  // 상품평 목록 조회 로그

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                        log.error("물품이 존재하지 않습니다. 물품Id: {}", productId);
                        return new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND);
                    });
            List<Review> reviews = reviewRepository.findByProductOrderByCreatedAtDesc(product);

            log.info("상품 ID {}의 상품평 {}개 조회 완료", productId, reviews.size());  // 상품평 조회 완료 로그

            return reviews.stream()
                    .map(review -> {
                        // Review 객체에서 ReviewResponseDto로 필드 값 복사 ( id, content, rating, createdAt )
                        ReviewResponseDto dto = new ReviewResponseDto();
                        customBeanUtils.copyProperties(review, dto);

                        dto.setAuthorName(review.getMember().getName());
                        dto.setMemberId(review.getMember().getMemberId());

                        return dto;
                    })
                    .collect(Collectors.toList());
        } catch (BusinessLogicException e) {
            log.error("해당 상품평의 상품평 조회 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.REVIEW_NOT_FOUND);
        } catch (Exception e) {
            log.error("해당 상품평의 상품평 조회 실패 {} ", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }
}
