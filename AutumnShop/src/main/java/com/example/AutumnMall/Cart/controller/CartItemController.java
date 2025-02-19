package com.example.AutumnMall.Cart.controller;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.dto.AddCartItemDto;
import com.example.AutumnMall.Cart.dto.ResponseGetCartItemDto;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.Cart.service.CartItemService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/cartItems")
@RestController
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping
    public ResponseEntity<Void> addCartItem(@IfLogin LoginUserDto loginUserDto,
                                            @RequestBody List<AddCartItemDto> requestBody,
                                            @RequestHeader HttpHeaders headers) {
        boolean isBatchRequest = "true".equals(headers.getFirst("Batch-Request"));

        if (!isBatchRequest) {
            // 단일 요청일 경우: requestBody를 for loop로 처리
            for (AddCartItemDto addCartItemDto : requestBody) {
                if (cartItemService.isCartItemExist(loginUserDto.getMemberId(), addCartItemDto.getCartId(), addCartItemDto.getProductId())) {
                    CartItem cartItem = cartItemService.getCartItem(loginUserDto.getMemberId(), addCartItemDto.getCartId(), addCartItemDto.getProductId());
                    cartItem.setQuantity(cartItem.getQuantity() + addCartItemDto.getQuantity());
                    cartItemService.updateCartItem(cartItem);
                } else {
                    cartItemService.addCartItem(addCartItemDto);
                }
            }
        } else {
            // 배치 요청일 경우: addItemsToCart 호출
            cartItemService.addItemsToCart(requestBody);
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<ResponseGetCartItemDto> getCartItems(@IfLogin LoginUserDto loginUserDto, @RequestParam(required = false) Long cartId) {
        if(cartId == null)
            return cartItemService.getCartItems(loginUserDto.getMemberId());
        return cartItemService.getCartItems(loginUserDto.getMemberId(), cartId);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@IfLogin LoginUserDto loginUserDto, @PathVariable Long cartItemId,
        @RequestParam(required = false) Long id){
        if(!cartItemService.isCartItemExist(loginUserDto.getMemberId(), cartItemId)) {
            log.error("회원 {} 의 상품 ID {}를 찾을 수 없습니다.", loginUserDto.getMemberId(), cartItemId);  // 오류 로그
            throw new BusinessLogicException(ExceptionCode.CARTITEM_NOT_FOUND);
        }
        else {
            if (id == null)
                cartItemService.deleteCartItem(cartItemId);
            else {
                cartItemService.deleteCartItem(cartItemId, id);
            }
            return ResponseEntity.ok().build();
        }
    }

}
