package com.example.AutumnMall.Payment.repository;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Payment.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findOrderIdByMemberId(Member memberId); // 사용자 ID를 기준으로 주문 목록을 찾는 메소드

}
