package com.example.AutumnMall.Member.repository;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Member.domain.RecentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentProductRepository extends JpaRepository<RecentProduct, Long> {
    Optional<RecentProduct> findByMemberAndProduct(Member member, Product product);
    List<RecentProduct> findByMember(Member member);

    // 멤버 ID를 기반으로 최근에 본 상품 조회
    List<RecentProduct> findTop5ByMemberOrderByViewedAtDesc(Member member);
}
