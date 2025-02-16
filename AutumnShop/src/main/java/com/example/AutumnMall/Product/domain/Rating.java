package com.example.AutumnMall.Product.domain;

import javax.persistence.Embeddable;

import lombok.*;


@Embeddable
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    private Double rate;
    private Integer count;
}

