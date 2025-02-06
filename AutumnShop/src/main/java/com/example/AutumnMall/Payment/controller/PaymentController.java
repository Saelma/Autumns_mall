package com.example.AutumnMall.Payment.controller;

import com.example.AutumnMall.Payment.domain.Payment;
import com.example.AutumnMall.Payment.dto.AddPaymentDto;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.Payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;


    @PostMapping
    public List<Payment> payment(@IfLogin LoginUserDto loginUserDto,
                                 @RequestBody AddPaymentDto addPaymentDto) {
        try {
            return paymentService.addPayment(loginUserDto.getMemberId(),
                    addPaymentDto.getCartId(), addPaymentDto.getOrderId(), addPaymentDto.getQuantity());

        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @GetMapping
    public Page<Payment> paymentListGet(@IfLogin LoginUserDto loginUserDto,
                                        @RequestParam(required = false, defaultValue = "0") int page){
        int size = 10;
        return paymentService.getPaymentPage(loginUserDto.getMemberId(), page, size);

    }

    @GetMapping("/{year}/{month}")
    public Page<Payment> paymentListGet(@IfLogin LoginUserDto loginUserDto,
                                        @PathVariable(required = false) Integer year,
                                        @PathVariable(required = false) Integer month,
                                        @RequestParam(required = false, defaultValue = "0") int page) {
        int size = 10;
        return paymentService.getPaymentDate(loginUserDto.getMemberId(), year, month, page, size);
    }

    @GetMapping("/order")
    public List<Payment> getPaymentOrder(@RequestParam(required = false, defaultValue = "150") Long orderId){
        return paymentService.getOrderPayment(orderId);
    }

}
