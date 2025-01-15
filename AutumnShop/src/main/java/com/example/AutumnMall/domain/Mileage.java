package com.example.AutumnMall.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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
    @JsonBackReference // 부모 엔티티로의 직렬화 방지
    private Member member;

    @Column(nullable = false)
    private int amount; // 마일리지 값

    @Column(nullable = false, length = 50)
    private String type; // 마일리지 타입 ( 적립, 사용, 소멸)

    @Column(nullable = false, length = 255)
    private String description; // 설명 ( 상품 구매 적립, 사용, 소멸)

    @CreationTimestamp
    private LocalDate date;


}
