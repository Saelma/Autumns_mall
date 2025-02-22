package com.example.AutumnMall.Cart.controller;

import com.example.AutumnMall.Cart.service.CartItemBatchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carts/batch")
@RequiredArgsConstructor
@Slf4j
public class CartItemBatchController {

    private final CartItemBatchService cartItemBatchService;

    @PostMapping("/deleteOldCartItems")
    public ResponseEntity<String> runCartItemBatch() {
        System.out.println(1);
        boolean success = cartItemBatchService.runCartItemBatch();
        if (success) {
            return ResponseEntity.ok("Batch job started successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to start batch job");
        }
    }
}