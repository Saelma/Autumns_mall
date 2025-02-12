package com.example.AutumnMall.Cart.controller;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.dto.AddCartItemDto;
import com.example.AutumnMall.Cart.dto.ResponseGetCartItemDto;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.Cart.service.CartItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/cartItems")
@RestController
@RequiredArgsConstructor
public class CartItemController {
    private final CartItemService cartItemService;

    @PostMapping
    public CartItem addCartItem(@IfLogin LoginUserDto loginUserDto, @RequestBody AddCartItemDto addCartItemDto){
        //같은 cart에 같은 product가 있으면 quantity를 더해줘야함
        if(cartItemService.isCartItemExist(loginUserDto.getMemberId(), addCartItemDto.getCartId(), addCartItemDto.getProductId())){
            CartItem cartItem = cartItemService.getCartItem(loginUserDto.getMemberId(), addCartItemDto.getCartId(), addCartItemDto.getProductId());
            cartItem.setQuantity(cartItem.getQuantity() + addCartItemDto.getQuantity());
            return cartItemService.updateCartItem(cartItem);
        }

        return cartItemService.addCartItem(addCartItemDto);
    }

    @GetMapping
    public List<ResponseGetCartItemDto> getCartItems(@IfLogin LoginUserDto loginUserDto, @RequestParam(required = false) Long cartId) {
        if(cartId == null)
            return cartItemService.getCartItems(loginUserDto.getMemberId());
        return cartItemService.getCartItems(loginUserDto.getMemberId(), cartId);
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity deleteCartItem(@IfLogin LoginUserDto loginUserDto, @PathVariable Long cartItemId,
        @RequestParam(required = false) Long id){
        if(!cartItemService.isCartItemExist(loginUserDto.getMemberId(), cartItemId))
            return ResponseEntity.badRequest().build();
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
