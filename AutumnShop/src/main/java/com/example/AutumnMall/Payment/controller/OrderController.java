package com.example.AutumnMall.Payment.controller;

import com.example.AutumnMall.Payment.domain.Order;
import com.example.AutumnMall.Payment.dto.AddOrderDto;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.Payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> addOrder(@IfLogin @RequestBody AddOrderDto addOrderDto){
        return ResponseEntity.ok(orderService.addorder(addOrderDto.getMemberId()));
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrderById(@IfLogin LoginUserDto loginUserDto){
        List<Order> order = orderService.findByMemberId(loginUserDto.getMemberId());
        if(order != null){
            return ResponseEntity.ok(order);
        } else
            return null;
    }
}
