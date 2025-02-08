package com.example.AutumnMall.Cart.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Cart.repository.CartRepository;
import com.example.AutumnMall.Cart.domain.Cart;
import com.example.AutumnMall.Member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Cart addCart(Long memberId) {
        try{
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다. 회원Id: " + memberId));
            Optional<Cart> cart = cartRepository.findByMember(member);

            LocalDateTime now = LocalDateTime.now();  // 현재 시간 가져오기
            String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));  // 형식에 맞게 변환

            if(cart.isEmpty()) {
                Cart newCart = new Cart();
                newCart.setMember(member);
                newCart.setDate(formattedDate);

                Cart saveCart = cartRepository.save(newCart);
                log.info("해당 멤버가 카트를 생성했습니다 : {} with 카트Id : {}", memberId, saveCart.getId());

                return saveCart;
            } else {
                Cart existingCart = cart.get();
                existingCart.setDate(formattedDate);
                log.info("해당 멤버가 카트를 업데이트했습니다 : {} with 카트Id : {}", memberId, existingCart.getId());

                return cartRepository.save(existingCart);
            }
        }catch(RuntimeException e){
            log.error("카트 생성 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public Optional<Cart> findByMemberId(Long memberId) {
        try{

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다. 회원Id: " + memberId));
        return cartRepository.findByMember(member);
        }catch(RuntimeException e){
            log.error("카트 조회 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }
}
