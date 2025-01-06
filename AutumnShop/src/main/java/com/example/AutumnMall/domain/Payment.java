package com.example.AutumnMall.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
@Setter
@Getter
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;
    private Long productId;
    private Double productPrice;
    private String productTitle;
    private Double productRate;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;



}
