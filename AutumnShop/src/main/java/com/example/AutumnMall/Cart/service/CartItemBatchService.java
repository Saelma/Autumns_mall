package com.example.AutumnMall.Cart.service;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.repository.CartItemJdbcRepository;
import com.example.AutumnMall.Cart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemBatchService {
    private final CartItemJdbcRepository cartItemJdbcRepository;

    // 대량 삽입 메서드
    public void batchInsertCartItems(List<CartItem> cartItems) {
        cartItemJdbcRepository.batchInsertCartItems(cartItems);
    }

    // 오래된 장바구니 아이템 삭제 (예: 30일 이상된 항목 삭제)
    public void deleteOldCartItems(int days) {
        cartItemJdbcRepository.deleteOldCartItems(days);
    }
}
