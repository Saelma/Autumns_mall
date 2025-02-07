package com.example.AutumnMall.Payment.domain;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.utils.audit.Auditable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
@Setter
@Getter
public class Payment extends Auditable {

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
