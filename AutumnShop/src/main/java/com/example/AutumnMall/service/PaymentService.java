package com.example.AutumnMall.service;

import com.example.AutumnMall.domain.*;
import com.example.AutumnMall.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;


    @Transactional
    public List<Payment> addPayment(Long memberId, Long cartId, Long orderId, List<Integer> quantities){
        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
            List<CartItem> cartItems = cartItemRepository.findByCart_IdAndCart_Member(cartId, member);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
            List<Payment> payments = new ArrayList<>();

            LocalDate localDate = LocalDate.now();
            localDate.getYear();
            localDate.getDayOfMonth();
            localDate.getMonthValue();

            Iterator<CartItem> iterator = cartItems.iterator();
            Iterator<Integer> quantityIterator = quantities.iterator();
            while (iterator.hasNext()) {
                CartItem cartItem = iterator.next();
                int quantity = quantityIterator.next();

                Product product = cartItem.getProduct();

                Payment userPayment = new Payment();
                userPayment.setImageUrl(product.getImageUrl());
                userPayment.setProductId(product.getId());
                userPayment.setProductPrice(product.getPrice());
                userPayment.setProductTitle(product.getTitle());
                userPayment.setProductRate(product.getRating().getRate());
                userPayment.setQuantity(quantity);
                userPayment.setMember(member);
                userPayment.setDate(localDate);
                userPayment.setOrder(order);

                payments.add(paymentRepository.save(userPayment));
            }
            cartItemRepository.deleteByCart_member(member);
            return payments;

        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Transactional
    public Page<Payment> getPaymentDate(Long memberId, int year, int month, int page, int size){
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));

        return paymentRepository.findByMemberAndDateBetween(member, startDate, endDate, PageRequest.of(page, size));
    }
    @Transactional
    public List<Payment> getPayment(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return paymentRepository.findByMember(member);
    }

    @Transactional
    public Page<Payment> getPaymentPage(Long memberId, int page, int size){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + memberId));
        return paymentRepository.findAllByMember(member, PageRequest.of(page, size));
    }

    @Transactional
    public List<Payment> getOrderPayment(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + orderId));
        return paymentRepository.findByOrderId(order.getId());
    }

}
