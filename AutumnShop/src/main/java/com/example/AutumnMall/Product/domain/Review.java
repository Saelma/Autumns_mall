package com.example.AutumnMall.Product.domain;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.utils.audit.Auditable;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review extends Auditable {
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
    private String content;

    private int rating;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
