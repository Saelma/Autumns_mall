package com.example.AutumnMall.Payment.service;

import com.example.AutumnMall.Payment.controller.PaymentController;
import com.example.AutumnMall.Payment.domain.Payment;
import com.example.AutumnMall.Payment.dto.AddPaymentDto;
import com.example.AutumnMall.exception.BusinessLogicException;
import com.example.AutumnMall.exception.ExceptionCode;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@AutoConfigureRestDocs
public class PaymentServiceTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentationContextProvider))
                .build();
    }

    @Test
    public void testAddPayment_Success() throws Exception {
        Long memberId = 31L;
        Long cartId = 7L;
        Long orderId = 161L;
        String impuid = "imp53071323";
        List<Integer> quantities = List.of(1, 2);

        // 요청 DTO 설정
        AddPaymentDto addPaymentDto = new AddPaymentDto();
        addPaymentDto.setCartId(cartId);
        addPaymentDto.setOrderId(orderId);
        addPaymentDto.setQuantity(quantities);
        addPaymentDto.setImpuid(impuid);

        // 응답 Payment 객체 설정
        Payment payment = new Payment();
        payment.setId(1L);
        payment.getProduct().setPrice(100.0);
        payment.setImpuid(impuid);

        // LoginUserDto 설정
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setMemberId(memberId);

        // Mock 설정
        when(paymentService.addPayment(memberId, cartId, orderId, quantities, impuid))
                .thenReturn(List.of(payment));

        // When & Then (테스트 실행 및 검증)
        mockMvc.perform(post("/payment")
                        .param("memberId", String.valueOf(loginUserDto.getMemberId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addPaymentDto)))
                .andExpect(status().isOk())
                .andDo(document("add-payment-success"));

        // 서비스가 1회 호출되었는지 확인
        verify(paymentService, times(1))
                .addPayment(memberId, cartId, orderId, quantities, impuid);
    }

    @Test
    public void testAddPayment_FAILED_INVALID_IMPUID() throws Exception {
        Long memberId = 31L;
        Long cartId = 7L;
        Long orderId = 161L;
        String impuid = "imp266394620868";
        List<Integer> quantities = List.of(1, 2);

        // 요청 DTO 설정
        AddPaymentDto addPaymentDto = new AddPaymentDto();
        addPaymentDto.setCartId(cartId);
        addPaymentDto.setOrderId(orderId);
        addPaymentDto.setQuantity(quantities);
        addPaymentDto.setImpuid(impuid);

        // 응답 Payment 객체 설정
        Payment payment = new Payment();
        payment.setId(1L);
        payment.getProduct().setPrice(100.0);
        payment.setImpuid(impuid);

        // LoginUserDto 설정
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setMemberId(memberId);

        // 잘못된 impUid에 대한 Mock 설정 (PAYMENT_ALREADY_PAID 예외 처리)
        when(paymentService.addPayment(memberId, cartId, orderId, quantities, impuid))
                .thenThrow(new BusinessLogicException(ExceptionCode.PAYMENT_ALREADY_PAID));

        // When & Then (테스트 실행 및 검증)
        mockMvc.perform(post("/payment")
                        .param("memberId", String.valueOf(loginUserDto.getMemberId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addPaymentDto)))
                .andExpect(status().isBadRequest())  // 400 상태 코드 예상
                .andExpect(jsonPath("$.message").value("이미 처리된 결제입니다."))
                .andDo(document("add-payment-invalid-impuid"));

        // 서비스 메서드가 1회 호출되었는지 확인
        verify(paymentService, times(1)) // 1회 호출되는지 확인
                .addPayment(memberId, cartId, orderId, quantities, impuid);
    }

}
