package com.example.AutumnMall.CartItem.service;

import com.example.AutumnMall.Cart.controller.CartItemController;
import com.example.AutumnMall.Cart.dto.AddCartItemDto;
import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.dto.ResponseGetCartItemDto;
import com.example.AutumnMall.Cart.service.CartItemService;
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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Slf4j
@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
@AutoConfigureRestDocs
public class CartItemServiceTest {

    @Mock
    private CartItemService cartItemService;

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
    public void testAddCartItem_Success() throws Exception {
        Long memberId = 1L;

        // 준비: AddCartItemDto와 CartItem 객체 설정
        AddCartItemDto addCartItemDto = new AddCartItemDto();
        addCartItemDto.setCartId(1L);
        addCartItemDto.setProductId(1L);
        addCartItemDto.setQuantity(2);

        List<AddCartItemDto> addCartItemDtos = new ArrayList<>();
        addCartItemDtos.add(addCartItemDto);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setQuantity(2);

        // LoginUserDto 설정
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setMemberId(memberId);

        // Mocking: cartItemService.addCartItem 메서드 호출시 CartItem 반환하도록 설정
        when(cartItemService.addCartItem(any(AddCartItemDto.class))).thenReturn(cartItem);

        // 테스트: addCartItem 엔드포인트 호출
        mockMvc.perform(post("/cartItems")
                        .param("memberId", String.valueOf(loginUserDto.getMemberId()))
                        .header("Batch-Request", "false") // Batch-Request 헤더 설정
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(addCartItemDtos)))
                .andExpect(status().isOk())
                .andDo(document("add-cart-item-success"));

        // 검증: 서비스 메서드가 한 번 호출되었는지 확인
        verify(cartItemService, times(1)).addCartItem(any(AddCartItemDto.class));
    }

    @Test
    public void testAddCartItem_CartNotFound() throws Exception {
        // 준비: AddCartItemDto 객체 설정
        AddCartItemDto addCartItemDto = new AddCartItemDto();
        addCartItemDto.setCartId(1L);
        addCartItemDto.setProductId(1L);
        addCartItemDto.setQuantity(2);

        // Mocking: cartItemService.addCartItem 메서드가 BusinessLogicException을 던지도록 설정
        when(cartItemService.addCartItem(any(AddCartItemDto.class)))
                .thenThrow(new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

        // 테스트: addCartItem 엔드포인트 호출
        mockMvc.perform(post("/cartItems")
                        .contentType("application/json")
                        .content("{ \"cartId\": 1, \"productId\": 1, \"quantity\": 2 }"))
                .andExpect(status().isBadRequest());

        // 검증: 서비스 메서드가 한 번 호출되었는지 확인
        verify(cartItemService, times(1)).addCartItem(any(AddCartItemDto.class));
    }

    @Test
    public void testGetCartItems_Success() throws Exception {
        // 준비: 로그인 유저와 cartId 설정
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setMemberId(1L);
        Long cartId = 1L;

        // ResponseGetCartItemDto 준비
        ResponseGetCartItemDto cartItemDto = new ResponseGetCartItemDto();
        cartItemDto.setId(1L);
        cartItemDto.setProductId(1L);
        cartItemDto.setTitle("Sample Product");
        cartItemDto.setPrice(100.0);
        cartItemDto.setDescription("This is a sample product.");
        cartItemDto.setImageUrl("http://example.com/sample-product.jpg");
        cartItemDto.setQuantity(2);

        List<ResponseGetCartItemDto> cartItems = List.of(cartItemDto);

        // Mocking: cartItemService.getCartItems 메서드 호출 시 cartItems 반환하도록 설정
        when(cartItemService.getCartItems(loginUserDto.getMemberId(), cartId)).thenReturn(cartItems);

        // 테스트: getCartItems 엔드포인트 호출
        mockMvc.perform(get("/cartItems")
                        .contentType("application/json")
                        .param("cartId", String.valueOf(cartId))
                        .param("memberId", String.valueOf(loginUserDto.getMemberId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].productId").value(1L))
                .andExpect(jsonPath("$[0].title").value("Sample Product"))
                .andExpect(jsonPath("$[0].price").value(100.0))
                .andExpect(jsonPath("$[0].description").value("This is a sample product."))
                .andExpect(jsonPath("$[0].imageUrl").value("http://example.com/sample-product.jpg"))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andDo(document("get-cart-items-success"));

        // 검증: 서비스 메서드가 한 번 호출되었는지 확인
        verify(cartItemService, times(1)).getCartItems(loginUserDto.getMemberId(), cartId);
    }


    @Test
    public void testGetCartItems_CartNotFound() throws Exception {
        // 준비: 로그인 유저와 cartId 설정
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setMemberId(1L);

        // Mocking: cartItemService.getCartItems 메서드가 BusinessLogicException을 던지도록 설정
        when(cartItemService.getCartItems(any(Long.class), any(Long.class)))
                .thenThrow(new BusinessLogicException(ExceptionCode.CART_NOT_FOUND));

        // 테스트: getCartItems 엔드포인트 호출
        mockMvc.perform(get("/cartItems")
                        .contentType("application/json")
                        .param("cartId", "1")
                        .param("memberId", String.valueOf(loginUserDto.getMemberId())))
                .andExpect(status().isBadRequest()) // HTTP 상태 코드 400 (Bad Request) 기대
                .andExpect(jsonPath("$.message").value("Cart not found")) // 예외 메시지 확인
                .andDo(document("get-cart-items-cart-not-found"));

        // 검증: 서비스 메서드가 한 번 호출되었는지 확인
        verify(cartItemService, times(1)).getCartItems(any(Long.class), any(Long.class));
    }


    @Test
    public void testDeleteCartItem_Success() throws Exception {
        // 준비: 로그인 유저 설정 및 cartItemId
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setMemberId(1L);
        Long cartItemId = 1L;

        // Mocking: cartItemService.isCartItemExist 메서드가 true 반환하도록 설정
        when(cartItemService.isCartItemExist(cartItemId, loginUserDto.getMemberId())).thenReturn(true);

        // 테스트: deleteCartItem 엔드포인트 호출
        mockMvc.perform(delete("/cartItems/{cartItemId}", cartItemId)
                        .contentType("application/json")
                        .param("memberId", String.valueOf(loginUserDto.getMemberId()))
                        .param("cartItemId", String.valueOf(cartItemId)))
                .andExpect(status().isOk()) // 정상적으로 처리되면 200 OK 상태 코드
                .andDo(document("delete-cart-item-success"));

        // 검증: 서비스 메서드가 한 번 호출되었는지 확인
        verify(cartItemService, times(1)).deleteCartItem(cartItemId);
    }

    @Test
    public void testDeleteCartItem_CartItemNotFound() throws Exception {
        // 준비: 로그인 유저 설정 및 cartItemId
        LoginUserDto loginUserDto = new LoginUserDto();
        loginUserDto.setMemberId(1L);
        Long cartItemId = 1L;

        // Mocking: cartItemService.isCartItemExist 메서드가 false 반환하도록 설정
        when(cartItemService.isCartItemExist(cartItemId, loginUserDto.getMemberId())).thenReturn(false);

        // 테스트: deleteCartItem 엔드포인트 호출
        mockMvc.perform(delete("/cartItems/{cartItemId}", cartItemId)
                        .contentType("application/json")
                        .param("memberId", String.valueOf(loginUserDto.getMemberId()))
                        .param("cartItemId", String.valueOf(cartItemId)))
                .andExpect(status().isBadRequest()) // 장바구니 아이템이 없을 경우 400 Bad Request
                .andExpect(jsonPath("$.message").value("Cart item not found")) // 예외 메시지 검증
                .andDo(document("delete-cart-item-cart-item-not-found"));

        // 검증: 서비스 메서드가 호출되지 않았는지 확인
        verify(cartItemService, times(0)).deleteCartItem(cartItemId);
    }
}
