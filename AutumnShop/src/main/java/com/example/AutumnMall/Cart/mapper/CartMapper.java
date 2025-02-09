package com.example.AutumnMall.Cart.mapper;

import com.example.AutumnMall.Cart.domain.Cart;
import com.example.AutumnMall.Cart.dto.AddCartDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public abstract class CartMapper {
    // AddCartDto -> Cart 엔티티로 매핑
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "member", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "modifiedAt", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "cartItems", ignore = true) // CartItems는 별도로 처리
    public abstract Cart addCartDtoToCart(AddCartDto addCartDto);
}
