package com.example.AutumnMall.repository;

import com.example.AutumnMall.domain.Member;
import com.example.AutumnMall.domain.Payment;
import com.example.AutumnMall.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByMember(Member member);

    Page<Payment> findByMemberAndDateBetween(Member member, LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Payment> findAllByMember(Member member, Pageable pageable);

    List<Payment> findByOrderId(Long orderId);

    boolean existsByMemberAndProductId(Member member, Long productId);

}
