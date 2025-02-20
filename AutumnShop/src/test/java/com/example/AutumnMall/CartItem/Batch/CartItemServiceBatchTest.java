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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        cartItemBatchService.batchInsertCartItems(cartItems);
        System.out.println("✅ 1000개 장바구니 상품이 성공적으로 추가되었습니다.");
    }

    @Test
    void 일정기간_지난_장바구니_아이템_삭제_테스트() {
        cartItemBatchService.deleteOldCartItems(30);
        System.out.println("✅ 30일 지난 장바구니 아이템이 삭제되었습니다.");
    }
}
