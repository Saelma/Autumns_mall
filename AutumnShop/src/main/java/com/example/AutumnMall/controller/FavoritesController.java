package com.example.AutumnMall.controller;

import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.service.FavoritesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getFavorites(@IfLogin LoginUserDto loginUserDto){
        return ResponseEntity.ok(favoritesService.getFavoritesProductIdByMember(loginUserDto.getMemberId()));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Boolean> checkIfFavorite(@IfLogin LoginUserDto loginUserDto, @PathVariable Long productId){
        boolean isFavorite = favoritesService.isProductInFavorites(loginUserDto.getMemberId(), productId);
        return ResponseEntity.ok(isFavorite);
    }
}
