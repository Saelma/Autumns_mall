package com.example.AutumnMall.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="mileage")
@Builder
public class Mileage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private int amount; // 마일리지 값

    @Column(nullable = false, length = 50)
    private String type; // 마일리지 타입 ( 적립, 사용, 소멸)

    @Column(nullable = false, length = 255)
    private String description; // 설명 ( 상품 구매 적립, 사용, 소멸)

    @Column(nullable = false)
    private LocalDate date;


}
