package com.example.AutumnMall.Payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ResponsePaymentDto {
    private String impuid;
    private String name;
    private String status;
    private Long amount;
}
