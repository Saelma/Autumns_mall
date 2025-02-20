package com.example.AutumnMall.batch.reader;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.repository.CartItemJdbcRepository;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.List;

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
        return cartItemIterator.hasNext() ? cartItemIterator.next() : null;
    }
}