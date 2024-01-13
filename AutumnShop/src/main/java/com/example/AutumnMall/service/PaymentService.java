package com.example.AutumnMall.service;

import com.example.AutumnMall.domain.*;
import com.example.AutumnMall.repository.CartItemRepository;
import com.example.AutumnMall.repository.PaymentRepository;
import com.example.AutumnMall.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
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
    private final ProductRepository productRepository;

    @Transactional
    public List<Payment> addPayment(Long memberId, Long cartId, List<Integer> quantities){
        try {
            List<CartItem> cartItems = cartItemRepository.findByCart_IdAndCart_MemberId(cartId, memberId);
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

                Optional<Product> product = productRepository.findById(cartItem.getProductId());
                Product productItem = product.get();


                Payment userPayment = new Payment();
                userPayment.setImageUrl(productItem.getImageUrl());
                userPayment.setProductId(cartItem.getProductId());
                userPayment.setProductPrice(cartItem.getProductPrice());
                userPayment.setProductTitle(cartItem.getProductTitle());
                userPayment.setProductRate(productItem.getRating().getRate());
                userPayment.setQuantity(quantity);
                userPayment.setMemberId(memberId);
                userPayment.setDate(localDate);


                payments.add(paymentRepository.save(userPayment));
                cartItemRepository.deleteByCart_memberId(memberId);
            }

            return payments;

        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    @Transactional
    public List<Payment> getPaymentDate(Long memberId, int year, int month){
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        return paymentRepository.findByMemberIdAndDateBetween(memberId, startDate, endDate);
    }
    @Transactional
    public List<Payment> getPayment(Long memberId){
        return paymentRepository.findByMemberId(memberId);
    }

}
