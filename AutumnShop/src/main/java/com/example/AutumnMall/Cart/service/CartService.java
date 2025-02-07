package com.example.AutumnMall.Cart.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Cart.repository.CartRepository;
import com.example.AutumnMall.Cart.domain.Cart;
import com.example.AutumnMall.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;

    public Cart addCart(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        Optional<Cart> cart = cartRepository.findByMember(member);

        LocalDateTime now = LocalDateTime.now();  // 현재 시간 가져오기
        String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));  // 형식에 맞게 변환

        if(cart.isEmpty()) {
            Cart newCart = new Cart();
            newCart.setMember(member);
            newCart.setDate(formattedDate);

            Cart saveCart = cartRepository.save(newCart);
            return saveCart;
        } else {
            Cart existingCart = cart.get();
            existingCart.setDate(formattedDate);
            return cartRepository.save(existingCart);
        }
    }
    public Optional<Cart> findByMemberId(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return cartRepository.findByMember(member);
    }
}
