package com.example.AutumnMall.Payment.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Payment.domain.Order;
import com.example.AutumnMall.Payment.domain.OrderStatus;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Payment.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    public Order addorder(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        LocalDate localDate = LocalDate.now();
        localDate.getYear(); // 년
        localDate.getDayOfMonth(); // 달 마다 일 나누기
        localDate.getMonthValue(); // 이게 달 나누기

        Order order = new Order();
        order.setMemberId(member);
        order.setOrderDate(localDate);
        order.setStatus(OrderStatus.ORDERED);

        return orderRepository.save(order);
    }

    public List<Order> findByMemberId(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        // 주문 ID로 주문 엔티티를 찾고, 결과를 반환합니다.
        // 결과가 없는 경우 null을 반환할 수 있으나, Optional을 사용하는 것이 더 나은 접근 방식일 수 있습니다.
        return orderRepository.findOrderIdByMemberId(member);
    }

    // 필요에 따라 주문 생성, 주문 상태 업데이트, 주문 취소 등의 메서드를 추가할 수 있습니다.
}