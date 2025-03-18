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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final Dotenv dotenv = Dotenv.configure().load();

    String restApiKey = dotenv.get("REST_API_KEY");
    String restAPiSecretKey = dotenv.get("REST_API_SECRET_KEY");

    private final PaymentRepository paymentRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private final CustomBeanUtils customBeanUtils;

    private String accessToken = null;
    private long tokenExpiry = 0;  // 만료 시간 (초 단위)

    private String getAccessToken() throws IOException {
        long now = System.currentTimeMillis() / 1000; // 현재 시간 (초 단위)

        // 기존 accessToken이 유효하면 그대로 사용
        if (accessToken != null && now < tokenExpiry) {
            log.info("기존 accessToken 사용: {}", accessToken);
            return accessToken;
        }

        // 새로운 accessToken 요청
        String url = "https://api.iamport.kr/users/getToken";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("imp_key", restApiKey);
        requestBody.put("imp_secret", restAPiSecretKey);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null || responseBody.get("response") == null) {
            log.error("Access Token 발급 실패");
            throw new BusinessLogicException(ExceptionCode.IAMPORT_TOKEN_NOT_FOUND);
        }

        Map<String, Object> tokenResponse = (Map<String, Object>) responseBody.get("response");
        tokenExpiry = ((Integer) tokenResponse.get("expired_at")).longValue(); // 만료 시간 저장

        log.info("새로운 accessToken 발급: {}, 만료시간: {}", accessToken, tokenExpiry);
        return (String) tokenResponse.get("access_token");
    }


    private boolean verifyPayment(String impUid) throws BusinessLogicException, IOException {
        String accessToken = getAccessToken(); // Access Token 발급

        String url = "https://api.iamport.kr/payments/" + impUid; // 결제 조회 API
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken); // 인증 토큰 추가

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (responseBody == null || responseBody.get("response") == null) {
            log.error("결제 정보를 가져올 수 없습니다. impUid: {}", impUid);
            throw new BusinessLogicException(ExceptionCode.IAMPORT_NOT_FOUND);
        }
        Map<String, Object> paymentResponse = (Map<String, Object>) responseBody.get("response");

        // 결제 중복 여부 확인
        boolean isDuplicate = paymentRepository.existsByImpuid(impUid);
        if (isDuplicate) {
            log.error("이미 처리된 결제입니다. impUid: {}", impUid);
            throw new BusinessLogicException(ExceptionCode.PAYMENT_ALREADY_PAID);
        }

        // 결제 상태 확인
        String status = (String) paymentResponse.get("status");
        if (!"paid".equals(status)) {
            log.error("결제 검증 실패. 상태: {}, impUid: {}", status, impUid);
            throw new BusinessLogicException(ExceptionCode.INVALID_PAYMENT_STATUS);
        }

        return true;
    }

    @Transactional
    public List<Payment> addPayment(Long memberId, Long cartId, Long orderId, List<Integer> quantities,
                    String impuid){
        log.info("회원 ID {}의 결제 시작. 주문 ID: {}", memberId, orderId);  // 결제 시작 로그

        try {
            Member member = memberRepository.findByMemberId(memberId)
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

            if(!verifyPayment(impuid)){
                log.error("유효하지 않은 결제입니다.");
                return (List<Payment>) new BusinessLogicException(ExceptionCode.INVALID_PAYMENT_STATUS);
            }

            List<Payment> payments = new ArrayList<>();
            LocalDate localDate = LocalDate.now();

            Iterator<CartItem> iterator = cartItems.iterator();
            Iterator<Integer> quantityIterator = quantities.iterator();
            while (iterator.hasNext()) {
                CartItem cartItem = iterator.next();
                int quantity = quantityIterator.next();

                Product product = productRepository.findByIdWithLock(cartItem.getProduct().getId())
                        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.PRODUCT_NOT_FOUND));

                if(product.getRating().getCount() < quantity){
                    log.error("구매 수량 {}가 잔여 수량보다 많습니다. 상품: {}", quantity, product.getTitle());  // 오류 로그
                    throw new BusinessLogicException(ExceptionCode.INVALID_PAYMENT_STATUS);
                }

                product.getRating().setCount(product.getRating().getCount() - quantity);
                productRepository.save(product);

                // 먼저 customBeanUtils로 필드를 복사 ( price, title )
                Payment userPayment = new Payment();
                customBeanUtils.copyProperties(product, userPayment);

                userPayment = userPayment.toBuilder()
                        .id(null)  // Id는 null로 설정
                        .product(product)
                        .quantity(quantity)
                        .date(localDate)
                        .impuid(impuid)
                        .status("paid")
                        .build();

                // member와 order는 set 메서드로 설정
                userPayment.setMember(member);
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

    @Transactional
    public List<Payment> getAllPayment(){
        return paymentRepository.findAll();
    }
}
