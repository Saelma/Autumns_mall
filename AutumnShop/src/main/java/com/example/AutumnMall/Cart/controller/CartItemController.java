package com.example.AutumnMall.Cart.controller;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.dto.AddCartItemDto;
import com.example.AutumnMall.Cart.dto.ResponseGetCartItemDto;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.Cart.service.CartItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public ResponseEntity<CartItem> addCartItem(@IfLogin LoginUserDto loginUserDto, @RequestBody AddCartItemDto addCartItemDto){
        //같은 cart에 같은 product가 있으면 quantity를 더해줘야함
        if(cartItemService.isCartItemExist(loginUserDto.getMemberId(), addCartItemDto.getCartId(), addCartItemDto.getProductId())){
            CartItem cartItem = cartItemService.getCartItem(loginUserDto.getMemberId(), addCartItemDto.getCartId(), addCartItemDto.getProductId());
            cartItem.setQuantity(cartItem.getQuantity() + addCartItemDto.getQuantity());
            return ResponseEntity.ok(cartItemService.updateCartItem(cartItem));
        }

        return ResponseEntity.ok(cartItemService.addCartItem(addCartItemDto));
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
