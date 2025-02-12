package com.example.AutumnMall.Payment.service;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.repository.CartItemRepository;
import com.example.AutumnMall.Member.domain.Member;
import com.example.AutumnMall.Member.repository.MemberRepository;
import com.example.AutumnMall.Payment.domain.Order;
import com.example.AutumnMall.Payment.domain.Payment;
import com.example.AutumnMall.Payment.repository.OrderRepository;
import com.example.AutumnMall.Payment.repository.PaymentRepository;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.Product.repository.ProductRepository;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import com.example.AutumnMall.utils.CustomBean.CustomBeanUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    private final CustomBeanUtils customBeanUtils;

    // 추후 Builder를 통해 리팩토링 할 예정
    @Transactional
    public List<Payment> addPayment(Long memberId, Long cartId, Long orderId, List<Integer> quantities){
        log.info("회원 ID {}의 결제 시작. 주문 ID: {}", memberId, orderId);  // 결제 시작 로그

        try {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", + memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });

            List<CartItem> cartItems = cartItemRepository.findByCart_IdAndCart_Member(cartId, member);
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.error("주문 ID {}를 찾을 수 없습니다.", orderId);  // 오류 로그
                        return new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
                    });

            List<Payment> payments = new ArrayList<>();
            LocalDate localDate = LocalDate.now();

            Iterator<CartItem> iterator = cartItems.iterator();
            Iterator<Integer> quantityIterator = quantities.iterator();
            while (iterator.hasNext()) {
                CartItem cartItem = iterator.next();
                int quantity = quantityIterator.next();

                Product product = cartItem.getProduct();
                System.out.println(product.getPrice());


                if(product.getRating().getCount() < quantity){
                    log.error("구매 수량 {}가 잔여 수량보다 많습니다. 상품: {}", quantity, product.getTitle());  // 오류 로그
                    throw new BusinessLogicException(ExceptionCode.INVALID_PAYMENT_STATUS);
                }

                product.getRating().setCount(product.getRating().getCount() - quantity);
                productRepository.save(product);

                // payment와 product의 같은 속성만 그대로 복사 ( price, title )
                Payment userPayment = new Payment();
                customBeanUtils.copyProperties(product, userPayment);
                userPayment.setId(null);
                userPayment.setProductId(product.getId());
                userPayment.setProductRate(product.getRating().getRate());
                userPayment.setQuantity(quantity);
                userPayment.setMember(member);
                userPayment.setDate(localDate);
                userPayment.setOrder(order);

                payments.add(paymentRepository.save(userPayment));
                log.info("주문 ID {}에 대한 결제 처리 완료. 상품: {}", orderId, product.getTitle());  // 결제 처리 완료 로그
            }

            // 장바구니 비우기
            cartItemRepository.deleteByCart_member(member);
            log.info("회원 ID {}의 결제 완료 후 장바구니 비움", memberId);  // 장바구니 비우기 로그

            return payments;

        } catch (BusinessLogicException e) {
            log.error("결제 처리 중 오류 발생 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        } catch (Exception e) {
            log.error("결제 처리 중 오류 발생 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Page<Payment> getPaymentDate(Long memberId, int year, int month, int page, int size){
        try {
            log.info("회원 ID {}의 결제 내역 조회. 기간: {}-{}", memberId, year, month);  // 결제 내역 조회 로그

            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.plusMonths(1).minusDays(1);
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", + memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });

            return paymentRepository.findByMemberAndDateBetween(member, startDate, endDate, PageRequest.of(page, size));
        } catch (BusinessLogicException e) {
            log.error("날짜에 따른 결제내역 조회 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        } catch (Exception e) {
            log.error("날짜에 따른 결제내역 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public List<Payment> getPayment(Long memberId){
        try {
            log.info("회원 ID {}의 모든 결제 내역 조회", memberId);  // 모든 결제 내역 조회 로그

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", + memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });

            return paymentRepository.findByMember(member);
        } catch (BusinessLogicException e) {
            log.error("모든 결제내역 조회 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        } catch (Exception e) {
            log.error("모든 결제내역 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public Page<Payment> getPaymentPage(Long memberId, int page, int size){
        try {
            log.info("회원 ID {}의 결제 내역 페이징 조회. 페이지: {}, 크기: {}", memberId, page, size);  // 페이징 결제 내역 조회 로그

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", + memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });

            return paymentRepository.findAllByMember(member, PageRequest.of(page, size));
        } catch (BusinessLogicException e) {
            log.error("결제내역 페이징 조회 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        } catch (Exception e) {
            log.error("결제내역 페이징 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public List<Payment> getOrderPayment(Long orderId){
        try {
            log.info("주문 ID {}의 결제 내역 조회", orderId);  // 주문 결제 내역 조회 로그

            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> {
                        log.error("주문 ID {}를 찾을 수 없습니다.", orderId);  // 오류 로그
                        return new BusinessLogicException(ExceptionCode.ORDER_NOT_FOUND);
                    });

            return paymentRepository.findByOrderId(order.getId());
        } catch (BusinessLogicException e) {
            log.error("주문 ID의 결제 내역 조회 실패 : {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        } catch (Exception e) {
            log.error("주문 ID의 결제 내역 조회 실패 (예상치 못한 예외): {}", e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    // 상품평(ReviewController)에서 해당 멤버가 물품을 구매한 적 있는지 체크하기 위해 사용됨
    @Transactional
    public boolean purchasedProduct(Long memberId, Long productId){
        try {
            log.info("회원 ID {}가 상품 ID {}를 구매했는지 확인", memberId, productId);  // 구매 여부 체크 로그

            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> {
                        log.error("회원이 존재하지 않습니다. 회원Id: {}", + memberId);
                        return new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND);
                    });

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> {
                        log.error("물품이 존재하지 않습니다. 물품Id: {}", productId);
                        return new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND);
                    });

            boolean result = paymentRepository.existsByMemberAndProductId(member, product.getId());
            log.info("회원 ID {}의 상품 ID {} 구매 여부: {}", memberId, productId, result);  // 구매 여부 결과 로그
            return result;
        } catch (BusinessLogicException e) {
            log.error("회원 ID {}의 상품 ID {} 구매 여부 확인 실패 : {}", memberId, productId, e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.PAYMENT_NOT_FOUND);
        } catch (Exception e) {
            log.error("회원 ID {}의 상품 ID {} 구매 여부 확인 실패 : (예상치 못한 예외) {} ", memberId, productId, e.getMessage(), e);
            throw new BusinessLogicException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }
}
