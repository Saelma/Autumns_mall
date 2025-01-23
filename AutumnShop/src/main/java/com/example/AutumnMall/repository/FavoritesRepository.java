package com.example.AutumnMall.repository;

import com.example.AutumnMall.domain.Favorites;
import com.example.AutumnMall.domain.Member;
import com.example.AutumnMall.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {
    List<Favorites> findByMember(Member member);

    boolean existsByMemberAndProduct(Member member, Product product);

    void deleteByMemberAndProduct(Member member, Product product);
}
