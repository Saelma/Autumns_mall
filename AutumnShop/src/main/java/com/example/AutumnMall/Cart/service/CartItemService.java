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
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import com.example.AutumnMall.utils.CustomBean.CustomBeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private final CartItemMapper cartItemMapper;

    @Autowired
    private final CustomBeanUtils customBeanUtils;

    @Transactional
    public CartItem addCartItem(AddCartItemDto addCartItemDto) {
        try {
            Cart cart = cartRepository.findById(addCartItemDto.getCartId())
                    .orElseThrow(() -> {
                        log.error("장바구니가 존재하지 않습니다. 장바구니Id: {}", addCartItemDto.getCartId());
                        return new BusinessLogicException(ExceptionCode.CART_NOT_FOUND);
                    });

            Product product = productRepository.findById(addCartItemDto.getProductId())
                    .orElseThrow(() -> {
                        log.error("물건이 존재하지 않습니다 물품Id: {}", addCartItemDto.getProductId());
                        return new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND);
                    });

            if (addCartItemDto.getQuantity() > product.getRating().getCount()) {
                throw new BusinessLogicException(ExceptionCode.INVALID_CARTITEM_STATUS);
            }

            CartItem cartItem = cartItemMapper.addCartItemDtoToCartItem(addCartItemDto);
            cartItem.setCart(cart);
            cartItem.setProduct(product);

            CartItem savedCartItem = cartItemRepository.save(cartItem);

            // 로그 추가: 아이템이 장바구니에 추가되었음을 기록
            log.info("장바구니에 아이템 추가됨: CartId={}, ProductId={}, Quantity={}",
                    cartItem.getCart().getId(), cartItem.getProduct().getId(), cartItem.getQuantity());

            return savedCartItem;
        }catch(BusinessLogicException e){
            log.error("장바구니 아이템 추가 실패 (비즈니스 로직 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.CARTITEM_NOT_FOUND);
        } catch (Exception e) {
            log.error("장바구니 아이템 추가 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public boolean isCartItemExist(Long memberId, Long cartId, Long productId) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });
            boolean check = cartItemRepository.existsByCart_memberAndCart_idAndProductId(member, cartId, productId);

            // 로그 추가: 장바구니 아이템 존재 여부 확인
            log.info("장바구니 아이템 존재 여부 확인: MemberId={}, CartId={}, ProductId={}, Exists={}",
                    memberId, cartId, productId, check);

            return check;
        }catch(BusinessLogicException e){
            log.error("장바구니 아이템 확인 실패 (비즈니스 로직 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.CARTITEM_NOT_FOUND);
        } catch (Exception e) {
            log.error("장바구니 아이템 확인 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public CartItem getCartItem(Long memberId, Long cartId, Long productId) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });

            // 로그 추가: 장바구니 아이템 조회
            log.info("장바구니 아이템 조회됨: MemberId={}, CartId={}, ProductId={}",
                    memberId, cartId, productId);

            return cartItemRepository.findByCart_memberAndCart_idAndProductId(member, cartId, productId).orElseThrow();
        }catch(BusinessLogicException e){
            log.error("장바구니 아이템 조회 실패 (비즈니스 로직 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.CARTITEM_NOT_FOUND);
        } catch (Exception e) {
            log.error("장바구니 아이템 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public CartItem updateCartItem(CartItem cartItem) {
        try {
            CartItem findCartItem = cartItemRepository.findById(cartItem.getId())
                    .orElseThrow(() -> {
                        log.error(
                                "장바구니 아이템을 찾을 수 없습니다. 카트Id : {}", cartItem.getCart().getId() + "카트 Item Id : " + cartItem.getId());
                        return new BusinessLogicException(ExceptionCode.CARTITEM_NOT_FOUND);
                    });

            if (findCartItem.getProduct().getRating().getCount() < cartItem.getQuantity() || cartItem.getQuantity() > 10) {
                throw new BusinessLogicException(ExceptionCode.INVALID_CARTITEM_STATUS);
            }

            findCartItem.setQuantity(cartItem.getQuantity());
            log.info("장바구니 아이템 수정됨: CartItemId={}, NewQuantity={}", cartItem.getId(), cartItem.getQuantity());
            return findCartItem;
        } catch (BusinessLogicException e) {
            log.error("장바구니 아이템 업데이트 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.CARTITEM_NOT_FOUND);
        } catch (Exception e) {
            log.error("장바구니 아이템 업데이트 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public boolean isCartItemExist(Long memberId, Long cartItemId) {
        try{
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("회원이 존재하지 않습니다. 회원Id: {}", memberId);
                    return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                });

        // 로그 추가: 장바구니 아이템 존재 여부 확인
        log.info("장바구니 아이템 존재 여부 확인: MemberId={}, CartItemId={}",
                memberId, cartItemId);

        return cartItemRepository.existsByCart_memberAndCartId(member, cartItemId);
        } catch (BusinessLogicException e) {
            log.error("장바구니 아이템 확인 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.CARTITEM_NOT_FOUND);
        } catch (Exception e) {
            log.error("장바구니 아이템 확인 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional(readOnly = true)
    public List<ResponseGetCartItemDto> getCartItems(Long memberId, Long cartId) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });
            List<CartItem> cartItems = cartItemRepository.findByCart_IdAndCart_Member(cartId, member);

            // 로그 추가: 장바구니 아이템 목록 조회
            log.info("회원Id={}의 장바구니 아이템 {}개 조회됨, CartId={}", memberId, cartItems.size(), cartId);

            return cartItems.stream().map(cartItem -> {
                Product product = cartItem.getProduct();

                ResponseGetCartItemDto responseDto = new ResponseGetCartItemDto();

                // CustomBeanUtils로 Product 속성 복사 ( product의 title, price, description, imageUrl )
                customBeanUtils.copyProperties(product, responseDto);

                // CartItem의 id와 quantity는 수동으로 설정
                responseDto.setProductId(product.getId());
                responseDto.setId(cartItem.getId());
                responseDto.setQuantity(cartItem.getQuantity());

                log.info("장바구니 아이템 정보: {}", responseDto);

                return responseDto;
            }).collect(Collectors.toList());
        } catch (BusinessLogicException e) {
            log.error("장바구니 아이템 조회 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.CARTITEM_NOT_FOUND);
        } catch (Exception e) {
            log.error("장바구니 아이템 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional(readOnly = true)
    public List<ResponseGetCartItemDto> getCartItems(Long memberId) {
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });
            List<CartItem> cartItems = cartItemRepository.findByCart_Member(member);

            // 로그 추가: 장바구니 아이템 목록 조회
            log.info("회원Id={}의 장바구니 아이템 {}개 조회됨", memberId, cartItems.size());

            return cartItems.stream().map(cartItem -> {
                Product product = cartItem.getProduct();
                ResponseGetCartItemDto responseDto = new ResponseGetCartItemDto();

                // CustomBeanUtils로 필드 복사 ( // CustomBeanUtils로 Product 속성 복사 ( product의 id, title, price, description, imageUrl )
                customBeanUtils.copyProperties(product, responseDto);

                // CartItem의 id와 quantity는 수동으로 설정
                responseDto.setProductId(cartItem.getProduct().getId());
                responseDto.setId(cartItem.getId());
                responseDto.setQuantity(cartItem.getQuantity());

                return responseDto;
            }).collect(Collectors.toList());
        } catch (BusinessLogicException e) {
            log.error("장바구니 아이템 조회 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.CARTITEM_NOT_FOUND);
        } catch (Exception e) {
            log.error("장바구니 아이템 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
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
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
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
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }


    /// 배치 처리
    @Transactional
    public void addItemsToCart(List<AddCartItemDto> addCartItemDtos) {
        // 상품 ID와 수량 추출
        List<Long> productIds = addCartItemDtos.stream()
                .map(AddCartItemDto::getProductId)
                .collect(Collectors.toList());
        List<Integer> quantities = addCartItemDtos.stream()
                .map(AddCartItemDto::getQuantity)
                .collect(Collectors.toList());

        List<Long> cartIds = addCartItemDtos.stream()
                .map(AddCartItemDto::getCartId)
                .collect(Collectors.toList());

        // 벌크 업데이트 호출
        cartItemRepository.bulkUpdateCartItemQuantities(cartIds, productIds, quantities);
    }

}
