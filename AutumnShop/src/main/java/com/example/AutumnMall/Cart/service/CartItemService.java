package com.example.AutumnMall.Cart.service;

import com.example.AutumnMall.Cart.mapper.CartItemMapper;
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

    private final CartItemMapper cartItemMapper;


    @Transactional
    public CartItem addCartItem(AddCartItemDto addCartItemDto) {
        try {
            Cart cart = cartRepository.findById(addCartItemDto.getCartId()).orElseThrow();
            Product product = productRepository.findById(addCartItemDto.getProductId()).orElseThrow();

            CartItem cartItem = cartItemMapper.addCartItemDtoToCartItem(addCartItemDto);
            cartItem.setCart(cart);
            cartItem.setProduct(product);

            CartItem savedCartItem = cartItemRepository.save(cartItem);

            // 로그 추가: 아이템이 장바구니에 추가되었음을 기록
            log.info("장바구니에 아이템 추가됨: CartId={}, ProductId={}, Quantity={}",
                    cartItem.getCart().getId(), cartItem.getProduct().getId(), cartItem.getQuantity());

            return savedCartItem;
        }catch(RuntimeException e){
            log.error("장바구니 아이템 추가 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public boolean isCartItemExist(Long memberId, Long cartId, Long productId) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다. 회원Id: " + memberId));
            boolean check = cartItemRepository.existsByCart_memberAndCart_idAndProductId(member, cartId, productId);

            // 로그 추가: 장바구니 아이템 존재 여부 확인
            log.info("장바구니 아이템 존재 여부 확인: MemberId={}, CartId={}, ProductId={}, Exists={}",
                    memberId, cartId, productId, check);

            return check;
        }catch(RuntimeException e){
            log.error("장바구니 아이템 확인 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public CartItem getCartItem(Long memberId, Long cartId, Long productId) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다. 회원Id: " + memberId));

            // 로그 추가: 장바구니 아이템 조회
            log.info("장바구니 아이템 조회됨: MemberId={}, CartId={}, ProductId={}",
                    memberId, cartId, productId);

            return cartItemRepository.findByCart_memberAndCart_idAndProductId(member, cartId, productId).orElseThrow();
        }catch(RuntimeException e){
            log.error("장바구니 아이템 조회 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public CartItem updateCartItem(CartItem cartItem) {
        try {
            CartItem findCartItem = cartItemRepository.findById(cartItem.getId())
                    .orElseThrow(() -> new RuntimeException(
                            "장바구니 아이템을 찾을 수 없습니다. 카트Id :" + cartItem.getCart().getId() + "카트 Item Id : " + cartItem.getId()));
            findCartItem.setQuantity(cartItem.getQuantity());

            // 로그 추가: 장바구니 아이템이 업데이트되었음을 기록
            log.info("장바구니 아이템 수정됨: CartItemId={}, NewQuantity={}", cartItem.getId(), cartItem.getQuantity());

            return findCartItem;
        }catch(RuntimeException e){
            log.error("장바구니 아이템 업데이트 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public boolean isCartItemExist(Long memberId, Long cartItemId) {
        try{
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다. 회원Id: " + memberId));

        // 로그 추가: 장바구니 아이템 존재 여부 확인
        log.info("장바구니 아이템 존재 여부 확인: MemberId={}, CartItemId={}",
                memberId, cartItemId);

        return cartItemRepository.existsByCart_memberAndCartId(member, cartItemId);
        }catch(RuntimeException e){
            log.error("장바구니 아이템 확인 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<ResponseGetCartItemDto> getCartItems(Long memberId, Long cartId) {
        try{
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다. 회원Id: " + memberId));
        List<CartItem> cartItems = cartItemRepository.findByCart_IdAndCart_Member(cartId, member);

        // 로그 추가: 장바구니 아이템 목록 조회
        log.info("회원Id={}의 장바구니 아이템 {}개 조회됨, CartId={}", memberId, cartItems.size(), cartId);

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
        }catch(RuntimeException e){
            log.error("장바구니 아이템 조회 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public List<ResponseGetCartItemDto> getCartItems(Long memberId) {
        try{
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다. 회원Id: " + memberId));
        List<CartItem> cartItems = cartItemRepository.findByCart_Member(member);

        // 로그 추가: 장바구니 아이템 목록 조회
        log.info("회원Id={}의 장바구니 아이템 {}개 조회됨", memberId, cartItems.size());

        return cartItems.stream().map(cartItem -> new ResponseGetCartItemDto(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getTitle(),
                cartItem.getProduct().getPrice(),
                cartItem.getProduct().getDescription(),
                cartItem.getQuantity(),
                cartItem.getProduct().getImageUrl()
        )).collect(Collectors.toList());
        }catch(RuntimeException e){
            log.error("장바구니 아이템 조회 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void deleteCartItem(Long cartItemId) {
        try {
            cartItemRepository.deleteByCartId(cartItemId);

            // 로그 추가: 카트의 모든 아이템 삭제
            log.info("회원의 모든 카트 아이템이 삭제됨: CartItemId={}", cartItemId);
        }catch(Exception e){
            log.error("장바구니 모든 아이템 삭제 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public void deleteCartItem(Long cartItemId, Long Id){
        try{
        cartItemRepository.deleteByCartIdAndId(cartItemId, Id);

        // 로그 추가: 특정 장바구니 아이템이 삭제되었음을 기록
        log.info("회원의 특정 카트 아이템이 삭제됨: CartItemId={}, CartId={}", Id, cartItemId);
        }catch(Exception e){
            log.error("장바구니 아이템 삭제 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

}
