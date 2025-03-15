package com.example.AutumnMall.Product.repository;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Product.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductOrderByCreatedAtDesc(Product product);

    Optional<Review> findByMemberAndProduct(Member member, Product product);
}
