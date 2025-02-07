package com.example.AutumnMall.Member.domain;

import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.utils.audit.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "favorites")
@Getter
@Setter
public class Favorites extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // 매핑된 product_id만 사용하기 위해 설정 
    @Column(name = "product_id", insertable = false, updatable = false)
    private Long productId;
}
