package com.example.AutumnMall.Cart.repository;

import com.example.AutumnMall.Cart.domain.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CartItemJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    /**
     * 장바구니 상품을 대량 추가
     */
    public void batchInsertCartItems(List<CartItem> cartItems) {
        String sql = "INSERT INTO cart_items (member_id, product_id, quantity, created_at) VALUES (?, ?, ?, ?)";

        List<Object[]> batchArgs = cartItems.stream()
                .map(item -> new Object[]{item.getCart().getMember().getMemberId(), item.getProduct().getId(), item.getQuantity(), item.getCreatedAt()})
                .collect(Collectors.toList());

        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    /**
     * 일정 기간이 지난 장바구니 아이템 삭제
     */
    public void deleteOldCartItems(int days) {
        String sql = "DELETE FROM cart_items WHERE created_at < NOW() - INTERVAL ? DAY";
        jdbcTemplate.update(sql, days);
    }
}
