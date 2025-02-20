package com.example.AutumnMall.Cart.mapper;

import com.example.AutumnMall.Cart.domain.CartItem;
import com.example.AutumnMall.Cart.dto.AddCartItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(target = "cart", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(target = "id", ignore = true)
    CartItem addCartItemDtoToCartItem(AddCartItemDto addCartItemDto);
}
