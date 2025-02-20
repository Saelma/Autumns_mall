package com.example.AutumnMall.batch.writer;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.repository.CartItemJdbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OldCartItemWriter implements ItemWriter<CartItem> {

    private final CartItemJdbcRepository cartItemJdbcRepository;

    @Override
    public void write(List<? extends CartItem> items) {
        if (!items.isEmpty()) {
            cartItemJdbcRepository.deleteOldCartItems(30);
        }
    }
}
