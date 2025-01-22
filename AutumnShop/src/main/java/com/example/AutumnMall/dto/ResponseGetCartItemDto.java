package com.example.AutumnMall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseGetCartItemDto {
    private Long id;
    private Long productId;
    private String productTitle;
    private Double productPrice;
    private String productDescription;
    private String imageUrl;
    private int quantity;

    public ResponseGetCartItemDto(Long id, Long productId, String productTitle, double productPrice, String productDescription, int quantity, String imageUrl){
        this.id = id;
        this.productId = productId;
        this.productTitle = productTitle;
        this.productPrice = productPrice;
        this.productDescription = productDescription;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

}
