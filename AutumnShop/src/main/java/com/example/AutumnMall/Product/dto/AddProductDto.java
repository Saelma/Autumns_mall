package com.example.AutumnMall.Product.dto;

import lombok.Data;

@Data
public class AddProductDto {
    private String title;

    private Double price;

    private String description;

    private Long categoryId;

    private int count;
}
