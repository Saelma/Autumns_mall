package com.example.AutumnMall.Cart.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Cart.dto.ResponseGetCartItemDto;
import com.example.AutumnMall.Cart.repository.CartItemRepository;
import com.example.AutumnMall.Cart.repository.CartRepository;
import com.example.AutumnMall.Cart.domain.Cart;
import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.dto.AddCartItemDto;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public CartItem addCartItem(AddCartItemDto addCartItemDto) {
        Cart cart = cartRepository.findById(addCartItemDto.getCartId()).orElseThrow();
        Product product = productRepository.findById(addCartItemDto.getProductId()).orElseThrow();

        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setQuantity(addCartItemDto.getQuantity());
        cartItem.setProduct(product);

        CartItem savedCartItem = cartItemRepository.save(cartItem);

        // 로그 추가: 아이템이 장바구니에 추가되었음을 기록
        log.info("CartItem added: CartId={}, ProductId={}, Quantity={}",
                cartItem.getCart().getId(), cartItem.getProduct().getId(), cartItem.getQuantity());

        return savedCartItem;
    }

    @Transactional(readOnly = true)
    public boolean isCartItemExist(Long memberId, Long cartId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        boolean check = cartItemRepository.existsByCart_memberAndCart_idAndProductId(member, cartId, productId);
        return check;
    }

    @Transactional(readOnly = true)
    public CartItem getCartItem(Long memberId, Long cartId, Long productId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return cartItemRepository.findByCart_memberAndCart_idAndProductId(member, cartId, productId).orElseThrow();
    }

    @Transactional
    public CartItem updateCartItem(CartItem cartItem) {
        CartItem findCartItem = cartItemRepository.findById(cartItem.getId()).orElseThrow();
        findCartItem.setQuantity(cartItem.getQuantity());

        // 로그 추가: 장바구니 아이템이 업데이트되었음을 기록
        log.info("CartItem updated: CartItemId={}, NewQuantity={}", cartItem.getId(), cartItem.getQuantity());

        return findCartItem;
    }

    @Transactional(readOnly = true)
    public boolean isCartItemExist(Long memberId, Long cartItemId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return cartItemRepository.existsByCart_memberAndCartId(member, cartItemId);
    }

    @Transactional(readOnly = true)
    public List<ResponseGetCartItemDto> getCartItems(Long memberId, Long cartId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        List<CartItem> cartItems = cartItemRepository.findByCart_IdAndCart_Member(cartId, member);

        return cartItems.stream()
                .map(cartItem -> {
                    Product product = cartItem.getProduct();

                    return new ResponseGetCartItemDto(
                            cartItem.getId(),
                            product.getId(),
                            product.getTitle(),
                            product.getPrice(),
                            product.getDescription(),
                            cartItem.getQuantity(),
                            product.getImageUrl()
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ResponseGetCartItemDto> getCartItems(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        List<CartItem> cartItems = cartItemRepository.findByCart_Member(member);

        return cartItems.stream().map(cartItem -> new ResponseGetCartItemDto(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getTitle(),
                cartItem.getProduct().getPrice(),
                cartItem.getProduct().getDescription(),
                cartItem.getQuantity(),
                cartItem.getProduct().getImageUrl()
        )).collect(Collectors.toList());
    }

    @Transactional
    public void deleteCartItem(Long cartItemId) {
        cartItemRepository.deleteByCartId(cartItemId);

        // 로그 추가: 특정 장바구니 아이템이 삭제되었음을 기록
        log.info("CartItem deleted: CartItemId={}", cartItemId);
    }

    @Transactional
    public void deleteCartItem(Long cartItemId, Long Id){
        cartItemRepository.deleteByCartIdAndId(cartItemId, Id);

        // 로그 추가: 특정 장바구니 아이템이 삭제되었음을 기록
        log.info("CartItem deleted: CartItemId={}, CartId={}", Id, cartItemId);
    }

}
