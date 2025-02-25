package com.example.AutumnMall.Payment.domain;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.utils.audit.Auditable;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "payment")
@Setter
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="impuid")
    private String impuid;
    private String status;

    private String imageUrl;
    private Long productId;
    private Double price;
    private String title;
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
