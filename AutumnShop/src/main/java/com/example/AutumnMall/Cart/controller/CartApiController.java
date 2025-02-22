package com.example.AutumnMall.Cart.controller;

import com.example.AutumnMall.Cart.domain.Cart;
import com.example.AutumnMall.Cart.dto.AddCartDto;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.Cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/carts") // http://localhost:8080/carts
@RequiredArgsConstructor
public class CartApiController {
    private final CartService cartService;
    @PostMapping
    public ResponseEntity<Cart> addCart(@IfLogin @RequestBody AddCartDto addCartDto) {
        Cart cart = cartService.addCart(addCartDto);
        return ResponseEntity.ok(cart);
    }
    @GetMapping("/{memberId}") // http://localhost:8080/carts/{memberId}
    public ResponseEntity<Optional<Cart>> getCartById(@IfLogin LoginUserDto loginUserDto, @PathVariable Long memberId) {
        return ResponseEntity.ok(cartService.findByMemberId(memberId));
    }
}
