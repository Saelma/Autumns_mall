package com.example.AutumnMall.Cart.repository;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    boolean existsByCart_memberAndCart_idAndProductId(Member memberId, Long cartId, Long productId);
    Optional<CartItem> findByCart_memberAndCart_idAndProductId(Member memberId, Long cartId, Long productId);

    boolean existsByCart_memberAndCartId(Member memberId, Long cartItemId);
    Integer deleteByCart_member(Member memberId);


    List<CartItem> findByCart_memberAndCart_id(Member memberId, Long cartId);

    List<CartItem> findByCart_Member(Member memberId);

    List<CartItem> findByCart_IdAndCart_Member(Long cartId, Member memberId);

    List<CartItem> deleteByCartId(Long cartItemId);

    List<CartItem> deleteByCartIdAndId(Long cartItemId, Long Id);
}
