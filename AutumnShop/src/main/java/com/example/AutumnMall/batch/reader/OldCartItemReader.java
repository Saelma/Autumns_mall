package com.example.AutumnMall.batch.reader;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.repository.CartItemJdbcRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class OldCartItemReader implements ItemReader<CartItem> {

    private final CartItemJdbcRepository cartItemJdbcRepository;
    private Iterator<CartItem> cartItemIterator;

    @Autowired
    public OldCartItemReader(CartItemJdbcRepository cartItemJdbcRepository) {
        this.cartItemJdbcRepository = cartItemJdbcRepository;
    }

    @PostConstruct
    public void init() {
        List<CartItem> oldCartItems = cartItemJdbcRepository.findCartItemsOlderThan(30);
        System.out.println("장바구니 불러오는 중: " + oldCartItems);  // 데이터 확인용 로그
        cartItemIterator = oldCartItems.iterator();
    }

    @Override
    public CartItem read() throws Exception {
        // Iterator가 없거나 더 이상 요소가 없을 때, 새롭게 데이터를 불러옴
        if (cartItemIterator == null || !cartItemIterator.hasNext()) {
            List<CartItem> oldCartItems = cartItemJdbcRepository.findCartItemsOlderThan(30);
            if (oldCartItems.isEmpty()) {
                log.info("삭제할 장바구니 아이템이 없습니다.");
                return null;
            }
            cartItemIterator = oldCartItems.iterator();
        }

        if (cartItemIterator.hasNext()) {
            CartItem cartItem = cartItemIterator.next();
            if (cartItem == null || cartItem.getQuantity() <= 0) {
                log.warn("잘못된 CartItem 발견: {}", cartItem);
                return null; // 예외를 던지지 않고 null 반환
            }
            return cartItem;
        }

        return cartItemIterator.hasNext() ? cartItemIterator.next() : null;
    }
}