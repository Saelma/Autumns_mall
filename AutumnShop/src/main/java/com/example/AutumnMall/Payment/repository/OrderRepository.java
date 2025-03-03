package com.example.AutumnMall.Payment.repository;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Payment.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByMemberId(Member memberId, Pageable pageable); // 사용자 ID를 기준으로 주문 목록을 찾는 메소드

}
