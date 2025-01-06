package com.example.AutumnMall.service;

import com.example.AutumnMall.domain.Member;
import com.example.AutumnMall.repository.CartRepository;
import com.example.AutumnMall.domain.Cart;
import com.example.AutumnMall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;

    public Cart addCart(Long memberId, String date) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        Optional<Cart> cart = cartRepository.findByMember(member);
        if(cart.isEmpty()) {
            Cart newCart = new Cart();
            newCart.setMember(member);
            newCart.setDate(date);
            Cart saveCart = cartRepository.save(newCart);
            return saveCart;
        } else {
            return cart.get();
        }
    }
    public Optional<Cart> findByMemberId(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return cartRepository.findByMember(member);
    }
}
