package com.example.AutumnMall.Cart.service;

import com.example.AutumnMall.Cart.dto.AddCartDto;
import com.example.AutumnMall.Cart.mapper.CartMapper;
import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Cart.repository.CartRepository;
import com.example.AutumnMall.Cart.domain.Cart;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final CartMapper cartMapper;

    @Transactional
    public Cart addCart(AddCartDto addCartDto) {
        try{
            Member member = memberRepository.findById(addCartDto.getMemberId())
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: " + addCartDto.getMemberId());
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });
            Optional<Cart> cart = cartRepository.findByMember(member);

            LocalDateTime now = LocalDateTime.now();  // 현재 시간 가져오기
            String formattedDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss"));  // 형식에 맞게 변환

            if(cart.isEmpty()) {
                Cart newCart = cartMapper.addCartDtoToCart(addCartDto);
                newCart.setMember(member);
                newCart.setDate(formattedDate);

                Cart saveCart = cartRepository.save(newCart);
                log.info("해당 멤버가 카트를 생성했습니다 : {} with 카트Id : {}", addCartDto.getMemberId(), saveCart.getId());

                return saveCart;
            } else {
                Cart existingCart = cart.get();
                existingCart.setDate(formattedDate);
                log.info("해당 멤버가 카트를 업데이트했습니다 : {} with 카트Id : {}", addCartDto.getMemberId(), existingCart.getId());

                return cartRepository.save(existingCart);
            }
        } catch (BusinessLogicException e) {
            log.error("카트 생성 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.CART_NOT_FOUND);
        } catch (Exception e) {
            log.error("카트 생성 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Cart> findByMemberId(Long memberId) {
        try{

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("회원이 존재하지 않습니다. 회원Id: {}", memberId);
                    return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                });
        return cartRepository.findByMember(member);
        } catch (BusinessLogicException e) {
            log.error("카트 조회 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.CART_NOT_FOUND);
        } catch (Exception e) {
            log.error("카트 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }
}
