package com.example.AutumnMall.Member.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Member.domain.RecentProduct;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Product.repository.ProductRepository;
import com.example.AutumnMall.Member.repository.RecentProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecentProductService {
    private final RecentProductRepository recentProductRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void addRecentProduct(Long memberId, Long productId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("물품을 찾을 수 없습니다."));

        Optional<RecentProduct> existing = recentProductRepository.findByMemberAndProduct(member, product);

        if(existing.isPresent()){
            RecentProduct recentProduct = existing.get();
            recentProduct.setViewedAt(LocalDateTime.now());
        }else {
            List<RecentProduct> recentProducts = recentProductRepository.findByMember(member);

            if(recentProducts.size() >= 5){
                recentProducts.sort(Comparator.comparing(RecentProduct::getId));
                RecentProduct oldRecentProduct = recentProducts.get(0);
                recentProductRepository.delete(oldRecentProduct);
            }

            RecentProduct newRecentProduct = RecentProduct.builder()
                    .member(member)
                    .product(product)
                    .viewedAt(LocalDateTime.now())
                    .build();
            recentProductRepository.save(newRecentProduct);
        }

    }

    @Transactional
    public List<Product> getRecentProducts(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("멤버를 찾을 수 없습니다."));

        List<RecentProduct> recentProducts = recentProductRepository.findTop5ByMemberOrderByViewedAtDesc(member);

        return recentProducts.stream()
                .map(RecentProduct::getProduct)
                .collect(Collectors.toList());
    }
}
