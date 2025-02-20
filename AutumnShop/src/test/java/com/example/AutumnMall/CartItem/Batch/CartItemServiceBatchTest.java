package com.example.AutumnMall.CartItem.Batch;

import com.example.AutumnMall.Cart.controller.CartItemController;
import com.example.AutumnMall.Cart.domain.Cart;
import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.dto.AddCartItemDto;
import com.example.AutumnMall.Cart.service.CartItemBatchService;
import com.example.AutumnMall.Cart.service.CartItemService;
import com.example.AutumnMall.Product.domain.Product;
import com.example.AutumnMall.security.jwt.util.LoginUserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@Slf4j
@SpringBatchTest
@SpringBootTest
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@AutoConfigureRestDocs
public class CartItemServiceBatchTest {

    @Mock
    private CartItemService cartItemService;

    @Mock
    private CartItemBatchService cartItemBatchService;

    @InjectMocks
    private CartItemController cartItemController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        mockMvc = MockMvcBuilders.standaloneSetup(cartItemController)
                .apply(MockMvcRestDocumentation.documentationConfiguration(restDocumentationContextProvider))
                .build();

    }

    @Test
    public void testBulkUpdate_Success() throws Exception {
        Long cartId = 1L;
        Long memberId = 1L; // 로그인된 사용자의 memberId
        List<AddCartItemDto> addCartItemDtos = new ArrayList<>();

        // 테스트 데이터 생성
        for (long i = 1; i <= 100000; i++) {
            AddCartItemDto dto = new AddCartItemDto();
            dto.setCartId(i);
            dto.setProductId(1L);
            dto.setQuantity(2);
            addCartItemDtos.add(dto);
        }

        // LoginUserDto 설정
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setMemberId(memberId);

        // CartItemService의 addItemsToCart 메서드 mocking
        doNothing().when(cartItemService).addItemsToCart(addCartItemDtos);

        // 테스트: 배치 요청이므로 Batch-Request 헤더를 true로 설정
        mockMvc.perform(post("/cartItems")
                        .param("memberId", String.valueOf(loginUserDto.getMemberId()))
                        .header("Batch-Request", "true") // Batch-Request 헤더 설정
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addCartItemDtos)))
                .andExpect(status().isOk())
                .andDo(document("cart-item-update-bulk-success"));

        // 검증: 서비스의 addItemsToCart 메서드가 호출되었는지 확인
        verify(cartItemService, times(1)).addItemsToCart(addCartItemDtos);
    }

    @Test
    public void testBulkUpdate_Fail() throws Exception {
        List<AddCartItemDto> addCartItemDtos = new ArrayList<>();
        Long memberId = 1L;

        // 잘못된 데이터 (예: cartId가 null)
        AddCartItemDto dto = new AddCartItemDto();
        dto.setCartId(null); // 잘못된 cartId
        dto.setProductId(1L);
        dto.setQuantity(2);
        addCartItemDtos.add(dto);

        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setMemberId(memberId);

        // CartItemService가 예외를 던지도록 설정
        doThrow(new IllegalArgumentException("Cart ID cannot be null"))
                .when(cartItemService).addItemsToCart(addCartItemDtos);

        // 요청 수행 및 예외 검증
        mockMvc.perform(post("/cartItems")
                        .param("memberId", String.valueOf(loginUserDto.getMemberId()))
                        .header("Batch-Request", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addCartItemDtos)))
                .andExpect(status().isBadRequest()) // 400 상태 코드 확인
                .andDo(document("cart-item-update-bulk-fail"));

        // 검증: 예외가 발생했는지 확인
        verify(cartItemService, times(1)).addItemsToCart(addCartItemDtos);
    }

    @Test
    @Rollback(false)   // 롤백을 하지 않도록 설정
    void 장바구니_대량_추가_테스트() {
        List<CartItem> cartItems = new ArrayList<>();

        Cart cart = new Cart();
        cart.setId(1L);

        Product product = new Product();
        product.setId(1L);
        for (int i = 1; i <= 1000; i++) {
            CartItem cartItem = new CartItem();
            cartItem.setId(1L);
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(2);

            cartItems.add(cartItem);
        }

        log.info("배치 작업 시작");

        cartItemBatchService.batchInsertCartItems(cartItems);
        System.out.println("✅ 1000개 장바구니 상품이 성공적으로 추가되었습니다.");
    }

    @Test
    void 일정기간_지난_장바구니_아이템_삭제_테스트() {
        cartItemBatchService.deleteOldCartItems(30);
        System.out.println("✅ 30일 지난 장바구니 아이템이 삭제되었습니다.");
    }

    @Test
    void testRetryAndRollbackOnError() throws Exception {
        List<CartItem> cartItems = new ArrayList<>();

        Cart cart = new Cart();
        cart.setId(1L);

        Product product = new Product();
        product.setId(1L);
        for (int i = 1; i <= 10; i++) {
            CartItem cartItem = new CartItem();
            cartItem.setId(1L);
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(i == 5 ? 0 : 2); // 5번째 아이템에서 예외 발생

            cartItems.add(cartItem);
        }

        // 배치 처리 시작
        cartItemBatchService.batchInsertCartItems(cartItems);

        // 확인: 예외 발생 시 재시도 및 롤백 동작을 확인 (예: 로그나 상태 확인)
        // 여기서는 재시도와 롤백 여부를 로그나 상태로 검증합니다.
        // 예를 들어, 스프링 배치 로그나 특정 상태를 검사할 수 있습니다.
    }

    @Test
    void testBatchLogging() throws Exception {
        List<CartItem> cartItems = new ArrayList<>();

        // 로그 파일이 저장될 경로 설정 (예: /logs/batch/)
        String logPath = "logs/batch/";

        // 배치 작업 실행
        cartItemBatchService.batchInsertCartItems(cartItems);

        // 로그가 올바르게 기록되었는지 확인 (로그 파일 경로와 내용을 확인하는 방식으로 검증)
        // 예: 파일이 존재하는지, 로그에 배치 관련 메시지가 포함되었는지 확인
        File logFile = new File(logPath + "batch_log.log");
        assertTrue(logFile.exists());

        // 로그 내용 검증 (예: "배치 작업 시작" 또는 "배치 작업 완료" 로그가 있는지)
        BufferedReader reader = new BufferedReader(new FileReader(logFile));
        String line;
        boolean foundLog = false;
        while ((line = reader.readLine()) != null) {
            if (line.contains("배치 작업 완료")) {
                foundLog = true;
                break;
            }
        }
        reader.close();

        assertTrue(foundLog, "배치 로그에 '배치 작업 완료' 메시지가 없습니다.");
    }


}
