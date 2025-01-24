package com.example.AutumnMall.controller;

import com.example.AutumnMall.domain.Product;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.service.FavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoritesController {
    private final FavoritesService favoritesService;

    @PostMapping("/{productId}")
    public ResponseEntity<Void> addFavorites(@IfLogin LoginUserDto loginUserDto, @PathVariable Long productId){
        favoritesService.addFavorites(loginUserDto.getMemberId(), productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorites(@IfLogin LoginUserDto loginUserDto, @PathVariable Long productId){
        favoritesService.removeFavorites(loginUserDto.getMemberId(), productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<Product>> getFavorites(@IfLogin LoginUserDto loginUserDto){
        if(loginUserDto.getMemberId() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Product> favoriteProducts = favoritesService.getFavoritesProductIdByMember(loginUserDto.getMemberId());

        return ResponseEntity.ok(favoriteProducts);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Boolean> checkIfFavorite(@IfLogin LoginUserDto loginUserDto, @PathVariable Long productId){
        boolean isFavorite = favoritesService.isProductInFavorites(loginUserDto.getMemberId(), productId);
        return ResponseEntity.ok(isFavorite);
    }
}
