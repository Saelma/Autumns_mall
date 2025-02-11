package com.example.AutumnMall.Cart.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGetCartItemDto {
    private Long id;
    private Long productId;
    private String title;
    private Double price;
    private String description;
    private String imageUrl;
    private int quantity;

}
