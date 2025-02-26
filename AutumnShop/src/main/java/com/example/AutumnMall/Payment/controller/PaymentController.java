package com.example.AutumnMall.Payment.controller;

import com.example.AutumnMall.Payment.domain.Payment;
import com.example.AutumnMall.Payment.dto.AddPaymentDto;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import com.example.AutumnMall.security.jwt.util.IfLogin;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.example.AutumnMall.Payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;


    @PostMapping
    public ResponseEntity<List<Payment>> payment(@IfLogin LoginUserDto loginUserDto,
                                                @RequestBody AddPaymentDto addPaymentDto) {
        try {
            return ResponseEntity.ok(paymentService.addPayment(loginUserDto.getMemberId(),
                    addPaymentDto.getCartId(), addPaymentDto.getOrderId(), addPaymentDto.getQuantity(), addPaymentDto.getImpuid()));

        }catch(BusinessLogicException ex){
            throw new BusinessLogicException(ex.getExceptionCode());
        }
    }

    @GetMapping
    public ResponseEntity<Page<Payment>> paymentListGet(@IfLogin LoginUserDto loginUserDto,
                                        @RequestParam(required = false, defaultValue = "0") int page){
        int size = 10;
        return ResponseEntity.ok(paymentService.getPaymentPage(loginUserDto.getMemberId(), page, size));

    }

    @GetMapping("/{year}/{month}")
    public ResponseEntity<Page<Payment>> paymentListGet(@IfLogin LoginUserDto loginUserDto,
                                        @PathVariable(required = false) Integer year,
                                        @PathVariable(required = false) Integer month,
                                        @RequestParam(required = false, defaultValue = "0") int page) {
        int size = 10;
        return ResponseEntity.ok(paymentService.getPaymentDate(loginUserDto.getMemberId(), year, month, page, size));
    }

    @GetMapping("/order")
    public ResponseEntity<List<Payment>> getPaymentOrder(@RequestParam(required = false, defaultValue = "150") Long orderId){
        return ResponseEntity.ok(paymentService.getOrderPayment(orderId));
    }

}
