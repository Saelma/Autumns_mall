package com.example.AutumnMall.Payment.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Payment.domain.Order;
import com.example.AutumnMall.Payment.domain.OrderStatus;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Payment.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    public Order addorder(Long memberId) {
        try {
            log.info("회원 ID {}에 대한 주문 추가 시도", memberId);  // 로그 남기기

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원 ID {}를 찾을 수 없습니다.", memberId);  // 오류 로그
                        return new RuntimeException("Member not found with id: " + memberId);
                    });

            LocalDate localDate = LocalDate.now();
            localDate.getYear(); // 년
            localDate.getDayOfMonth(); // 달 마다 일 나누기
            localDate.getMonthValue(); // 이게 달 나누기

            Order order = new Order();
            order.setMemberId(member);
            order.setOrderDate(localDate);
            order.setStatus(OrderStatus.ORDERED);

            Order savedOrder = orderRepository.save(order);
            log.info("회원 ID {}의 주문이 성공적으로 추가되었습니다. 주문 ID: {}", memberId, savedOrder.getId());  // 정보 로그

            return savedOrder;
        }catch(RuntimeException e){
            log.error("주문 리스트 추가 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    public List<Order> findByMemberId(Long memberId) {
        try {
            log.info("회원 ID {}의 주문 내역 조회 시도", memberId);

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원 ID {}를 찾을 수 없습니다.", memberId);  // 오류 로그
                        return new RuntimeException("Member not found with id: " + memberId);
                    });
            // 주문 ID로 주문 엔티티를 찾고, 결과를 반환합니다.
            // 결과가 없는 경우 null을 반환할 수 있으나, Optional을 사용하는 것이 더 나은 접근 방식일 수 있습니다.
            List<Order> orders = orderRepository.findOrderIdByMemberId(member);
            log.info("회원 ID {}의 주문 내역 조회 완료, 총 {}개의 주문", memberId, orders.size());  // 정보 로그

            return orders;
        }catch(RuntimeException e){
            log.error("주문 조회 실패 : {}", e.getMessage(), e);
            throw e;
        }
    }

    // 필요에 따라 주문 생성, 주문 상태 업데이트, 주문 취소 등의 메서드를 추가할 수 있습니다.
}