package com.example.AutumnMall.repository;

import com.example.AutumnMall.domain.Cart;
import com.example.AutumnMall.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository  extends JpaRepository<Cart, Long> {
    Optional<Cart> findByMemberAndDate(Member member, String date);

    Optional<Cart> findByMember(Member member);
}
