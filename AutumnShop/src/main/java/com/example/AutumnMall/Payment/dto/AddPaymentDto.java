package com.example.AutumnMall.Payment.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddPaymentDto {
    private Long cartId;
    private List<Integer> quantity;
    private Long orderId;
    private String impuid;
}
