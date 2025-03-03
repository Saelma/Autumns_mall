package com.example.AutumnMall.Payment.service;

import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Payment.domain.Order;
import com.example.AutumnMall.Payment.domain.OrderStatus;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Payment.repository.OrderRepository;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

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
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", + memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });

            LocalDate localDate = LocalDate.now();
            localDate.getYear(); // 년
            localDate.getDayOfMonth(); // 달 마다 일 나누기
            localDate.getMonthValue(); // 이게 달 나누기

            Order order = Order.builder()
                    .memberId(member)
                    .orderDate(localDate)
                    .status(OrderStatus.ORDERED)
                    .build();

            Order savedOrder = orderRepository.save(order);
            log.info("회원 ID {}의 주문이 성공적으로 추가되었습니다. 주문 ID: {}", memberId, savedOrder.getId());  // 정보 로그

            return savedOrder;
        } catch (BusinessLogicException e) {
            log.error("주문 리스트 추가 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
        } catch (Exception e) {
            log.error("주문 리스트 추가 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    public Page<Order> findByMemberId(Long memberId, int page, int size) {
        try {
            log.info("회원 ID {}의 주문 내역 조회 시도 (페이지: {}, 크기: {})", memberId, page, size);

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", + memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });
            // 주문 ID로 주문 엔티티를 찾고, 결과를 반환합니다.
            // 결과가 없는 경우 null을 반환할 수 있으나, Optional을 사용하는 것이 더 나은 접근 방식일 수 있습니다.
            Pageable pageable = PageRequest.of(page, size);
            Page<Order> orders = orderRepository.findByMemberId(member, pageable);
            log.info("회원 ID {}의 주문 내역 조회 완료, 총 {}개의 주문", memberId, orders.getTotalElements());

            return orders;
        } catch (BusinessLogicException e) {
            log.error("주문 조회 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
        } catch (Exception e) {
            log.error("주문 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 필요에 따라 주문 생성, 주문 상태 업데이트, 주문 취소 등의 메서드를 추가할 수 있습니다.
}