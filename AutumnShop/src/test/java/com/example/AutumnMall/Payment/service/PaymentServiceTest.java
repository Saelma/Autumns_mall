package com.example.AutumnMall.Payment.service;

import com.example.AutumnMall.Payment.controller.PaymentController;
import com.example.AutumnMall.Payment.domain.Payment;
import com.example.AutumnMall.Payment.dto.AddPaymentDto;
import com.example.AutumnMall.Payment.service.PaymentService;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ActiveProfiles("test")
@Slf4j
@SpringBatchTest
@SpringBootTest
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
        Long memberId = 1L;
        Long cartId = 1L;
        Long orderId = 1L;
        String impuid = "imp53071323";
        List<Integer> quantities = List.of(1, 2);

        // AddPaymentDto 설정
        AddPaymentDto addPaymentDto = new AddPaymentDto();
        addPaymentDto.setCartId(cartId);
        addPaymentDto.setOrderId(orderId);
        addPaymentDto.setQuantity(quantities);
        addPaymentDto.setImpuid(impuid);

        // Payment 객체 설정
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setPrice((double) 100L);
        payment.setImpuid("imp53071323");

        // LoginUserDto 설정
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setMemberId(memberId);

        // Mocking: paymentService.addPayment 메서드 호출 시 Payment 객체 반환
        when(paymentService.addPayment(memberId, cartId, orderId, quantities, impuid))
                .thenReturn(List.of(payment));

        // 테스트: 결제 추가 엔드포인트 호출
        mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addPaymentDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(document("add-payment-success"));

        // 검증: 서비스 메서드가 한 번 호출되었는지 확인
        verify(paymentService, times(1))
                .addPayment(memberId, cartId, orderId, quantities, impuid);
    }
}
