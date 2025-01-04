package com.example.AutumnMall.dto;

import lombok.Data;

@Data
public class AddCartItemDto {
    private Long cartId;
    private Long productId;
    private int quantity;
}
