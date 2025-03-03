package com.example.AutumnMall.Payment.controller;

import com.example.AutumnMall.Payment.domain.Order;
import com.example.AutumnMall.Payment.dto.AddOrderDto;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.Payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ResponseEntity<Page<Order>> getOrderById(@IfLogin LoginUserDto loginUserDto,
            @RequestParam(defaultValue = "0" ) int page,
            @RequestParam(defaultValue = "10") int size){
        Page<Order> orders = orderService.findByMemberId(loginUserDto.getMemberId(), page, size);
        if(orders != null){
            return ResponseEntity.ok(orders);
        } else
            return null;
    }
}
