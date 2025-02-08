package com.example.AutumnMall.Member.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Member.domain.RecentProduct;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Product.repository.ProductRepository;
import com.example.AutumnMall.Member.repository.RecentProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecentProductService {
    private final RecentProductRepository recentProductRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addRecentProduct(Long memberId, Long productId){
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("물품을 찾을 수 없습니다."));

            Optional<RecentProduct> existing = recentProductRepository.findByMemberAndProduct(member, product);

            if (existing.isPresent()) {
                RecentProduct recentProduct = existing.get();
                recentProduct.setViewedAt(LocalDateTime.now());

                // 로그 추가: 최근 본 제품 업데이트
                log.info("회원 {}가 제품 {}을(를) 다시 조회했습니다. 조회 시간: {}", memberId, productId, recentProduct.getViewedAt());
            } else {
                List<RecentProduct> recentProducts = recentProductRepository.findByMember(member);

                if (recentProducts.size() >= 5) {
                    recentProducts.sort(Comparator.comparing(RecentProduct::getId));
                    RecentProduct oldRecentProduct = recentProducts.get(0);
                    recentProductRepository.delete(oldRecentProduct);

                    // 로그 추가: 오래된 최근 본 제품 삭제
                    log.info("회원 {}의 최근 본 제품이 5개를 초과하여 제품 {}이(가) 삭제되었습니다.", memberId, oldRecentProduct.getProduct().getId());
                }

                RecentProduct newRecentProduct = RecentProduct.builder()
                        .member(member)
                        .product(product)
                        .viewedAt(LocalDateTime.now())
                        .build();
                recentProductRepository.save(newRecentProduct);

                // 로그 추가: 새로운 최근 본 제품 추가
                log.info("회원 {}가 제품 {}을(를) 최근 본 제품 목록에 추가했습니다. 조회 시간: {}", memberId, productId, newRecentProduct.getViewedAt());
            }
        }catch(RuntimeException e){
            log.error("최근 본 제품목록 추가 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public List<Product> getRecentProducts(Long memberId){
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

            List<RecentProduct> recentProducts = recentProductRepository.findTop5ByMemberOrderByViewedAtDesc(member);

            // 로그 추가: 최근 본 제품 조회
            log.info("회원 {}의 최근 본 5개 제품을 조회했습니다.", memberId);

            return recentProducts.stream()
                    .map(RecentProduct::getProduct)
                    .collect(Collectors.toList());
        }catch(RuntimeException e){
            log.error("최근 본 제품목록 조회 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }
}
