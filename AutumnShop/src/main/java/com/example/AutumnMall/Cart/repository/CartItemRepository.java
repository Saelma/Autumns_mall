package com.example.AutumnMall.Cart.repository;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

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

    // 벌크 업데이트: 동일한 상품이 존재하면 수량을 더함
    @Modifying
    @Transactional
    @Query("UPDATE CartItem c " +
            "SET c.quantity = c.quantity + :quantity " +
            "WHERE c.cart.id = :cartIds AND c.product.id = :productIds")
    void bulkUpdateCartItemQuantity(@Param("cartIds") List<Long> cartIds,
                                    @Param("productIds") List<Long> productIds,
                                    @Param("quantity") Integer quantity);

    // 여러 개 상품에 대한 수량 업데이트
    @Modifying
    @Transactional
    @Query("UPDATE CartItem c " +
            "SET c.quantity = c.quantity + " +
            "    CASE " +
            "        WHEN c.product.id IN :productIds THEN :quantities " +
            "        ELSE 0 " +
            "    END " +
            "WHERE c.cart.id = :cartIds AND c.product.id IN :productIds")
    void bulkUpdateCartItemQuantities(@Param("cartIds") List<Long> cartIds,
                                      @Param("productIds") List<Long> productIds,
                                      @Param("quantities") List<Integer> quantities);
}
