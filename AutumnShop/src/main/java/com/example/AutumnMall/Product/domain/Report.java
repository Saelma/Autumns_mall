package com.example.AutumnMall.Product.domain;

import com.example.AutumnMall.Member.domain.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 255, nullable = false)
    private String reason;

    @Column(length = 255, nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean seen = false;
}
